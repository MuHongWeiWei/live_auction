package com.example.fly.anyrtcdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fly.anyrtcdemo.R;

public class SerachFragment extends Fragment {
    private static SerachFragment instance;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_serach,container,false);
    }

    public static SerachFragment getInstance() {
        if(instance == null){
            instance = new SerachFragment();
        }
        return instance;
    }
}
