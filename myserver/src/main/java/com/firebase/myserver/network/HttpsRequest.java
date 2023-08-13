package com.firebase.myserver.network;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.firebase.myserver.common.Constants.FCM_JSON_PATH;
import static com.firebase.myserver.common.Constants.SCOPES;

@Slf4j
public class HttpsRequest {

    private HttpRequestCallBack mCallBack;
    private String firebaseMessage;
    private String jsonData;

    public HttpsRequest(HttpRequestCallBack mCallBack, String firebaseMessage) {
        this.mCallBack = mCallBack;
        this.firebaseMessage = firebaseMessage;
    }

    public interface HttpMethods {
        String GET = "GET";
        String POST = "POST";
    }

    public void requestHttpsConnection(String method, String targetUrl, Map<String, String> params) {
        requestConnection(getConnection(method, targetUrl, params), method);
    }

    private void requestConnection(HttpsURLConnection conn, String method) {

        String result = "";
        int responseCode = 0;
        String responseMessage = "";

        try {

            //connect()가 필요한 메서드 getOutputStream(), getInputStream()을 사용하면
            //connect()가 없어도 암묵적으로 통신을 연결한다.
            conn.connect();

            //POST인 경우 바디에 데이터 넣고 json으로 쏜다
            if(method.equals(HttpMethods.POST)){
                OutputStream os = conn.getOutputStream();
                //byte[] inputData = jsonData.getBytes(StandardCharsets.UTF_8);
                byte[] data = firebaseMessage.getBytes(StandardCharsets.UTF_8);
                os.write(data, 0, data.length);
                os.flush();
                os.close();
            }

            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                //응답 데이터 읽어온다
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String receivedData = null;
                while ((receivedData = br.readLine()) != null) {
                    response.append(receivedData.trim());
                }
                //응답 데이터
                result = response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Handle success
                log.debug("SUCCESS CB!");
            } else {
                // Handle failure
                log.error("FAILURE CB! === " + responseCode);
                log.error("FAILURE CB! === " + responseMessage);
            }
        }
    }


    private HttpsURLConnection getConnection(String methods,
                                             String targetUrl,
                                             Map<String, String> params
    ) {

        HttpsURLConnection conn = null;

        try {

            URL url = new URL(targetUrl);
            if(methods.equals(HttpMethods.GET)) {
                //GET인경우 params를 url뒤에 쿼리스트링으로 붙인다.
                url = new URL(paramSet(targetUrl, params));
            }else if(methods.equals(HttpMethods.POST)){
                //POST일경우 params를 json형식으로 변경한다.
                setJsonData(params);
            }

            //connection 설정값
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            conn.setRequestProperty("Content-Type","application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setHostnameVerifier(SslUtil.getHostNameVerifier());
            conn.setSSLSocketFactory(SslUtil.getSSLSocketFactory());
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

    private void setJsonData(Map<String, String> params) {
        Gson gson = new Gson();
        jsonData = gson.toJson(params);
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
}
