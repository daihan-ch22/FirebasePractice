package com.firebase.myserver.util;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import static com.firebase.myserver.common.Constants.DEVICE_TOKEN;

@Slf4j
public class JsonUtil {

    public static String getFirebaseJsonString(String title, String body) {

        JsonObject notification = new JsonObject();
        JsonObject message = new JsonObject();
        JsonObject finalJsonString = new JsonObject();

        notification.addProperty("title", title);
        notification.addProperty("body", body);

        message.add("notification", notification);
        message.addProperty("token", DEVICE_TOKEN);

        finalJsonString.add("message", message);

        log.error("finalJsonString::::: ");
        log.error(finalJsonString.toString());

        return finalJsonString.toString();
    }

    public static String getFirebaseJsonString(String title, String body, String[] args) {
        return "";
    }
}
