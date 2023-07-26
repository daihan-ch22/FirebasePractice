package com.firebase.myserver.network;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Slf4j
public class HttpRequest {

    private HttpRequestCallBack mCallBack;

    public HttpRequest(HttpRequestCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface HttpMethods{
        String GET = "GET";
        String POST = "POST";
    }

    public void requestConnection(String methods, URL url, Map<String, String> propertyMap){

        HttpURLConnection conn = getConnection(methods, url, propertyMap);
        int responseCode = 0;

        if(conn != null){
           try {
               conn.connect();
               responseCode = conn.getResponseCode();

               if(responseCode == 200){
                   //Success
                   log.debug("Token Request successful");
               } else {
                   //Abnormal
                   log.error("Something wrong! Response code == " + responseCode);
               }
           }catch (Exception e){
               e.printStackTrace();
               log.error("HTTP connection request failed");
           }
        }

    }


    private HttpURLConnection getConnection(String methods, URL url, Map<String, String> propertyMap){

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection)url.openConnection();

            if(propertyMap.size() != 0 || propertyMap != null){
                for(String key : propertyMap.keySet()){
                    conn.setRequestProperty(key, propertyMap.get(key));
                }
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod(methods);
            conn.setUseCaches(false);
        } catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }
}
