package org.foxteam.wbgrab;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.foxteam.wbgrab.restapi.AccountLogin;
import org.foxteam.wbgrab.restapi.RestException;
import org.foxteam.wbgrab.restapi.SearchStatus;
import org.foxteam.wbgrab.restapi.WbLoginSession;

import java.util.Date;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class WbGrabWorker extends Thread {
    private int topic_tag;
    private WbGrabHost wbGrabHost;
    private String keyword;
    private int entityLimit;

    private static final int PAGE_LIMIT = 20;

    public WbGrabWorker(WbGrabHost wbGrabHost, String keyword, int entityLimit) {
        this.wbGrabHost = wbGrabHost;
        this.keyword = keyword;
        this.entityLimit = entityLimit;
        topic_tag = wbGrabHost.getTopicTag(keyword);
    }

    private int count() {
        DBObject query = new BasicDBObject(WbGrabHost.FIELD_TOPIC_ID, topic_tag);
        DBCursor cursor = wbGrabHost.getWeibos().find(query);
        return cursor.count();
    }

    @Override
    public void run() {
        try {
            while (count() < entityLimit) {
                WbAccount wbAccount = wbGrabHost.findAccount();
                WbLoginSession wbSession;
                if (wbAccount == null) {
                    Thread.sleep(60 * 1000);
                    continue;
                }

                try {
                    AccountLogin accountLogin = new AccountLogin();
                    wbSession = accountLogin.login(wbAccount.username, wbAccount.password);
                } catch (RestException e) {
                    wbAccount.reset_time.setTime(new Date().getTime() + 1000 * 60 * 15);
                    wbAccount.save();
                    Thread.sleep(1000 * 5);
                    continue;
                }

                SearchStatus search = new SearchStatus();
                search.setSession(wbSession);
                int page_done = 0;
                while (count() < entityLimit && page_done < PAGE_LIMIT) {
                    try {
                        JSONObject result = search.searchStatus(keyword, page_done, 50);
                        wbGrabHost.insertStatuses(result, topic_tag);
                        page_done++;
                    } catch (RestException e) {
                        break;
                    } catch (GrabberException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
