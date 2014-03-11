package org.foxteam.wbgrab.restapi;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class WbLoginSession {
    private String oauth_token;
    private String oauth_token_secret;
    private String sid;
    private String gsid;
    private String uid;
    private String oauth2_access_token;
    private int oauth2_issued_at;
    private int oauth2_expires;

    public static WbLoginSession fromJSON(JSONObject json) {
        WbLoginSession session = new WbLoginSession();
        session.gsid = json.getString("gsid");
        session.oauth_token = json.getString("oauth_token");
        session.oauth_token_secret = json.getString("oauth_token_secret");
        session.uid = json.getString("uid");
        JSONObject oauth2 = json.getJSONObject("oauth2.0");
        session.oauth2_access_token = oauth2.getString("access_token");
        session.oauth2_issued_at = oauth2.getIntValue("issued_at");
        session.oauth2_expires = oauth2.getIntValue("expires");
        return session;
    }

    public String serialize() {
        JSONObject json = new JSONObject();
        json.put("gsid", gsid);
        json.put("oauth_token", oauth_token);
        json.put("oauth_token_secret", oauth2_access_token);
        json.put("uid", uid);

        JSONObject oauth2 = new JSONObject();
        oauth2.put("access_token", oauth2_access_token);
        oauth2.put("issued_at", oauth2_issued_at);
        oauth2.put("expires", oauth2_expires);

        json.put("oauth2.0", oauth2);
        return json.toJSONString();
    }

    public String getAccessToken() {
        return oauth2_access_token;
    }
    public String getGsid() {
        return gsid;
    }
    public String getUid() {
        return uid;
    }
}
