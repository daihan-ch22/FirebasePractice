package com.firebase.myserver;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.firebase.myserver.Constants.*;

@Slf4j
@Service
public class NotiService {
    private static HttpURLConnection getConnection() throws IOException {
        URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    private static AccessToken getAccessToken() throws IOException {
        GoogleCredentials googleCredential = GoogleCredentials
                .fromStream(new ClassPathResource("/*your json file*/").getInputStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshIfExpired();

        AccessToken token = googleCredential.getAccessToken();
        log.debug("TOKEN = " + token);
        return token;
    }


}
