package org.foxteam.wbgrab;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class WbGrabHost implements IStatusProvider, org.foxteam.wbgrab.IWbGrabber {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTIONS_WEIBOS = "weibos";
    private static final String COLLECTIONS_TOPICS = "topics";
    private static final String COLLECTIONS_ACCOUNTS = "accounts";
    public static final String FIELD_TOPIC_ID = "topic_tag_id";
    private static final String FIELD_TOPIC = "topic";
    public static final String FIELD_ANALYSIS_RESULT = "analysis_result";

    private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
    private static Set<String> dateFields = new HashSet<String>();

    private MongoClient mongoClient = null;
    private DB db = null;
    private DBCollection users;
    private DBCollection weibos;
    private DBCollection topics;
    private DBCollection accounts;

    public WbGrabHost() {
        dateFields.add("created_at");
    }

    @Override
    public void Init() throws GrabberException {
        try {
            mongoClient = new MongoClient(GrabConfig.getValue("MongoHost"));
        } catch (UnknownHostException e) {
            throw new GrabberException("Cannot resolve database address", e);
        }

        initDatabase();
    }

    private void initDatabase() throws GrabberException {
        String dbName = GrabConfig.getValue("DatabaseName");
        db = mongoClient.getDB(dbName);
        if (!GrabConfig.getValue("UserName").isEmpty()) {
            String pwd = GrabConfig.getValue("Password");
            if (!db.authenticate(GrabConfig.getValue("UserName"), pwd.toCharArray()))
                return;
        }
        accounts = db.getCollection(COLLECTIONS_ACCOUNTS);
        users = db.getCollection(COLLECTION_USERS);
        weibos = db.getCollection(COLLECTIONS_WEIBOS);
        topics = db.getCollection(COLLECTIONS_TOPICS);
        try {
            users.ensureIndex(new BasicDBObject("id", 1), "id_1", true);
            weibos.ensureIndex(new BasicDBObject("id", 1), "id_1", false);
            weibos.ensureIndex(new BasicDBObject(FIELD_TOPIC_ID, 1), "topic_tag_1", false);
            topics.ensureIndex(new BasicDBObject(FIELD_TOPIC_ID, 1), "tag_id_1", true);
        } catch (MongoException e) {
            throw new GrabberException("Cannot create index", e);
        }
    }

    @Override
    public void resetDatabase() throws GrabberException {
        mongoClient.dropDatabase(GrabConfig.getValue("DatabaseName"));
        initDatabase();
    }

    @Override
    public void clearDatabase() {
        accounts.remove(new BasicDBObject());
        users.remove(new BasicDBObject());
        weibos.remove(new BasicDBObject());
        topics.remove(new BasicDBObject());
    }

    public DBCollection getWeibos() {
        return weibos;
    }

    protected static Date parseDate(String str, String format) throws ParseException {
        if (str == null || "".equals(str)) {
            return null;
        }
        SimpleDateFormat sdf = formatMap.get(format);
        if (null == sdf) {
            sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            formatMap.put(format, sdf);
        }
        synchronized (sdf) {
            // SimpleDateFormat is not thread safe
            return sdf.parse(str);
        }
    }

    public static Object JSON2DB(Object json) {
        if (json instanceof JSONObject) {
            return JSON2DBObject((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return JSON2DBList((JSONArray) json);
        } else if (json instanceof BigDecimal) {
            return ((BigDecimal) json).doubleValue();
        } else if (json == null) {
            return null;
        } else {
            return json;
        }
    }

    public static BasicDBList JSON2DBList(JSONArray json) {
        BasicDBList result = new BasicDBList();
        for (Object o : json) {
            result.add(JSON2DB(o));
        }
        return result;
    }

    public static BasicDBObject JSON2DBObject(JSONObject json) {
        BasicDBObject result = new BasicDBObject();
        for (String key : json.keySet()) {
            Object o = json.get(key);
            if (dateFields.contains(key) && o instanceof String) {
                try {
                    result.put(key, parseDate((String) o, "EEE MMM d HH:mm:ss z yyyy"));
                    continue;
                } catch (ParseException e) {
                    // Fallback to normal routine
                }
            }
            result.put(key, JSON2DB(o));
        }
        return result;
    }

    public DBObject insertUser(JSONObject user) throws GrabberException {
        long id = 0;
        try {
            id = user.getLong("id");
        } catch (JSONException e) {
            throw new GrabberException("User's ID not found in response", e);
        }
        BasicDBObject dbo = JSON2DBObject(user);

        return users.findAndModify(
                new BasicDBObject("id", id),    //query
                null,   // fields
                null,   // sort
                false,  // remove
                new BasicDBObject("$set", dbo), // update
                true,   // return new
                true    // upsert
        );
    }

    public DBObject insertStatus(JSONObject status, int topic_tag) throws GrabberException {
        long id = 0;
        try {
            id = status.getLong("id");
        } catch (JSONException e) {
            throw new GrabberException("Status' ID not found in response",e);
        }
        if (status.containsKey("user")) {
            DBObject usr = null;
            try {
                usr = insertUser(status.getJSONObject("user"));
                status.remove("user");
                status.put("user", new DBRef(db, COLLECTION_USERS, usr.get("_id")));
            } catch (JSONException e) {
                //log.info("Impossible reached!", e);
                throw new GrabberException(e);
            }
        }
        BasicDBObject dbo = JSON2DBObject(status);
        dbo.put(FIELD_TOPIC_ID, topic_tag);
        return weibos.findAndModify(
                new BasicDBObject("id", id),    //query
                null,   // fields
                null,   // sort
                false,  // remove
                new BasicDBObject("$set", dbo), // update
                true,   // return new
                true    // upsert
        );
    }

    public void insertStatuses(JSONObject statuses, int topic_tag) throws GrabberException {
        JSONArray statusArray = null;
        try {
            statusArray = statuses.getJSONArray("statuses");
        } catch (JSONException e) {
            throw new GrabberException("No status found in response", e);
        }
        for (int i = 0; i < statusArray.size(); i++) {
            try {
                insertStatus(statusArray.getJSONObject(i), topic_tag);
            } catch (JSONException e) {
                //log.info("Impossible reached!", e);
            }
        }
    }

    @Override
    public int getTopicTag(String topic) {
        int hashResult = 0;
        boolean done = false;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(topic.getBytes());
            byte[] result = messageDigest.digest();
            hashResult = (result[0] & 0xff) << 24
                    | (result[1] & 0xff) << 16
                    | (result[2] & 0xff) << 8
                    | (result[3] & 0xff);
            hashResult = result[0] & 0x3f;
        } catch (NoSuchAlgorithmException e) {
            // Impossible
        }
        BasicDBObject dbo = new BasicDBObject(FIELD_TOPIC, topic);
        BasicDBObject o = new BasicDBObject("$set", new BasicDBObject());
        while (!done) {
            dbo.put(FIELD_TOPIC_ID, hashResult);
            try {
                topics.update(dbo, o, true, false);
                done = true;
            } catch (MongoException.DuplicateKey e) {
                ++hashResult;
                continue;
            }
            done = true;
        }
        return hashResult;
    }

    @Override
    public Iterable<IStatus> getStatus(int topic_tag, boolean unAnalyzedOnly) {
        return new StatusIterator(weibos, topic_tag, unAnalyzedOnly);
    }

    public WbAccount newAccount(String username, String password) {
        return WbAccount.newAccount(accounts, username, password);
    }

    public WbAccount findAccount() {
        DBObject candidate = accounts.findOne(
                new BasicDBObject(WbAccount.FIELD_REST_TIME,
                        new BasicDBObject("$lte", new Date()))
        );
        if (candidate != null) {
            return new WbAccount(accounts, candidate);
        }
        candidate = accounts.findOne(
                new BasicDBObject(WbAccount.FIELD_USER_LIMIT_REMAINING,
                        new BasicDBObject("$gt", 0)),
                null,
                new BasicDBObject(WbAccount.FIELD_USER_LIMIT_REMAINING, -1)
        );
        if (candidate != null) {
            return new WbAccount(accounts, candidate);
        }
        return null;
    }

}
