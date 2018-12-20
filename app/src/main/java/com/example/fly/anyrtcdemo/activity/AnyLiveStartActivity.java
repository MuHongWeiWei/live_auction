package com.example.fly.anyrtcdemo.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.PermissionsCheckUtil;
import com.example.fly.anyrtcdemo.Utils.QRCodeUtil;
import com.example.fly.anyrtcdemo.Utils.RTMPCHttpSDK;
import com.example.fly.anyrtcdemo.application.Constant;
import com.example.fly.anyrtcdemo.firebaseClass.Live;
import com.google.firebase.database.FirebaseDatabase;
import com.zhy.m.permission.MPermissions;

import org.json.JSONException;
import org.json.JSONObject;

public class AnyLiveStartActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_nickname;
    private String nick, header = null;
    private EditText et_theme;
    private Button btn_start;
    private static final int REQUECT_CODE_RECORD = 0;
    private static final int REQUECT_CODE_CAMERA = 1;
    private EditText price;
    private String type;
    private ImageView QRcode;
    private TextView type1;
    private String money;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_anylivestart);

        getDate();

        initView();

        iniData();

        setOnClick();

        getDevicePermission();
    }

    private void getDate() {
        //取得MainActivity的暱稱與圖像
        nick = getIntent().getExtras().getString("nickname", "請去改名字");
        header = getIntent().getExtras().getString("headUrl");
        type = getIntent().getExtras().getString("type");
    }

    private void initView() {
        price = findViewById(R.id.et_price);
        et_theme = findViewById(R.id.et_theme);
        tv_nickname = findViewById(R.id.tv_nickname);
        type1 = findViewById(R.id.tv_type);
        btn_start = findViewById(R.id.btn_start);
        QRcode = findViewById(R.id.QRcode);
    }


    private void iniData() {
        //顯示使用者暱稱
        tv_nickname.setText(nick);
        String type = getIntent().getStringExtra("type");
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap("看拍啦-" + type, 520, 520);
        QRcode.setImageBitmap(mBitmap);
        type1.setText("看拍啦-" + type);
    }

    private void setOnClick() {
        btn_start.setOnClickListener(this);
    }

    public void reset(View view) {
        price.setText("");
    }


    @Override
    public void onClick(View v) {
        String title = et_theme.getText().toString().trim();
        if (title.length() == 0 || title.length() >= 11) {
            Toast.makeText(this, getString(R.string.live_theme_not_null), Toast.LENGTH_SHORT).show();
        } else {
            String anyrtcId, hostID, rtmpPushUrl, rtmpPullUrl, hlsUrl;
            anyrtcId = RTMPCHttpSDK.getRandomString(12);
            hostID = anyrtcId;
            rtmpPushUrl = String.format(Constant.RTMP_PUSH_URL, anyrtcId);
            rtmpPullUrl = String.format(Constant.RTMP_PULL_URL, anyrtcId);
            hlsUrl = String.format(Constant.HLS_URL, anyrtcId);
            JSONObject item = new JSONObject();
            try {
                item.put("type", type);
                item.put("hosterId", hostID);
                item.put("rtmp_url", rtmpPullUrl);
                item.put("hls_url", hlsUrl);
                item.put("topic", title);
                item.put("nickname", nick);
                item.put("headUrl", header);
                item.put("anyrtcId", anyrtcId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("hosterId", hostID);
            bundle.putString("rtmp_url", rtmpPushUrl);
            bundle.putString("hls_url", hlsUrl);
            bundle.putString("topic", title);
            bundle.putString("headUrl", header);
            bundle.putString("nickname", nick);
            bundle.putString("andyrtcId", anyrtcId);
            bundle.putString("userData", item.toString());
            Intent intent = new Intent(AnyLiveStartActivity.this, AnyHosterActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);

//           把名稱類型金額上傳到Firebase
            money = price.getText().toString();
            if (money.equals("")) {
                money = "0";
                FirebaseDatabase.getInstance().getReference("users").child("lives").setValue(new Live("底價", money, type));
            }else{
                FirebaseDatabase.getInstance().getReference("users").child("lives").setValue(new Live("底價", money, type));
            }
        }
        }

        //獲取錄音跟相機權限
        private void getDevicePermission () {
            PermissionsCheckUtil.isOpenCarmaPermission(new PermissionsCheckUtil.RequestPermissionListener() {
                @Override
                public void requestPermissionSuccess() {

                }

                @Override
                public void requestPermissionFailed() {
                    PermissionsCheckUtil.showMissingPermissionDialog(AnyLiveStartActivity.this, getString(R.string.str_no_camera_permission));
                }

                @Override
                public void requestPermissionThanSDK23() {
                    if (ContextCompat.checkSelfPermission(AnyLiveStartActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        MPermissions.requestPermissions(AnyLiveStartActivity.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
                    }
                }
            });


            PermissionsCheckUtil.isOpenRecordAudioPermission(new PermissionsCheckUtil.RequestPermissionListener() {
                @Override
                public void requestPermissionSuccess() {

                }

                @Override
                public void requestPermissionFailed() {
                    PermissionsCheckUtil.showMissingPermissionDialog(AnyLiveStartActivity.this, getString(R.string.str_no_audio_record_permission));
                }

                @Override
                public void requestPermissionThanSDK23() {
                    if (ContextCompat.checkSelfPermission(AnyLiveStartActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        MPermissions.requestPermissions(AnyLiveStartActivity.this, REQUECT_CODE_RECORD, Manifest.permission.RECORD_AUDIO);
                    }
                }
            });
        }

    }
