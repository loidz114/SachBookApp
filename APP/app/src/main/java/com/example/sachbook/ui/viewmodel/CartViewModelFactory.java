package com.example.sachbook.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public CartViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}