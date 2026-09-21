package com.example.makhovyklab1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    private AppContract appContract;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.appContract = (AppContract) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (appContract != null) {
            appContract.unregisterListeners(this);
        }

        appContract = null;
    }

    final AppContract getAppContract() {
        return appContract;
    }

    final <T> void registerListener(
            Class<T> clazz,
            ResponseListener<T> listener
    ) {
        getAppContract().registerListener(this, clazz, listener);
    }
}