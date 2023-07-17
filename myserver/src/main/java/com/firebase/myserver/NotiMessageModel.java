package com.firebase.myserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Data
@Builder
public class NotiMessageModel {
    private Notification notification;

    @AllArgsConstructor
    @Data
    @Builder
    public static class Notification{
        private String title;
        private String body;
    }


}
