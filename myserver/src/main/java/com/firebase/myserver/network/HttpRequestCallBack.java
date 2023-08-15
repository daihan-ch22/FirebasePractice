package com.firebase.myserver.network;

import org.apache.http.client.HttpResponseException;

public interface HttpRequestCallBack {

    void onSuccess();

    void onFailure(int returnCode, String message) throws HttpResponseException;
}
