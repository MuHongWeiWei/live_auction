package com.example.fly.anyrtcdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.fragment.ClassFragment;
import com.example.fly.anyrtcdemo.fragment.LiveFragment;
import com.example.fly.anyrtcdemo.fragment.MemberDataFragment;
import com.example.fly.anyrtcdemo.fragment.SerachFragment;
import com.example.fly.anyrtcdemo.fragment.TopFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.aviran.cookiebar2.CookieBar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    String name;
    private ImageView kpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(name == null){
            name = "未設定暱稱";
        }


        CookieBar.build(HomeActivity.this)
                .setTitle( name + "," + "歡迎回來")
                .show();


        Button btnClass = findViewById(R.id.btn_class);
        Button btnTop = findViewById(R.id.btn_top);
        Button btnLive = findViewById(R.id.btn_live);
        Button btnSerach = findViewById(R.id.btn_serach);
        Button btnMemberData = findViewById(R.id.btn_memberdata);
        kpl = findViewById(R.id.iv_kpl);

        btnClass.setOnClickListener(this);
        btnTop.setOnClickListener(this);
        btnLive.setOnClickListener(this);
        btnSerach.setOnClickListener(this);
        btnMemberData.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_class:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, ClassFragment.getInstance()).commit();
                kpl.setVisibility(View.GONE);
                break;
            case R.id.btn_top:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, TopFragment.getInstance()).commit();
                kpl.setVisibility(View.GONE);
                break;
            case R.id.btn_live:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, LiveFragment.getInstance()).commit();
                kpl.setVisibility(View.GONE);
                break;
            case R.id.btn_serach:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, SerachFragment.getInstance()).commit();
                kpl.setVisibility(View.GONE);
                break;
            case R.id.btn_memberdata:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MemberDataFragment.getInstance()).commit();
                kpl.setVisibility(View.GONE);
                break;


        }
    }
}
