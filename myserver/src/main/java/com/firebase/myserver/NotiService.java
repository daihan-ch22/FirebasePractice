package com.firebase.myserver;

import com.firebase.myserver.network.HttpsRequest;
import com.firebase.myserver.network.HttpRequestCallBack;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.firebase.myserver.common.Constants.*;

@Slf4j
@Service
public class NotiService implements HttpRequestCallBack {

    @Transactional
    public void sendMessage(JsonObject fcmMessage) {

        final String stringUrl = BASE_URL + FCM_SEND_ENDPOINT;
        String title = fcmMessage.get("title").toString();
        String body = fcmMessage.get("body").toString();

        Message message = Message.builder()
                .setToken(DEVICE_TOKEN) //TODO: setup Android & put a fcm device token
                .putData("title", title)
                .putData("body", body)
                .build();


        String data =
                "{\n" + "\"message\":{\n" + " \"notification\": {\n" + " \"title\":" + title+ ",\n"
                        + " \"body\": \"" + body + "\",\n"
                        + "  },\n" + " \"token\": \"" + DEVICE_TOKEN + "\"\n" + "  }\n" + "}\n";

        // option 1 이거는 되는데 HttpUrlConnection 방식은 JSON형식의 문제인듯
        /*try {
            String send = FirebaseMessaging.getInstance().send(message);
            log.debug("FB message RESULT == " + send);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }*/


        log.error(data);


        HttpsRequest request = new HttpsRequest(this, data);
        request.requestHttpsConnection(HttpsRequest.HttpMethods.POST, stringUrl, new HashMap<>());
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
