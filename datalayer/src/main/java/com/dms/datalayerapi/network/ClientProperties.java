package com.dms.datalayerapi.network;

import android.support.annotation.NonNull;

import com.dms.datalayerapi.network.constants.NetworkConstants;
import com.dms.datalayerapi.network.event.ClientUpdateListener;
import com.dms.datalayerapi.network.exception.InvalidClientException;
import com.squareup.okhttp.OkHttpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Raja.p on 20-05-2016.
 */
public class ClientProperties {

    private OkHttpClient okHttpClient;
    private TimeUnit defaultTimeConstant = TimeUnit.MINUTES;
    private ClientUpdateListener onClientUpdateListener;

    static ClientProperties getInstance() {
        return new ClientProperties();
    }

    private ClientProperties() {

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("app.ibeyonde.com", session);
            }
        };

        okHttpClient = new OkHttpClient();
        okHttpClient.setHostnameVerifier(hostnameVerifier);
        try {
            okHttpClient.setSslSocketFactory(new MyTLSSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        setDefaultSettings();

    }

    private void setDefaultSettings() {
        okHttpClient.setConnectTimeout(NetworkConstants.CONNECTION_TIMEOUT, defaultTimeConstant);
        okHttpClient.setReadTimeout(NetworkConstants.READ_TIMEOUT, defaultTimeConstant);
    }

    public OkHttpClient getClient() {
        return this.okHttpClient;
    }

    /**
     * It throws an InvalidClientException
     *
     * @param okHttpClient
     * @throws InvalidClientException
     */
    public void updateClient(@NonNull OkHttpClient okHttpClient) throws InvalidClientException {
        if (okHttpClient != null) {
            this.okHttpClient = okHttpClient;
            if (this.onClientUpdateListener != null)
                this.onClientUpdateListener.onClientUpdated();
        } else {
            throw new InvalidClientException();
        }
    }

    public void setOnClientUpdateListener(ClientUpdateListener onClientUpdateListener) {
        this.onClientUpdateListener = onClientUpdateListener;
    }
}
