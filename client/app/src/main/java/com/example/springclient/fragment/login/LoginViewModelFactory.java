package com.example.springclient.fragment.login;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private Context context;

    public LoginViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel((Application) context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
