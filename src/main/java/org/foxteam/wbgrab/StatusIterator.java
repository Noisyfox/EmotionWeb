package org.foxteam.wbgrab;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.Iterator;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class StatusIterator implements Iterable<IStatus>, Iterator<IStatus> {
    private DBCursor cursor;
    private boolean unAnalyzedOnly;
    private int topic_tag;
    private DBCollection weibos;

    public StatusIterator(DBCollection weibos, int topic_tag, boolean unAnalyzedOnly) {
        this.weibos = weibos;
        this.topic_tag = topic_tag;
        this.unAnalyzedOnly = unAnalyzedOnly;
        BasicDBObject query = new BasicDBObject(WbGrabHost.FIELD_TOPIC_ID, topic_tag);
        if (unAnalyzedOnly)
            query.put(WbGrabHost.FIELD_ANALYSIS_RESULT, null);
        cursor = weibos.find(query);
    }

    @Override
    public Iterator<IStatus> iterator() {
        return new StatusIterator(weibos, topic_tag, unAnalyzedOnly);
    }

    @Override
    public boolean hasNext() {
        return cursor.hasNext();
    }

    @Override
    public IStatus next() {
        if (cursor.hasNext()) {
            return new Status(weibos, cursor.next());
        } else {
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
