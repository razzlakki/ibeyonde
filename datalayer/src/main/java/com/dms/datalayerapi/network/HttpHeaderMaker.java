package com.dms.datalayerapi.network;

import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by razzl_000 on 5/22/2016.
 */
public class HttpHeaderMaker {
    HashMap<String, String> headers;

    public HttpHeaderMaker() {
        headers = new HashMap<>();
    }

    public HttpHeaderMaker addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public void build(Request.Builder absRequest) {
        Iterator<Map.Entry<String, String>> i = headers.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> me = i.next();
            absRequest.addHeader(me.getKey(), me.getValue());
        }
    }

}
