package org.foxteam.wbgrab;

import com.mongodb.DBObject;

/**
 * Created by YeahO_O on 3/8/14.
 */
public interface IStatus {
    String getText();

    DBObject getAnalyzeResult();

    void setAnalyzeResult(DBObject analyzeResult);

    void save();
}
