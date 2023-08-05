package com.firebase.myserver.network;

public interface HttpRequestCallBack {

    void onSuccess();

    void onFailure(String message);
}
