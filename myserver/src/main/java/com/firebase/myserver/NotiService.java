package com.firebase.myserver;

import com.firebase.myserver.network.HttpsRequest;
import com.firebase.myserver.network.HttpRequestCallBack;
import com.firebase.myserver.util.JsonUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.firebase.myserver.common.Constants.*;

@Slf4j
@Service
public class NotiService implements HttpRequestCallBack {

    @Transactional
    public void sendMessage(JsonObject fcmMessage) throws HttpResponseException {

        String title = fcmMessage.get("title").toString();
        String body = fcmMessage.get("body").toString();

        useHttpsUrlConnection(title, body);

        //useFBMessagingApi(title, body);
    }

    // Option 1 : FirebaseMessaging과 Message객체 만들어서 호출
    private void useFBMessagingApi(String title, String body){
        Message message = Message.builder()
                .setToken(DEVICE_TOKEN)
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            String send = FirebaseMessaging.getInstance().send(message);
            log.debug("FB message RESULT == " + send);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    // Option 2 : 직접 Json형식으로 데이터 형식 만들어서 HttpsUrlConnection으로 쏘는 방식
    private void useHttpsUrlConnection(String title, String body) throws HttpResponseException {
        final String stringUrl = BASE_URL + FCM_SEND_ENDPOINT;

        String jsonStringForFirebase = JsonUtil.getFirebaseJsonString(title, body);

        HttpsRequest request = new HttpsRequest(this, jsonStringForFirebase);
        request.requestHttpsConnection(HttpsRequest.HttpMethods.POST, stringUrl, new HashMap<>());
    }

    @Override
    public void onSuccess() {
        log.debug("SUCCESS CB!");
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) throws HttpResponseException {
        log.error("FAILURE CB! === " + responseCode);
        log.error("FAILURE CB! === " + responseMessage);
        throw new HttpResponseException(responseCode, responseMessage);
    }
}
