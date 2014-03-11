package org.foxteam.wbgrab.restapi;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.RequestBuilder;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class SearchStatus extends RestRequest {
    public JSONObject searchStatus(String q, int page, int count) throws RestException {
        RequestBuilder request = RequestBuilder.post()
                .setUri(RestConfig.getValue("ServerUrl") + "/search/statuses")
                .addParameter("q", q)
                .addParameter("page", String.valueOf(page))
                .addParameter("count", String.valueOf(count));
        return restRequest(request, AuthType.AUTH_TYPE_GSID);
    }
}
