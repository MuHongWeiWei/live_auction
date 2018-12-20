package com.example.fly.anyrtcdemo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
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

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.SoftKeyboardUtil;
import com.example.fly.anyrtcdemo.Utils.ThreadUtil;
import com.example.fly.anyrtcdemo.adapter.LiveChatAdapter;
import com.example.fly.anyrtcdemo.bean.ChatMessageBean;
import com.example.fly.anyrtcdemo.firebaseClass.Live;
import com.example.fly.anyrtcdemo.weight.ScrollRecycerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import org.anyrtc.rtmpc_hybird.RTMPCAbstractGuest;
import org.anyrtc.rtmpc_hybird.RTMPCGuestKit;
import org.anyrtc.rtmpc_hybird.RTMPCHybird;
import org.anyrtc.rtmpc_hybird.RTMPCVideoView;
import org.anyrtc.utils.RTMPAudioManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.List;


public class AnyGuestActivity extends BaseActivity implements ScrollRecycerView.ScrollPosation {

    private static final int CLOSED = 0;
    private RTMPCGuestKit mGuestKit;
    private RTMPCVideoView mVideoView;
    private boolean mStartLine = false;
    private String mNickname;
    private String mRtmpPullUrl;
    private String mAnyrtcId;
    private String mHlsUrl;
    private String mGuestId;
    private JSONObject mUserData;
    private String mTopic;
    private String headerUrl;
    private SoftKeyboardUtil softKeyboardUtil;
    private int duration = 100;//软键盘延迟打开时间
    private boolean isKeybord = false;
    private CheckBox mCheckBarrage;
    private DanmakuView mDanmakuView;
    private EditText editMessage;
    private ViewAnimator vaBottomBar;
    private LinearLayout llInputSoft;
    private FrameLayout flChatList;
    private ScrollRecycerView rcLiveChat;
    private List<ChatMessageBean> mChatMessageList;
    private LiveChatAdapter mChatLiveAdapter;
    private int maxMessageList = 150; //列表中最大 消息数目
    private RTMPAudioManager mRtmpAudioManager = null;
    private ImageView btnChat;
    private TextView tv_title;
    private RelativeLayout rl_rtmpc_videos;
    private TextView txt_watcher_number;
    private TextView tvTotal;
    private String price;

    //主播關直播
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOSED: {
                    mGuestKit.HangupRTCLine();
                    mVideoView.OnRtcRemoveRemoteRender("LocalCameraRender");
                    mStartLine = false;
                    finish();
                }
                break;
            }
        }
    };
    private TextView type;
    private TextView tv_speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_anyguest);

        getDate();

        initView();

        iniData();

    }

    public void five(View view){
        int i = Integer.parseInt(price);
        int x = i + 5;
        FirebaseDatabase.getInstance().getReference("users").child("lives").setValue(new Live(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), String.valueOf(x)));
    }

    public void ten(View view){
        int i = Integer.parseInt(price);
        int x = i + 10;
        FirebaseDatabase.getInstance().getReference("users").child("lives").setValue(new Live(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), String.valueOf(x)));
    }

    public void fifty(View view){
        int i = Integer.parseInt(price);
        int x = i + 50;
        FirebaseDatabase.getInstance().getReference("users").child("lives").setValue(new Live(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), String.valueOf(x)));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("users").
                child("lives").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Live live = dataSnapshot.getValue(Live.class);
                price = live.getPrice();
                tvTotal.setText(price);
                tv_speaker.setText(live.getViewer());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDate() {
        mChatMessageList = new ArrayList<ChatMessageBean>();
        mNickname = getIntent().getExtras().getString("nickname","請去改名字");
        headerUrl = getIntent().getExtras().getString("headUrl");
        mRtmpPullUrl = getIntent().getExtras().getString("rtmp_url");
        mAnyrtcId = getIntent().getExtras().getString("anyrtcId");
        mHlsUrl = getIntent().getExtras().getString("hls_url");
        mGuestId = mNickname;
        mTopic = getIntent().getExtras().getString("topic");
    }

    private void initView() {
        type = findViewById(R.id.tv_type);
        tvTotal = findViewById(R.id.tv_total);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_speaker = (TextView) findViewById(R.id.tv_speaker);
        mCheckBarrage = (CheckBox) findViewById(R.id.check_barrage);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        editMessage = (EditText) findViewById(R.id.edit_message);
        vaBottomBar = (ViewAnimator) findViewById(R.id.va_bottom_bar);
        llInputSoft = (LinearLayout) findViewById(R.id.ll_input_soft);
        flChatList = (FrameLayout) findViewById(R.id.fl_chat_list);
        btnChat = (ImageView) findViewById(R.id.iv_host_text);
        rcLiveChat = (ScrollRecycerView) findViewById(R.id.rc_live_chat);

        rl_rtmpc_videos = (RelativeLayout) findViewById(R.id.rl_rtmpc_videos);

        txt_watcher_number = (TextView) findViewById(R.id.txt_watcher_number);

    }

    private void iniData() {
        FirebaseDatabase.getInstance().getReference("users")
                .child("lives").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Live live = dataSnapshot.getValue(Live.class);
                        type.setText(live.getType());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        tv_title.setText(mTopic);
        mChatLiveAdapter = new LiveChatAdapter(mChatMessageList, this);
        rcLiveChat.setLayoutManager(new LinearLayoutManager(this));
        rcLiveChat.setAdapter(mChatLiveAdapter);
        rcLiveChat.addScrollPosation(this);
        setEditTouchListener();
        vaBottomBar.setAnimateFirstView(true);
        //设置流
        setStream();

    }

    private void setStream() {
        //设置横屏模式，也可sdk初始化时进行设置
        //RTMPCHybird.Inst().SetScreenToLandscape();
        mVideoView = new RTMPCVideoView(rl_rtmpc_videos, RTMPCHybird.Inst().Egl(), false);

        mVideoView.setBtnCloseEvent(mBtnVideoCloseEvent);

        {

            mRtmpAudioManager = RTMPAudioManager.create(this, new Runnable() {

                @Override
                public void run() {
                    onAudioManagerChangedState();
                }
            });
            // Store existing audio settings and change audio mode to
            // MODE_IN_COMMUNICATION for best possible VoIP performance.
            mRtmpAudioManager.init();
        }

        mUserData = new JSONObject();
        try {
            mUserData.put("nickName", mNickname);
            mUserData.put("headUrl",headerUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 初始化rtmp播放器
         */
        mGuestKit = new RTMPCGuestKit(this, mGuestListener);
        VideoRenderer render = mVideoView.OnRtcOpenLocalRender(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        /**
         * 开始播放rtmp流
         */
        mGuestKit.StartRtmpPlay(mRtmpPullUrl, render.GetRenderPointer());
        /**
         * 开启RTC连线连接
         */
        mGuestKit.JoinRTCLine(mAnyrtcId, mGuestId, mUserData.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mStartLine) {
                mGuestKit.HangupRTCLine();
                mVideoView.OnRtcRemoveRemoteRender("LocalCameraRender");

                finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
        softKeyboardUtil.removeGlobalOnLayoutListener(this);
        // Close RTMPAudioManager
        if (mRtmpAudioManager != null) {
            mRtmpAudioManager.close();
            mRtmpAudioManager = null;

        }

        /**
         * 销毁rtmp播放器
         */
        if (mGuestKit != null) {
            mGuestKit.Clear();
            mVideoView.OnRtcRemoveLocalRender();
            mGuestKit = null;
        }
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if
        // AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
    }

    public void OnBtnClicked(View btn) {
        if (btn.getId() == R.id.btn_send_message) {
            String message = editMessage.getText().toString();
            editMessage.setText("");
            if (message.equals("")) {
                return;
            }
            if (mCheckBarrage.isChecked()) {
                mGuestKit.SendBarrage(mNickname, headerUrl, message);
                IDanmakuItem item = new DanmakuItem(AnyGuestActivity.this, new SpannableString(mNickname+":"+message), mDanmakuView.getWidth(), 0, R.color.colorAccent, 18, 1);
                mDanmakuView.addItemToHead(item);
            } else {
                mGuestKit.SendUserMsg(mNickname, headerUrl, message);
                addChatMessageList(new ChatMessageBean(mNickname, mNickname,headerUrl, message));//TODO 此处弹幕开关未开启时,消息在消息列表显示
            }
        } else if (btn.getId() == R.id.iv_host_text) {
            btnChat.clearFocus();
            vaBottomBar.setDisplayedChild(1);
            editMessage.requestFocus();
            softKeyboardUtil.showKeyboard(AnyGuestActivity.this, editMessage);
        }
    }

    /**
     * 设置 键盘的监听事件
     */
    private void setEditTouchListener() {
        softKeyboardUtil = new SoftKeyboardUtil();

        softKeyboardUtil.observeSoftKeyboard(AnyGuestActivity.this, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow) {
                if (isShow) {
                    ThreadUtil.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isKeybord) {
                                isKeybord = true;
                                llInputSoft.animate().translationYBy(-editMessage.getHeight() / 3).setDuration(100).start();
                                flChatList.animate().translationYBy(-editMessage.getHeight() / 3).setDuration(100).start();
                            }

                        }
                    }, duration);
                } else {
                    btnChat.requestFocus();
                    vaBottomBar.setDisplayedChild(0);
                    llInputSoft.animate().translationYBy(editMessage.getHeight() / 3).setDuration(100).start();
                    flChatList.animate().translationYBy(editMessage.getHeight() / 3).setDuration(100).start();
                    isKeybord = false;
                }
            }
        });
    }

    /**
     * 更新列表
     *
     * @param chatMessageBean
     */
    private void addChatMessageList(ChatMessageBean chatMessageBean) {
        // 150 条 修改；

        if (mChatMessageList == null) {
            return;
        }

        if (mChatMessageList.size() < maxMessageList) {
            mChatMessageList.add(chatMessageBean);
        } else {
            mChatMessageList.remove(0);
            mChatMessageList.add(chatMessageBean);
        }
        mChatLiveAdapter.notifyDataSetChanged();
        rcLiveChat.smoothScrollToPosition(mChatMessageList.size() - 1);
    }


    @Override
    public void ScrollButtom() {
    }

    /**
     * 连线时小图标的关闭按钮连接
     */
    private RTMPCVideoView.BtnVideoCloseEvent mBtnVideoCloseEvent = new RTMPCVideoView.BtnVideoCloseEvent() {

        @Override
        public void CloseVideoRender(View view, String strPeerId) {
            /**
             * 挂断连线
             */
            mGuestKit.HangupRTCLine();
            mVideoView.OnRtcRemoveRemoteRender("LocalCameraRender");
            mStartLine = false;
        }

        @Override
        public void OnSwitchCamera(View view) {
            /**
             * 连线时切换游客摄像头
             */
            mGuestKit.SwitchCamera();
        }
    };

    /**
     * 观看直播回调信息接口
     */
    private RTMPCAbstractGuest mGuestListener = new RTMPCAbstractGuest() {

        @Override
        public void OnRTCJoinLineResult(int code, String strReason) {

        }

        //rtmp連接成功
        @Override
        public void OnRtmplayerOKCallback() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(3000);
                        JSONObject json = new JSONObject();
                        try {//主播的信息
                            json.put("guestId", mNickname);
                            json.put("headUrl", headerUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //像主播連線
                        if(mGuestKit != null){
                            mGuestKit.ApplyRTCLine(json.toString());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

      //主播未開啟直播
        @Override
        public void OnRTCJoinLineResultCallback(final int code, String strReason) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (code == 0) {

                    } else if (code == 101) {
                        Toast.makeText(AnyGuestActivity.this, R.string.str_hoster_not_live, Toast.LENGTH_LONG).show();
                        mHandler.sendEmptyMessageDelayed(CLOSED, 2000);
                    }
                }
            });
        }



      //關閉直播
        @Override
        public void OnRTCLineLeaveCallback(int code, String strReason) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AnyGuestActivity.this, R.string.str_hoster_leave, Toast.LENGTH_LONG).show();
                    mHandler.sendEmptyMessageDelayed(CLOSED, 2000);


                }
            });
        }



        //聊天框訊息
        @Override
        public void OnRTCUserMessageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strMessage) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addChatMessageList(new ChatMessageBean(strCustomID, strCustomName, strCustomHeader, strMessage));
                }
            });
        }

       //彈幕
        @Override
        public void OnRTCUserBarrageCallback(final String strCustomID, final String strCustomName, final String strCustomHeader, final String strBarrage) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    addChatMessageList(new ChatMessageBean(strCustomID, strCustomName,strCustomHeader, strBarrage));//TODO 把获取到的弹幕消息添加到消息列表
                    IDanmakuItem item = new DanmakuItem(AnyGuestActivity.this, new SpannableString(strCustomName+":"+strBarrage), mDanmakuView.getWidth(), 0, R.color.colorAccent, 18, 1);
                    mDanmakuView.addItemToHead(item);
                }
            });
        }

       //觀看直播人數
        @Override
        public void OnRTCMemberListWillUpdateCallback(final int totalMembers) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_watcher_number.setText(String.format(getString(R.string.str_live_watcher_number), totalMembers -1));
                }
            });
        }

         //觀眾上下線
        @Override
        public void OnRTCMemberCallback(final String strCustomID, final String strUserData) {
            AnyGuestActivity.this.runOnUiThread(new Runnable() {
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

       //直播觀看總人數更新
        @Override
        public void OnRTCMemberListUpdateDoneCallback() {

        }

        //連接後的更新
        @Override
        public void OnRTCOpenVideoRenderCallback(final String strLivePeerID) {

        }

        //連麥關掉之後的更新
        @Override
        public void OnRTCCloseVideoRenderCallback(final String strLivePeerID) {

        }

        @Override
        public void OnRtmplayerStatusCallback(int i, int i1) {

        }

        //緩衝時間
        @Override
        public void OnRtmplayerCacheCallback(int time) {

        }

        //觀眾申請連賣更新
        @Override
        public void OnRTCApplyLineResultCallback(final int code) {

        }

        @Override
        public void OnRTCOtherLineOpenCallback(String strLivePeerID, String strCustomID, String strUserData) {

        }

        //其他用戶連線更新
        @Override
        public void OnRTCOtherLineCloseCallback(String strLivePeerID) {

        }


        //連線掛掉更新
        @Override
        public void OnRTCHangupLineCallback() {

        }


        @Override
        public void OnRtmplayerClosedCallback(int errcode) {

        }
    };

}
