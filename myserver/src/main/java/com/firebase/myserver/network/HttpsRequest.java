package com.firebase.myserver.network;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import static com.firebase.myserver.common.Constants.FCM_JSON_PATH;
import static com.firebase.myserver.common.Constants.SCOPES;

@Slf4j
public class HttpsRequest {

    private HttpRequestCallBack mCallBack;
    private String firebaseMessage;

    public HttpsRequest(HttpRequestCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public HttpsRequest(HttpRequestCallBack mCallBack, String firebaseMessage) {
        this.mCallBack = mCallBack;
        this.firebaseMessage = firebaseMessage;
    }

    public interface HttpMethods {
        String GET = "GET";
        String POST = "POST";
    }

    public void requestHttpsConnection(String methods, String targetUrl, Map<String, String> params) {
        requestConnection(getConnection(methods, targetUrl, params));
    }

    private void requestConnection(HttpsURLConnection conn) {

        try {
            conn.connect();
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(firebaseMessage.getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Handle success
                log.debug("SUCCESS CB!");
            } else {
                // Handle failure
                log.error("FAILURE CB! === " + responseCode);
                log.error("FAILURE CB! === " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HttpsURLConnection getConnection(String methods,
                                             String targetUrl,
                                             Map<String, String> params
    ) {

        HttpsURLConnection conn = null;
        URL url = null;

        SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
        HostnameVerifier hostnameVerifier = getHostNameVerifier();

        try {
            url = new URL(paramSet(targetUrl, params));
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            conn.setRequestProperty("Content-Type","application/json; UTF-8" );
            //conn.setHostnameVerifier(hostnameVerifier);
            //conn.setSSLSocketFactory(sslSocketFactory);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod(methods);
            conn.setUseCaches(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String getAccessToken() {
        GoogleCredentials googleCredential = null;
        try {
            googleCredential = GoogleCredentials
                    .fromStream(new ClassPathResource(FCM_JSON_PATH).getInputStream())
                    .createScoped(SCOPES);
            googleCredential.refreshIfExpired();

        } catch (IOException e) {
            e.printStackTrace();
        }
        @NotNull AccessToken token = googleCredential.getAccessToken();
        log.debug("FCM TOKEN = " + token);
        return token.getTokenValue();
    }

    private void propertySet(HttpsURLConnection conn, Map<String, String> propertyMap) {
        if (propertyMap != null || propertyMap.size() != 0) {
            for (String key : propertyMap.keySet()) {
                conn.setRequestProperty(key, propertyMap.get(key));
            }
        }
    }

    private String paramSet(String targetUrl, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(targetUrl);
        if (params.size() != 0 || params != null) {
            int paramCounts = 0;
            for (String key : params.keySet()) {
                if (paramCounts == 0) {
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key).append("=").append(params.get(key));
                paramCounts++;
            }
        }
        return builder.toString();
    }

    private SSLSocketFactory getSSLSocketFactory() {

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;

        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, certs, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    private HostnameVerifier getHostNameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true; //일단 그냥 통과
            }
        };
    }
}
