package com.example.springclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> webSocketMessage = new MutableLiveData<>();

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

}
