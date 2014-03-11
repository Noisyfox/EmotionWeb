package org.foxteam.wbgrab.restapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class RestRequest {
    private WbLoginSession session;
    private static HttpClient client = HttpClients.createDefault();

    public enum AuthType {
        AUTH_TYPE_NONE,
        AUTH_TYPE_OAUTH2,
        AUTH_TYPE_GSID
    }

    public void setSession(WbLoginSession session) {
        this.session = session;
    }

    public JSONObject restRequest(RequestBuilder request, AuthType authType) throws RestException {
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("User-Agent", RestConfig.getValue("user_agent"));
        request.addHeader("Accept-Encoding", "gzip");
        switch (authType) {
            case AUTH_TYPE_NONE:
                request.addParameter("source", RestConfig.getValue("AppKey"));
                request.addParameter("c", RestConfig.getValue("ClientName"));
                request.addParameter("wm", RestConfig.getValue("WMValue"));
                request.addParameter("from", RestConfig.getValue("From"));
                request.addParameter("lang", RestConfig.getValue("Lang"));
                request.addParameter("ua", RestConfig.getValue("user_agent2"));
                break;
            case AUTH_TYPE_OAUTH2:
                if (session == null) {
                    throw new RestException("WbLoginSession must be set by `setSession' before request call");
                }
                request.addHeader("Authorization", "OAuth2 " + session.getAccessToken());
                try {
                    InetAddress localAddress = InetAddress.getLocalHost();
                    request.addHeader("API-RemoteIP", localAddress.getHostAddress());
                } catch (UnknownHostException e) {
                    throw new RestException("Cannot get local IP address");
                }
                break;
            case AUTH_TYPE_GSID:
                if (session == null) {
                    throw new RestException("WbLoginSession must be set by `setSession' before request call");
                }
                request.addParameter("s", AccountLogin.checksum(session.getUid()));
                request.addParameter("gsid", session.getGsid());
                request.addParameter("source", RestConfig.getValue("AppKey"));
                request.addParameter("c", RestConfig.getValue("ClientName"));
                request.addParameter("wm", RestConfig.getValue("WMValue"));
                request.addParameter("from", RestConfig.getValue("From"));
                request.addParameter("lang", RestConfig.getValue("Lang"));
                request.addParameter("ua", RestConfig.getValue("user_agent2"));
                break;
        }
        HttpUriRequest httpUriRequest = request.build();

        try {
            HttpResponse response = client.execute(httpUriRequest);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new RestException("NULL response error");
            }
            String result = EntityUtils.toString(entity);
            JSONObject json = (JSONObject) JSON.parse(result);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RestException("Request RestAPI failed", json, response.getStatusLine().getStatusCode());
            }
            if (json.containsKey("error_code") || json.containsKey("errno")) {
                throw new RestException("Request RestAPI failed", json);
            }
            return json;
        } catch (IOException e) {
            throw new RestException("Request aborted", e);
        }

    }
}
