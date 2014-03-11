package org.foxteam.wbgrab;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class Status implements IStatus {
    private DBCollection weibos;
    private DBObject dbObj;

    public Status(DBCollection weibos, DBObject dbObj) {
        this.weibos = weibos;
        this.dbObj = dbObj;
    }

    @Override
    public String getText() {
        Object o = dbObj.get("text");
        if (o instanceof String) {
            return (String) o;
        } else {
            return null;
        }
    }

    public DBObject getUser() {
        DBRef userRef = (DBRef) dbObj.get("user");
        return userRef.fetch();
    }

    @Override
    public DBObject getAnalyzeResult() {
        Object o = dbObj.get(WbGrabHost.FIELD_ANALYSIS_RESULT);
        if (o instanceof DBObject) {
            return (DBObject) o;
        } else {
            return null;
        }
    }

    @Override
    public void setAnalyzeResult(DBObject analyzeResult) {
        dbObj.put(WbGrabHost.FIELD_ANALYSIS_RESULT, analyzeResult);
    }

    @Override
    public void save() {
        weibos.save(dbObj);
    }

}
