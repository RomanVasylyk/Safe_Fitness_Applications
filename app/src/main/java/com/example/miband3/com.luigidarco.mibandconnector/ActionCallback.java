package com.example.miband3.com.luigidarco.mibandconnector;

public interface ActionCallback {

    void onSuccess(Object data);

    void onFailure(int errorCode, String msg);
}
