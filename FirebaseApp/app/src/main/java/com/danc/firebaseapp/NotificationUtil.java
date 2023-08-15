package com.danc.firebaseapp;

public class NotificationUtil {

    private static NotificationUtil notificationUtil;

    public static NotificationUtil getInstance(){
        if(notificationUtil != null){
            return notificationUtil;
        } else {
            notificationUtil = new NotificationUtil();
            return notificationUtil;
        }
    }




}
