package com.example.fly.anyrtcdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.fly.anyrtcdemo.firebaseClass.Live;
import com.example.fly.anyrtcdemo.bean.ChatMessageBean;
import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.RTMPCHttpSDK;
import com.example.fly.anyrtcdemo.Utils.SoftKeyboardUtil;
import com.example.fly.anyrtcdemo.Utils.ThreadUtil;
import com.example.fly.anyrtcdemo.adapter.LiveChatAdapter;
import com.example.fly.anyrtcdemo.application.Constant;
import com.example.fly.anyrtcdemo.weight.ScrollRecycerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import org.anyrtc.rtmpc_hybird.RTMPCAbstractHoster;
import org.anyrtc.rtmpc_hybird.RTMPCHosterKit;
import org.anyrtc.rtmpc_hybird.RTMPCHybird;
import org.anyrtc.rtmpc_hybird.RTMPCVideoView;
import org.anyrtc.utils.RTMPAudioManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.List;

public class AnyHosterActivity extends BaseActivity implements ScrollRecycerView.ScrollPosation {
    private String mNickname, mRtmpPushUrl, mAnyrtcId, mHlsUrl, mGuestId, mUserData, mTopic, mHosterId, mVodSvrId, mVodResTag, header,mtype;
    private int duration = 100, maxMessageList = 150;
    private TextView tv_title, txt_watcher_number,price;
    private ImageView btnChat, iv_camera;
    private CheckBox mCheckBarrage;
    private RTMPCHosterKit mHosterKit;
    private RTMPCVideoView mVideoView;
    private SoftKeyboardUtil softKeyboardUtil;
    private DanmakuView mDanmakuView;
    private EditText editMessage;
    private ViewAnimator vaBottomBar;
    private LinearLayout llInputSoft;
    private FrameLayout flChatList;
    private ScrollRecycerView rcLiveChat;
    private RelativeLayout titleBar;
    private List<ChatMessageBean> mChatMessageList;
    private LiveChatAdapter mChatLiveAdapter;
    private RelativeLayout rl_rtmpc_videos;
    private RTMPAudioManager mRtmpAudioManager = null;
    private AudioManager audioManager;
    private TextView tv_type;
    private int mprice;
    private TextView speaker;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_anyhoster);

        getDate();//取資料
        initView();//初始化元件
        iniData();//初始化資料
        setOnClick();//設定按鈕監聽
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
    }

    private void iniData() {
        price.setText(mprice + "$");
        tv_type.setText(mtype);
        iv_camera.setImageResource(R.drawable.em_camera_switch_selector);
        tv_title.setText(mTopic);
        titleBar.setBackgroundColor(getResources().getColor(R.color.qq));//標題設置透明化
        mChatLiveAdapter = new LiveChatAdapter(mChatMessageList, this);//聊天清單
        rcLiveChat.setLayoutManager(new LinearLayoutManager(this));//訊息布局
        rcLiveChat.setAdapter(mChatLiveAdapter);
        rcLiveChat.addScrollPosation(this);
        vaBottomBar.setAnimateFirstView(true);
        setEditTouchListener();//鍵盤
        setStream();//設置流
    }


    private void setStream() {
        //設定橫屏模式  當主播端設置後 觀眾端也必須設置橫屏模式
        mVideoView = new RTMPCVideoView(rl_rtmpc_videos, RTMPCHybird.Inst().Egl(), true);
        mVideoView.setBtnCloseEvent(mBtnVideoCloseEvent);//切換鏡頭的前後
        mHosterKit = new RTMPCHosterKit(this, mHosterListener);
        {
            VideoRenderer render = mVideoView.OnRtcOpenLocalRender(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            mHosterKit.SetVideoCapturer(render.GetRenderPointer(), true);
        }
        {
            mRtmpAudioManager = RTMPAudioManager.create(this, new Runnable() {
                @Override
                public void run() {
                    onAudioManagerChangedState();
                }
            });
            mRtmpAudioManager.init();
        }

        //設定自訂碼
        mHosterKit.SetNetAdjustMode(RTMPCHosterKit.RTMPNetAdjustMode.RTMP_NA_Fast);

        //開始推流
        mHosterKit.StartPushRtmpStream(mRtmpPushUrl);

        //RTC連線
        mHosterKit.OpenRTCLine(mAnyrtcId, mHosterId, mUserData);

    }

    private void getDate() {
        mprice = getIntent().getExtras().getInt("price");

        mChatMessageList = new ArrayList<ChatMessageBean>();
        mNickname = getIntent().getExtras().getString("nickname");
        mHosterId = getIntent().getExtras().getString("hosterId");
        mRtmpPushUrl = getIntent().getExtras().getString("rtmp_url");
        mAnyrtcId = getIntent().getExtras().getString("andyrtcId");
        mUserData = getIntent().getExtras().getString("userData");
        mHlsUrl = getIntent().getExtras().getString("hls_url");
        mTopic = getIntent().getExtras().getString("topic");
        header = getIntent().getExtras().getString("headUrl");
        mtype = getIntent().getExtras().getString("type");
    }

    private void setOnClick() {
        //相機鏡頭轉向
        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHosterKit.SwitchCamera();
            }
        });
    }

    private void initView() {
        iv_camera = findViewById(R.id.iv_camera);
        tv_title = findViewById(R.id.tv_title);
        tv_type = findViewById(R.id.tv_type);
        price = findViewById(R.id.tv_price);
        speaker = findViewById(R.id.tv_speak);
        titleBar = findViewById(R.id.title);
        mDanmakuView = findViewById(R.id.danmakuView);
        mCheckBarrage = findViewById(R.id.check_barrage);
        editMessage = findViewById(R.id.edit_message);
        vaBottomBar = findViewById(R.id.va_bottom_bar);
        llInputSoft = findViewById(R.id.ll_input_soft);
        flChatList = findViewById(R.id.fl_chat_list);
        btnChat = findViewById(R.id.iv_host_text);
        rcLiveChat = findViewById(R.id.rc_live_chat);
        rl_rtmpc_videos = findViewById(R.id.rl_rtmpc_videos);
        txt_watcher_number = findViewById(R.id.txt_watcher_number);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("users")
                .child("lives")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Live live =dataSnapshot.getValue(Live.class);
                        price.setText(live.getPrice());
                        speaker.setText(live.getViewer());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ShowExitDialog();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
        softKeyboardUtil.removeGlobalOnLayoutListener(this);

        if (mVodSvrId != null && mVodSvrId.length() > 0 && mVodResTag.length() > 0) {
            //關閉直播
            RTMPCHttpSDK.CloseRecRtmpStream(getApplicationContext(), RTMPCHybird.Inst().GetHttpAddr(), Constant.DEVELOPERID, Constant.APPID, Constant.APPTOKEN, mVodSvrId, mVodResTag);
        }

        if (mHosterKit != null) {
            mVideoView.OnRtcRemoveLocalRender();
            mHosterKit.Clear();
            mHosterKit = null;
        }
    }


    @Override
    public void ScrollButtom() {

    }



    //取得聲音訊息
    private void onAudioManagerChangedState() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

    }

    //按下聊天與送出
    public void OnBtnClicked(View btn) {
        if (btn.getId() == R.id.btn_send_message) {
            String message = editMessage.getText().toString();
            editMessage.setText("");
            if (TextUtils.isEmpty(message)) {
                return;
            }
            //彈幕
            if (mCheckBarrage.isChecked()) {
                mHosterKit.SendBarrage(mNickname, header, message);
                IDanmakuItem item = new DanmakuItem(AnyHosterActivity.this, new SpannableString(mNickname + ":" + message), mDanmakuView.getWidth(), 0, R.color.colorAccent, 18, 1);
                mDanmakuView.addItemToHead(item);
            } else {
                mHosterKit.SendUserMsg(mNickname, header, message);
                addChatMessageList(new ChatMessageBean(mNickname, mNickname, header, message));
            }
        } else if (btn.getId() == R.id.iv_host_text) {
            btnChat.clearFocus();
            vaBottomBar.setDisplayedChild(1);
            editMessage.requestFocus();
            softKeyboardUtil.showKeyboard(AnyHosterActivity.this, editMessage);
        }
    }

    //聊天訊息框
    private void addChatMessageList(ChatMessageBean chatMessageBean) {
        if (mChatMessageList == null) {
            return;
        }
        //訊息大於150則,清空訊息框
        if (mChatMessageList.size() < maxMessageList) {
            mChatMessageList.add(chatMessageBean);
        } else {
            mChatMessageList.remove(0);
            mChatMessageList.add(chatMessageBean);
        }
        mChatLiveAdapter.notifyDataSetChanged();
        rcLiveChat.smoothScrollToPosition(mChatMessageList.size() - 1);
    }

    //鍵盤的視窗大小
    private void setEditTouchListener() {
        softKeyboardUtil = new SoftKeyboardUtil();

        softKeyboardUtil.observeSoftKeyboard(AnyHosterActivity.this, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow) {
                if (isShow) {
                    ThreadUtil.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            llInputSoft.animate().translationYBy(-editMessage.getHeight() / 2).setDuration(100).start();
                            flChatList.animate().translationYBy(-editMessage.getHeight() / 2).setDuration(100).start();
                        }
                    }, duration);
                } else {
                    btnChat.requestFocus();
                    vaBottomBar.setDisplayedChild(0);
                    llInputSoft.animate().translationYBy(editMessage.getHeight() / 2).setDuration(100).start();
                    flChatList.animate().translationYBy(editMessage.getHeight() / 2).setDuration(100).start();
                }
            }
        });
    }

    //退出對話框
    private void ShowExitDialog() {
        View view = View.inflate(AnyHosterActivity.this, R.layout.layout_dialog_rtc, null);
        TextView tv_delete_title = (TextView) view.findViewById(R.id.tv_delete_title);
        TextView tv_rtc_nick = (TextView) view.findViewById(R.id.tv_rtc_nick);
        ImageView iv_trc_avatar = (ImageView) view.findViewById(R.id.iv_trc_avatar);
        iv_trc_avatar.setVisibility(View.GONE);
        tv_delete_title.setText(getString(R.string.str_exit));
        tv_rtc_nick.setText(R.string.str_live_stop);
        new AlertDialog.Builder(AnyHosterActivity.this).setView(view).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHosterKit.StopRtmpStream();
                finish();
            }
        }).setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    //主播信息
    private RTMPCAbstractHoster mHosterListener = new RTMPCAbstractHoster() {
        //連線成功
        @Override
        public void OnRtmpStreamOKCallback() {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //開始直播
                    RTMPCHttpSDK.RecordRtmpStream(AnyHosterActivity.this, RTMPCHybird.Inst().GetHttpAddr(), Constant.DEVELOPERID, Constant.APPID, Constant.APPTOKEN, mAnyrtcId, mRtmpPushUrl, mAnyrtcId, new RTMPCHttpSDK.RTMPCHttpCallback() {
                        @Override
                        public void OnRTMPCHttpOK(String strContent) {
                            try {
                                JSONObject recJson = new JSONObject(strContent);
                                mVodSvrId = recJson.getString("VodSvrId");
                                mVodResTag = recJson.getString("VodResTag");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void OnRTMPCHttpFailed(int code) {

                        }
                    });
                }
            });
        }

        //重新連線
        @Override
        public void OnRtmpStreamReconnectingCallback(final int times) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        //推流狀態
        @Override
        public void OnRtmpStreamStatusCallback(final int delayMs, final int netBand) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        //推流失敗
        @Override
        public void OnRtmpStreamFailedCallback(int code) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        //推流關閉
        @Override
        public void OnRtmpStreamClosedCallback() {
            finish();
        }


        //RTC開啟連線
        @Override
        public void OnRTCOpenLineResultCallback(final int code, String strErr) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }


        //申請連線
        @Override
        public void OnRTCApplyToLineCallback(final String strLivePeerID, final String strCustomID, final String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHosterKit.AcceptRTCLine(strLivePeerID);
                }
            });
        }

        //視頻連線超過4人
        @Override
        public void OnRTCLineFullCallback(final String strLivePeerID, String strCustomID, String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AnyHosterActivity.this, getString(R.string.str_connect_full), Toast.LENGTH_LONG).show();
                    mHosterKit.RejectRTCLine(strLivePeerID, true);
                }
            });
        }

        ///觀眾掛斷
        @Override
        public void OnRTCCancelLineCallback(String strLivePeerID) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        //RTC連線關閉
        @Override
        public void OnRTCLineClosedCallback(final int code, String strReason) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 207) {
                        Toast.makeText(AnyHosterActivity.this, getString(R.string.str_apply_anyrtc_account), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }

        //連賣後的視訊
        @Override
        public void OnRTCOpenVideoRenderCallback(final String strLivePeerID) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    audioManager.setSpeakerphoneOn(false);
                }
            });
        }

        //連麥關掉之後的更新
        @Override
        public void OnRTCCloseVideoRenderCallback(final String strLivePeerID) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        //聊天框訊息
        @Override
        public void OnRTCUserMessageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strMessage) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessageList(new ChatMessageBean(strCustomID, strCustomName, strCustomHeader, strMessage));
                }
            });
        }

        //彈幕
        @Override
        public void OnRTCUserBarrageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strBarrage) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IDanmakuItem item = new DanmakuItem(AnyHosterActivity.this, new SpannableString(strCustomName + ":" + strBarrage), mDanmakuView.getWidth(), 0, R.color.colorAccent, 18, 1);
                    mDanmakuView.addItemToHead(item);
                }
            });
        }

        //直播觀看人數
        @Override
        public void OnRTCMemberListWillUpdateCallback(final int totalMembers) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_watcher_number.setText(String.format(getString(R.string.str_live_watcher_number), totalMembers -1));
                }
            });
        }

        //觀眾上下線
        @Override
        public void OnRTCMemberCallback(final String strCustomID, final String strUserData) {
            AnyHosterActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject userData = new JSONObject(strUserData);
                        addChatMessageList(new ChatMessageBean(userData.getString("nickName"), "", userData.getString("headUrl"), ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //觀看人數更新
        @Override
        public void OnRTCMemberListUpdateDoneCallback() {

        }
    };


    private RTMPCVideoView.BtnVideoCloseEvent mBtnVideoCloseEvent = new RTMPCVideoView.BtnVideoCloseEvent() {


        @Override
        public void CloseVideoRender(View view, String s) {

        }

        @Override
        public void OnSwitchCamera(View view) {
            mHosterKit.SwitchCamera();
        }
    };

}
