package com.firebase.myserver;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseInitializer{

    @PostConstruct
    public FirebaseApp firebaseAppInit() throws IOException{

        log.debug("firebase init......");
        FileInputStream serviceAccount =
                new FileInputStream("fir-practice-7ae2d-firebase-adminsdk-ogel7-7b3f9214b8.json");

        log.debug("serviceAccount = " + serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        log.debug("options = " + options);

        return FirebaseApp.initializeApp(options);
    }
}
