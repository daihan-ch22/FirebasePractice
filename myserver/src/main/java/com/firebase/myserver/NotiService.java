package com.firebase.myserver;

import com.firebase.myserver.network.HttpRequest;
import com.firebase.myserver.network.HttpRequestCallBack;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.myserver.Constants.*;

@Slf4j
@Service
public class NotiService implements HttpRequestCallBack {

    public void createToken() throws IOException {
        URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);


        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("Authorization", "Bearer " + getAccessToken());
        propertyMap.put("Content-Type", "application/json; UTF-8");

        HttpRequest request = new HttpRequest(this);
        request.requestConnection(HttpRequest.HttpMethods.POST, url, propertyMap);

    }

    private static AccessToken getAccessToken() throws IOException {
        GoogleCredentials googleCredential = GoogleCredentials
                .fromStream(new ClassPathResource(FCM_JSON_PATH).getInputStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshIfExpired();

        AccessToken token = googleCredential.getAccessToken();
        log.debug("TOKEN = " + token);
        return token;
    }

    public static void sendMessage(JsonObject fcmMessage) throws IOException {
        HttpURLConnection connection = getConnection();
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(fcmMessage.toString());
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            String response = inputstreamToString(connection.getInputStream());
            System.out.println("Message sent to Firebase for delivery, response:");
            System.out.println(response);
        } else {
            System.out.println("Unable to send message to Firebase:");
            String response = inputstreamToString(connection.getErrorStream());
            System.out.println(response);
        }
    }

    @Override
    public void onSuccess() {
        log.debug("SUCCESS CB!");
    }

    @Override
    public void onFailure() {
        log.error("FAILURE CB!");
    }
}
