package com.firebase.myserver.common;

public class Constants {
    public static final String PROJECT_ID = "fir-practice-7ae2d";
    public static final String BASE_URL = "https://fcm.googleapis.com";
    public static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
    public static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    public static final String[] SCOPES = { MESSAGING_SCOPE };
    public static final String MESSAGE_KEY = "message";
    public static final String FCM_JSON_PATH = "fir-practice-7ae2d-firebase-adminsdk-ogel7-7b3f9214b8.json";
}
