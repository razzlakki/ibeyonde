package com.dms.datalayerapi.network;

import android.support.annotation.NonNull;

import com.dms.datalayerapi.network.constants.NetworkConstants;
import com.dms.datalayerapi.network.event.ClientUpdateListener;
import com.dms.datalayerapi.network.exception.InvalidClientException;
import com.squareup.okhttp.OkHttpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

        okHttpClient = new OkHttpClient();
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            this.okHttpClient.setSslSocketFactory(sslSocketFactory);
            this.okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
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
