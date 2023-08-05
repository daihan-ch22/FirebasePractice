package com.firebase.myserver.network;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Slf4j
public class HttpsRequest {

    private HttpRequestCallBack mCallBack;

    public HttpsRequest(HttpRequestCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface HttpMethods{
        String GET = "GET";
        String POST = "POST";
    }

    public void requestHttpsConnection(String methods, String targetUrl, Map<String, String> propertyMap ,Map<String, String> params){
        requestConnection(getConnection(methods, targetUrl, propertyMap , params));
    }

    private void requestConnection(HttpsURLConnection conn){

        int responseCode = 0;

        if(conn != null){
           try {
               URL url = conn.getURL();
               log.error("targetURL == " + url);
               conn.connect();
               responseCode = conn.getResponseCode();

               if(responseCode == HttpsURLConnection.HTTP_OK){
                   //Success
                   log.debug("Token Request successful");
                   mCallBack.onSuccess();
               } else {
                   //Abnormal
                   log.error("Something wrong! Response code == " + responseCode);
                   mCallBack.onFailure(conn.getResponseMessage());
               }
           }catch (Exception e){
               e.printStackTrace();
               log.error("HTTP connection request failed");
           }
        }
    }


    private HttpsURLConnection getConnection(String methods,
                                             String targetUrl,
                                             Map<String, String> propertyMap ,
                                             Map<String, String> params
    ){

        HttpsURLConnection conn = null;
        URL url = null;

        //socketfactory
        //verifier

        try {

            StringBuilder builder = new StringBuilder(targetUrl);
            if(params.size() != 0 || params != null){
                int paramCounts = 0;
                for(String key : params.keySet()){
                    if(paramCounts == 0) {
                        builder.append("?");
                    } else {
                        builder.append("&");
                    }
                    builder.append(key).append("=").append(params.get(key));
                    paramCounts++;
                }
            }
            url = new URL(builder.toString());

            conn = (HttpsURLConnection)url.openConnection();
            propertySet(conn, propertyMap);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setHostnameVerifier(getHostNameVerifier());
            conn.setSSLSocketFactory(getSSLSocketFactory());
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod(methods);
            conn.setUseCaches(false);
        } catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    private void propertySet(HttpsURLConnection conn, Map<String, String> propertyMap){
        if(propertyMap != null || propertyMap.size() != 0){
            for(String key : propertyMap.keySet()){
                conn.setRequestProperty(key, propertyMap.get(key));
            }
        }
    }

    private SSLSocketFactory getSSLSocketFactory(){

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;

        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try{
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, certs, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e){
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    private HostnameVerifier getHostNameVerifier(){
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
    }
}
