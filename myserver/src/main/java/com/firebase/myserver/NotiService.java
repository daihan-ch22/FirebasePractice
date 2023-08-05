package com.firebase.myserver;

import com.firebase.myserver.network.HttpsRequest;
import com.firebase.myserver.network.HttpRequestCallBack;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.myserver.common.Constants.*;

@Slf4j
@Service
public class NotiService implements HttpRequestCallBack {

    @Transactional
    private AccessToken getAccessToken() {
        GoogleCredentials googleCredential = null;

        try {
            googleCredential = GoogleCredentials
                    .fromStream(new ClassPathResource(FCM_JSON_PATH).getInputStream())
                    .createScoped(Arrays.asList(SCOPES));
            googleCredential.refreshIfExpired();

        } catch (IOException e){
            e.printStackTrace();
        }

        @NotNull AccessToken token = googleCredential.getAccessToken();
        log.debug("FCM TOKEN = " + token);
        return token;
    }

    @Transactional
    public void sendMessage(JsonObject fcmMessage) {

        final String stringUrl = BASE_URL + FCM_SEND_ENDPOINT;

        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("Authorization", "Bearer " + getAccessToken());
        propertyMap.put("Content-Type", "application/json; UTF-8");
        HttpsRequest request = new HttpsRequest(this);
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
