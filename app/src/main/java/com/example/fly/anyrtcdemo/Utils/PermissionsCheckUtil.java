package com.example.fly.anyrtcdemo.Utils;

import android.app.Activity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;


public class PermissionsCheckUtil {

    private static String[] PHONE_MTYB = new String[]{"sanxing", "xiaomi"};
    // 音頻獲取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 設置音頻採樣率，44100是目前的標準
    public static int sampleRateInHz = 44100;
    // 設置音頻的錄製聲道CHANNEL_IN_STEREO為雙聲道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音頻數據格式:PCM 16位每個樣本,保證設備支持
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 緩衝區大小
    public static int bufferSizeInBytes = 0;

    public interface RequestPermissionListener {

        //權限已打開,手機版本小於Andorid 6.0調用
        void requestPermissionSuccess();

        //權限未打開,手機版本小於Andorid 6.0調用
        void requestPermissionFailed();

        //手機版本大於6.0調用
        void requestPermissionThanSDK23();
    }

    // 判断當前手機版本是否大於等於Android 6.0
    private static boolean thanSDK23() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    //檢查相機的權限是否打開
    public static void isOpenCarmaPermission(RequestPermissionListener listener) {
        if (thanSDK23()) {
            listener.requestPermissionThanSDK23();
        } else {
            final android.hardware.Camera.Parameters parameters;
            android.hardware.Camera camera = null;
            try {
                camera = android.hardware.Camera.open(0);//前置相機
                parameters = camera.getParameters();
                listener.requestPermissionSuccess();
            } catch (RuntimeException e) {
                listener.requestPermissionFailed();

            } finally {
                if (camera != null) {
                    camera.release();
                }
            }
        }
    }

    //判斷是否有錄音權限
    public static boolean isOpenRecordAudioPermission(RequestPermissionListener listener) {
        if (thanSDK23()) {
            listener.requestPermissionThanSDK23();
        } else {
            bufferSizeInBytes = 0;
            bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
            //開始錄製音頻
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                listener.requestPermissionFailed();
                return false;
            }

            //根據開始錄音判斷是否有錄音權限
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                listener.requestPermissionFailed();
                return false;
            }

            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;

            listener.requestPermissionSuccess();
        }
        return true;
    }

    public static void showMissingPermissionDialog(final Activity activity, String message) {
        boolean canSetting = false;
        String mtyb = Build.BRAND;
        for (int i = 0; i < PHONE_MTYB.length; i++) {
            if (PHONE_MTYB[i].equalsIgnoreCase(mtyb)) {
                canSetting = true;
                break;
            } else {
                canSetting = false;
            }
        }
    }
}
