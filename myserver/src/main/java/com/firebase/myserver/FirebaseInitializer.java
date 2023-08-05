package com.firebase.myserver;

import com.firebase.myserver.common.Constants;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseInitializer {

    private final ResourceLoader resourceLoader;

    public FirebaseInitializer(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public FirebaseApp firebaseAppInit() throws IOException {
        log.debug("== Firebase init start ==");

        Resource resource = resourceLoader.getResource("classpath:" + Constants.FCM_JSON_PATH);
        InputStream serviceAccountStream = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        log.debug("== Firebase init finished ==");
        return FirebaseApp.initializeApp(options);
    }

}
