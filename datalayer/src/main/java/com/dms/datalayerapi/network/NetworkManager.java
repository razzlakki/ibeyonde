package com.dms.datalayerapi.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.dms.datalayerapi.network.event.ClientUpdateListener;
import com.dms.datalayerapi.network.exception.NetworkManagerException;
import com.dms.phoenix.util.Logger;
import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Raja.p on 20-05-2016.
 */
public abstract class NetworkManager {

    private static final String CACHE_STORE = "cache_store";
    private SharedPreferences sharedPrefrence;
    protected Context context;
    private ClientProperties clientProperties;
    protected MediaType JSON
            = MediaType.parse("application/text; charset=utf-8");
    private HttpHeaderMaker headers;
    protected Gson gson;
    private boolean diskCacheEnable;

    public void updateMediaType(MediaType mediaType) {
        this.JSON = mediaType;
    }

    protected NetworkManager(Context fragmentActivity) {
        this.context = fragmentActivity;
        clientProperties = ClientProperties.getInstance();
        gson = new Gson();
        diskCacheEnable = true;
        sharedPrefrence = context.getSharedPreferences(CACHE_STORE, Context.MODE_PRIVATE);
        headers = new HttpHeaderMaker();
        headers = getDefaultHeaders(headers);
        clientProperties.setOnClientUpdateListener(new ClientUpdateListener() {

            @Override
            public void onClientUpdated() {
                updateInputs();
            }
        });
    }

    public abstract HttpHeaderMaker getDefaultHeaders(HttpHeaderMaker headers);

    public HttpHeaderMaker getHeaders() {
        return headers;
    }

    public NetworkManager addHeaders(HttpHeaderMaker headers) {
        this.headers = headers;
        return this;
    }

    public NetworkManager diskCacheEnable(boolean diskCacheEnable) {
        this.diskCacheEnable = diskCacheEnable;
        return this;
    }

    private void updateInputs() {
        clientProperties.getClient().interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return onOnIntercept(chain);
            }
        });
    }

    protected Response onOnIntercept(Interceptor.Chain chain) throws IOException {
        try {
            Response response = chain.proceed(chain.request());
            String content = convertResponseToString(response);
            return response.newBuilder().body(ResponseBody.create(response.body().contentType(), content)).build();
        } catch (SocketTimeoutException exception) {
            updateException(NetworkManagerException.Type.CONNECTION_TIMEOUT_EXCEPTION, exception);
        } catch (UnknownHostException exception) {
            updateException(NetworkManagerException.Type.UNKNOWN_HOST_EXCEPTION, exception);
        } catch (IOException exception) {
            updateException(NetworkManagerException.Type.IO_EXCEPTION, exception);
        } catch (Exception exception) {
            updateException(NetworkManagerException.Type.UNKNOWN_EXCEPTION, exception);
        }
        return chain.proceed(chain.request());
    }

    protected abstract void updateException(NetworkManagerException.Type connectionTimeoutException, Exception exceptionDetail);


    public String convertResponseToString(Response response) {
        String responseString = "";
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            updateException(NetworkManagerException.Type.UNKNOWN_RESPONSE_EXCEPTION, e);
        }
        return responseString;
    }


    public ClientProperties getClientProperties() {
        return clientProperties;
    }


    public String doGet(String url) {
        if (diskCacheEnable)
            return cacheCheck(url, null, Http.GET);
        else
            return doBaseCall(url, null, Http.GET);
    }

    private String cacheCheck(final String url, final String body, final Http type) {
        String sharedResult = sharedPrefrence.getString(url, null);
        if (sharedResult == null) {
            String result = doBaseCall(url, body, type);
            if (result != null) {
                SharedPreferences.Editor editor = sharedPrefrence.edit();
                editor.putString(url, result);
                editor.commit();
            }
            return result;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = doBaseCall(url, body, type);
                    if (result != null) {
                        SharedPreferences.Editor editor = sharedPrefrence.edit();
                        editor.putString(url, result);
                        editor.commit();
                    }
                }
            }).start();
            return sharedResult;
        }

    }

    public String doPost(String url, String body) {
        if (diskCacheEnable)
            return cacheCheck(url, body, Http.POST);
        else
            return doBaseCall(url, body, Http.POST);
    }

    private String doBaseCall(String url, String body, Http type) {
        String stringResponse = null;
        try {
            Request.Builder absRequest = new Request.Builder()
                    .url(url);
            if (type == Http.POST)
                absRequest.post(RequestBody.create(JSON, body != null ? body : ""));
            if (headers != null) {
                try {
                    headers.build(absRequest);
                } catch (Exception e) {
                    updateException(NetworkManagerException.Type.UNKNOWN_HEADERS_EXCEPTION, e);
                }
            }
            Request request = absRequest.build();
            Response response = null;
            try {
                response = clientProperties.getClient().newCall(request).execute();
            } catch (IOException e) {
                Logger.e(e);
                updateException(NetworkManagerException.Type.IO_EXCEPTION, e);
            }
            try {
                stringResponse = response.body().string();
            } catch (IOException e) {
                updateException(NetworkManagerException.Type.UNKNOWN_RESPONSE_EXCEPTION, e);
            }
        } catch (Exception ex) {
            updateException(NetworkManagerException.Type.UNKNOWN_EXCEPTION, ex);
        }
        return stringResponse;
    }

}
