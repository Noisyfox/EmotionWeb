package org.foxteam.wbgrab;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.Date;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class WbAccount {
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_USER_LIMIT = "user_limit";
    public static final String FIELD_USER_LIMIT_REMAINING = "user_limit_remaining";
    public static final String FIELD_REST_TIME = "reset_time";

    private Object objectID;
    public String username;
    public String password;
    public long user_limit;
    public long user_limit_remaining;
    public Date reset_time;
    private DBCollection accounts;

    public WbAccount(DBCollection accounts, DBObject dbo) {
        this.accounts = accounts;
        update(dbo);
    }

    private void update(DBObject dbo) {
        objectID = dbo.get("_id");
        username = (String) dbo.get(FIELD_USERNAME);
        password = (String) dbo.get(FIELD_PASSWORD);
        user_limit = (Long) dbo.get(FIELD_USER_LIMIT);
        user_limit_remaining = (Long) dbo.get(FIELD_USER_LIMIT_REMAINING);
        reset_time = (Date) dbo.get(FIELD_REST_TIME);
    }

//    public void update(RateLimitStatus status) {
//        user_limit = status.getUserLimit();
//        user_limit_remaining = status.getRemainingUserHits();
//        reset_time.setTime(new Date().getTime() + 1000 * status.getResetTimeInSeconds());
//        save();
//    }

    public void save() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.append("_id", objectID);
        dbo.append(FIELD_USERNAME, username);
        dbo.append(FIELD_PASSWORD, password);
        dbo.append(FIELD_USER_LIMIT, user_limit);
        dbo.append(FIELD_USER_LIMIT_REMAINING, user_limit_remaining);
        dbo.append(FIELD_REST_TIME, reset_time);
        accounts.save(dbo);
    }

    public long decrease() {
        DBObject dbo = accounts.findAndModify(
                new BasicDBObject("_id", objectID),
                null,
                null,
                false,
                new BasicDBObject("$inc", new BasicDBObject(FIELD_USER_LIMIT_REMAINING, -1)),
                true,
                false
        );
        update(dbo);
        return user_limit_remaining;
    }

    public static WbAccount newAccount(DBCollection accounts, String username, String password) {
        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append(FIELD_USERNAME, username);
        setQuery.append(FIELD_PASSWORD, password);
        BasicDBObject setOnInsertQuery = new BasicDBObject();
        setOnInsertQuery.append(FIELD_REST_TIME, new Date());
        setOnInsertQuery.append(FIELD_USER_LIMIT, 0L);
        setOnInsertQuery.append(FIELD_USER_LIMIT_REMAINING, 0L);
        BasicDBObject update = new BasicDBObject();
        update.append("$set", setQuery);
        update.append("$setOnInsert", setOnInsertQuery);
        DBObject newDocument = accounts.findAndModify(
                new BasicDBObject(FIELD_USERNAME, username),    // query
                null,   // fields
                null,   // sort
                false,  // remove
                update, // update
                true,   // return new
                true    // upsert
        );
        return new WbAccount(accounts, newDocument);
    }

    @Override
    public String toString() {
        return "WbAccount[" + username + "]";
    }

}
