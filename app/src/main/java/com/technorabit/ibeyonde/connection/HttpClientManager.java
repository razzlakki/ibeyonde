package com.technorabit.ibeyonde.connection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import com.dms.datalayerapi.network.Http;
import com.dms.datalayerapi.network.HttpHeaderMaker;
import com.dms.datalayerapi.network.NetworkManager;
import com.dms.datalayerapi.network.event.NetworkConErrorListener;
import com.dms.datalayerapi.network.exception.NetworkManagerException;
import com.dms.datalayerapi.util.ConnectionUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Credentials;

/**
 * Created by Raja.p on 23-05-2016.
 */
public class HttpClientManager extends NetworkManager {

    public static String NETWORK_FAILURE_RECIVER = "com.technorabit.parentzapp.NETWORK_FAILURE_RECIVER";
    private String username;
    private String password;

    protected HttpClientManager(Context fragmentActivity) {
        super(fragmentActivity);
    }

    public static HttpClientManager get(FragmentActivity fragmentActivity) {
        return new HttpClientManager(fragmentActivity);
    }

    public static HttpClientManager get(Context fragmentActivity) {
        return new HttpClientManager(fragmentActivity);
    }

    @Override
    public HttpHeaderMaker getDefaultHeaders(HttpHeaderMaker headers) {
        if (username != null)
            headers.addHeader("Authorization", Credentials.basic(username.trim(), password.trim()));
        return headers;
    }

    private String getUserName() {
        return username;
    }

    @Override
    protected void updateException(NetworkManagerException.Type connectionTimeoutException, Exception exceptionDetail) {
        Intent intent = new Intent();
        intent.setAction(NETWORK_FAILURE_RECIVER);
        context.sendBroadcast(intent);
        switch (connectionTimeoutException) {
            case CONNECTION_TIMEOUT_EXCEPTION:
                break;
            case UNKNOWN_HOST_EXCEPTION:
                break;
            case IO_EXCEPTION:
                break;
            case UNKNOWN_EXCEPTION:
                break;
            case UNKNOWN_RESPONSE_EXCEPTION:
                break;
            case UNKNOWN_HEADERS_EXCEPTION:
                break;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public class NetworkTask<Progress, Result> extends AsyncTask<String, Progress, Result> {

        private NetworkConErrorListener networkConErrorListener;
        private Class<?> type;
        private Http callType;
        private boolean isConnectionAvailable;


        public NetworkTask(Class<?> result, Http callType) {
            type = result;
            this.callType = callType;
        }

        public NetworkTask(Class<?> result, Http callType, NetworkConErrorListener networkConErrorListener) {
            type = result;
            this.callType = callType;
            this.networkConErrorListener = networkConErrorListener;
        }

        public NetworkTask setNetworkConErrorListener(NetworkConErrorListener networkConErrorListener) {
            this.networkConErrorListener = networkConErrorListener;
            return this;
        }

        public void onNetworkNotAvailable() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isConnectionAvailable = ConnectionUtil.checkInternetConnection(context);
            if (!isConnectionAvailable) {
                if (networkConErrorListener != null) {
                    networkConErrorListener.onNetworkConnectionErrorListener();
                }
            }
        }

        @Override
        protected Result doInBackground(String... params) {
            Result classType = null;
            try {
                params = doMoreManipulationBefore(params);
                String response = "";
                switch (callType) {
                    case GET:
                        response = doGet(params[0]);
                        break;
                    case POST:
                        response = doPost(params[0], params[1]);
                        break;
                }
                if (response == null)
                    return manipulateMoreOnBackGround(null);
                if (type == null)
                    return manipulateMoreOnBackGround((Result) response);

                try {
                    if (response != null)
                        classType = (Result) gson.fromJson(response.trim(), type);
                } catch (Exception e) {
                    Log.e("Parce data Exception", e.getMessage());
                }
            } catch (Exception e) {
                Log.e("Doin BG Exception", e.getMessage());
            }
            return manipulateMoreOnBackGround(classType);
        }

        protected String[] doMoreManipulationBefore(String... params) {
            return params;
        }

        protected Result manipulateMoreOnBackGround(Result classType) {
            return classType;
        }
    }


    public Gson getGson() {
        return gson;
    }
}
