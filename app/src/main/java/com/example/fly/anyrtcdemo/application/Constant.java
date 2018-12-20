package com.example.fly.anyrtcdemo.application;

public class Constant {
    public static final String DEVELOPERID = "64822080";
    public static final String APPID = "anyrtcutFSNh5B6zjf";
    public static final String APPKEY = "an5JaAf/MPAP/jMo0fLyxmOCvKvmFU0SawYnLxkyNqc";
    public static final String APPTOKEN = "13665d619c0a49786e7c9f752684a580";

    public final static String gHttpLiveListUrl = "http://%s/anyapi/V1/livelist?AppID=%s&DeveloperID=%s";
    public final static String gHttpRecordUrl = "http://%s/anyapi/V1/recordrtmp?AppID=%s&DeveloperID=%s&AnyrtcID=%s&Url=%s&ResID=%s";
    public final static String gHttpCloseRecUrl = "http://%s/anyapi/V1/closerecrtmp?AppID=%s&DeveloperID=%s&VodSvrID=%s&VodResTag=%s";

    //rtmp 推流地址
    public static final String RTMP_PUSH_URL = "rtmp://live.hkstv.hk.lxdns.com:1935/live/%s";

    //rtmp 拉流地址
    public static final String RTMP_PULL_URL = "rtmp://live.hkstv.hk.lxdns.com:1935/live/%s";

     //hls 地址
    public static final String HLS_URL = "http://192.169.7.207/live/%s.m3u8";
}
