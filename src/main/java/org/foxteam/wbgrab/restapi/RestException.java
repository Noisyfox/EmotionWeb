package org.foxteam.wbgrab.restapi;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class RestException extends Exception {

    public RestException(String msg) {
        super(msg);
    }

    public RestException(Exception cause) {
        super(cause);
    }

    public RestException(String msg, Exception cause) {
        super(msg, cause);
    }

    public RestException(String msg, JSONObject json, int statusCode) {
        super(msg + "\n error: " + json.getString("error") + " error_code: " + json.getLong("error_code"));
    }

    public RestException(String msg, JSONObject json) {
        super(msg + "\n error: " + json.getString("errmsg") + " error_code: " + json.getLong("errno"));
    }
}
