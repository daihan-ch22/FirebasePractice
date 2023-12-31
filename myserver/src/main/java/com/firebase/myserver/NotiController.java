package com.firebase.myserver;


import com.firebase.myserver.common.Constants;
import com.google.api.Http;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.protocol.HTTP;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@Slf4j
public class NotiController {

    private NotiService service;

    @PostMapping("/fcm/api/v1")
    public ResponseEntity<?> reqFcm(@RequestBody MessageDto dto) {

        String title = dto.getTitle();
        String body = dto.getBody();

        try {
            if (title == null || title == "null" || body == null || body == "null") {
                throw new HttpResponseException(500, "이런! 널값이잖아?");
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", dto.getTitle());
            jsonObject.addProperty("body", dto.getBody());
            log.debug("input message : " + jsonObject.asMap().toString());
            service.sendMessage(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("앗! 널값이들어왔네!");
        }
        return ResponseEntity.ok("푸쉬메시지 전송 성공");
    }

    @PostMapping("/fcm/api/token")
    public ResponseEntity<?> getFCMToken(@RequestBody String token){
        String response = "";

        if(token != null && !token.isEmpty()) {
            Constants.DEVICE_TOKEN = token;
            response = "Successfully received FCM token from App.";
            log.debug("DEVICE TOKEN == " + Constants.DEVICE_TOKEN);
            return ResponseEntity.ok(response);
        } else {
            response = "Failed to receive FCM token from App.";
            return ResponseEntity.internalServerError().build();
        }
    }

}
