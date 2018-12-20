package com.example.fly.anyrtcdemo.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import org.anyrtc.rtmpc_hybird.RTMPCHybird;

import java.io.File;

public class MyApplication extends Application {
    private static MyApplication application;
    public static Context appContext;
    private static Handler handler;
    private static int mainThreadId;

    public static MyApplication getApp() {
        if (application != null && application instanceof MyApplication) {
            return (MyApplication) application;
        } else {
            application = new MyApplication();
            application.onCreate();
            return (MyApplication) application;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        application = this;

        //獲取Context
        appContext = getApplicationContext();

        //創建handler
        handler = new Handler(Looper.getMainLooper());

        //獲取主線程id
        mainThreadId = android.os.Process.myTid();
        PreferenceManager.getDefaultSharedPreferences(appContext);

         //初始化RTMPC引擎
        RTMPCHybird.Inst().Init(appContext);
        RTMPCHybird.Inst().InitEngineWithAnyrtcInfo(Constant.DEVELOPERID, Constant.APPID,  Constant.APPKEY,  Constant.APPTOKEN);
    }
    @Override
    public File getCacheDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cacheDir = getExternalCacheDir();
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir;
            }
        }

        File cacheDir = super.getCacheDir();

        return cacheDir;
    }

    public static Context getContext() {
        return appContext;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
