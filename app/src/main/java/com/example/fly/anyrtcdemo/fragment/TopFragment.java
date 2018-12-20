package com.example.fly.anyrtcdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fly.anyrtcdemo.R;

public class TopFragment extends Fragment {
    private static TopFragment instane;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top,container,false);
    }


    public static TopFragment getInstance() {
        if(instane == null){
            instane = new TopFragment();
        }
        return instane;
    }
}
