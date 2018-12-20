package com.example.fly.anyrtcdemo.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.ImageHeadUtils;
import com.example.fly.anyrtcdemo.Utils.PermissionsCheckUtil;
import com.example.fly.anyrtcdemo.Utils.RTMPCHttpSDK;
import com.example.fly.anyrtcdemo.Utils.RecyclerViewUtil;
import com.example.fly.anyrtcdemo.activity.AnyGuestActivity;
import com.example.fly.anyrtcdemo.activity.AnyLiveStartActivity;
import com.example.fly.anyrtcdemo.adapter.LiveHosterAdapter;
import com.example.fly.anyrtcdemo.application.Constant;
import com.example.fly.anyrtcdemo.bean.LiveItemBean;
import com.google.firebase.auth.FirebaseAuth;
import com.zhy.m.permission.MPermissions;

import org.anyrtc.rtmpc_hybird.RTMPCHybird;
import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;

public class ClassFragment extends Fragment implements RecyclerViewUtil.RefreshDataListener, RecyclerViewUtil.ScrollingListener, BGAOnItemChildClickListener {
    private static ClassFragment instance;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LiveHosterAdapter mAdapter;
    private RecyclerViewUtil mRecyclerViewUtils;
    private List<LiveItemBean> listLive;
    private String nick, headerUrl = null;
    private static final int REQUECT_CODE_RECORD = 0;
    private static final int REQUECT_CODE_CAMERA = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);



        getDate();
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        iniData();
        setOnClick();
        getDevicePermission();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mRecyclerViewUtils.beginRefreshing();
    }


    private void getDate() {
        nick = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        headerUrl = ImageHeadUtils.getVavatar();
    }

    private void setOnClick() {
        mAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int i) {
        //點擊列表按鈕
        //判斷為驗證
        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            if(nick.equals(null)){
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("此帳號名稱未設定,是否需要設定?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment,MemberDataFragment.getInstance()).commit();
                            }
                        })
                        .setNegativeButton("否",null)
                        .setCancelable(false)
                        .show();
            }else{
                Intent intent = new Intent(getActivity(), AnyGuestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("hls_url", listLive.get(i).getmHlsUrl());
                bundle.putString("rtmp_url", listLive.get(i).getmRtmpPullUrl());
                bundle.putString("anyrtcId", listLive.get(i).getmAnyrtcId());
                bundle.putString("userData", new JSONObject().toString());
                bundle.putString("headUrl", headerUrl);
                bundle.putString("nickname", nick);
                bundle.putString("topic", listLive.get(i).getmLiveTopic());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else {
            CookieBar.build(getActivity()).setTitle("提示").setMessage("此帳號未驗證,是否需要驗證?").setLayoutGravity(Gravity.BOTTOM).setActionColor(R.color.orange).setDuration(10000).setIcon(R.drawable.login).setAction("確定", new OnActionClickListener() {
                @Override
                public void onClick() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MemberDataFragment.getInstance()).commit();
                }
            }).show();
        }



    }

    private void iniData() {
        listLive = new ArrayList<LiveItemBean>();
        //獲取直播列表
        RTMPCHttpSDK.GetLiveList(getActivity(), RTMPCHybird.Inst().GetHttpAddr(), Constant.DEVELOPERID, Constant.APPID, Constant.APPTOKEN, mRTMPCHttpCallback);
        mAdapter = new LiveHosterAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerViewUtils = new RecyclerViewUtil();
        mRecyclerViewUtils.init(getActivity(), mSwipeRefreshLayout, this.mRecyclerView, mAdapter, this);
        mRecyclerViewUtils.beginRefreshing();//第一次自動加載
        mRecyclerViewUtils.setScrollingListener(this);
        mRecyclerViewUtils.setPullUpRefreshEnable(false);
    }

    private RTMPCHttpSDK.RTMPCHttpCallback mRTMPCHttpCallback = new RTMPCHttpSDK.RTMPCHttpCallback() {
        @Override
        public void OnRTMPCHttpOK(String strContent) {
            mRecyclerViewUtils.endRefreshing();
            try {
                listLive.clear();
                JSONObject liveJson = new JSONObject(strContent);
                JSONArray liveList = liveJson.getJSONArray("LiveList");
                JSONArray memberList = liveJson.getJSONArray("LiveMembers");

                for (int i = 0; i < liveList.length(); i++) {
                    LiveItemBean bean = new LiveItemBean();
                    JSONObject itemJson = new JSONObject(liveList.getString(i));
                    bean.setmHosterId(itemJson.getString("hosterId"));
                    bean.setmRtmpPullUrl(itemJson.getString("rtmp_url"));
                    bean.setmHlsUrl(itemJson.getString("hls_url"));
                    bean.setmLiveTopic(itemJson.getString("topic"));
                    bean.setmAnyrtcId(itemJson.getString("anyrtcId"));
                    bean.setmMemNumber(memberList.getInt(i));
                    listLive.add(bean);
                }
                mAdapter.setDatas(listLive);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnRTMPCHttpFailed(int code) {
            if (listLive == null) {
                new AlertDialog.Builder(getContext()).setNeutralButton("獲取失敗", null).show();
            }
        }


    };


    //獲取錄音跟相機權限
    private void getDevicePermission() {
        PermissionsCheckUtil.isOpenCarmaPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(getActivity(), getString(R.string.str_no_camera_permission));
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(getActivity(), REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
                }
            }
        });


        PermissionsCheckUtil.isOpenRecordAudioPermission(new PermissionsCheckUtil.RequestPermissionListener() {
            @Override
            public void requestPermissionSuccess() {

            }

            @Override
            public void requestPermissionFailed() {
                PermissionsCheckUtil.showMissingPermissionDialog(getActivity(), getString(R.string.str_no_audio_record_permission));
            }

            @Override
            public void requestPermissionThanSDK23() {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MPermissions.requestPermissions(getActivity(), REQUECT_CODE_RECORD, Manifest.permission.RECORD_AUDIO);
                }
            }
        });
    }


    public static ClassFragment getInstance() {
        if (instance == null) {
            instance = new ClassFragment();
        }
        return instance;
    }

    @Override
    public void onRefresh() {
        //刷新直播列表
        RTMPCHttpSDK.GetLiveList(getActivity(), RTMPCHybird.Inst().GetHttpAddr(), Constant.DEVELOPERID, Constant.APPID, Constant.APPTOKEN, mRTMPCHttpCallback);
    }

    @Override
    public boolean loadMore() {
        return false;
    }

    @Override
    public void scroll(boolean scrollState) {

    }
}
