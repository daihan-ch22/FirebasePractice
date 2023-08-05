package com.firebase.myserver;

import com.firebase.myserver.network.HttpsRequest;
import com.firebase.myserver.network.HttpRequestCallBack;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.myserver.common.Constants.*;

@Slf4j
@Service
@NoArgsConstructor
public class NotiService implements HttpRequestCallBack {

    FirebaseApp firebaseApp;

    @Transactional
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

    @Transactional
    public void sendMessage(JsonObject fcmMessage) {

        final String stringUrl = BASE_URL + FCM_SEND_ENDPOINT;

        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("Authorization", "Bearer " + getAccessToken());
        propertyMap.put("Content-Type", "application/json; UTF-8");

        Notification notification = Notification.builder().setTitle(fcmMessage.get("title").toString())
                .setBody(fcmMessage.get("body").toString()).build();

        Message message = Message.builder()
                        .setToken("DEVICETOKEN") //TODO: setup Android & put a fcm device token
                                .setNotification(notification)
                                        .build();

        //FirebaseMessaging instance = FirebaseMessaging.getInstance();


        String jsonMessage = new Gson().toJson(message).toString();
        log.error(jsonMessage);


        HttpsRequest request = new HttpsRequest(this, jsonMessage);
        request.requestHttpsConnection(HttpsRequest.HttpMethods.POST, stringUrl, propertyMap, new HashMap<>());

    }

    @Override
    public void onSuccess() {
        log.debug("SUCCESS CB!");
    }

    @Override
    public void onFailure(String message) {
        log.error("FAILURE CB! === " + message);
    }
}
