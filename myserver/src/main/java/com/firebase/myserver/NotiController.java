package com.firebase.myserver;


import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@Slf4j
public class NotiController {

    private NotiService service;

    @PostMapping("/fcm/api/v1")
    public ResponseEntity<?> reqFcm(
            @RequestParam(required = true) String title,
            @RequestParam(required = true) String body
    ) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("body", body);
        log.debug("input message : " + jsonObject.asMap().toString());
        service.sendMessage(jsonObject);

        return ResponseEntity.ok("!");
    }

}
