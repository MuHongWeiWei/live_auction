package com.example.fly.anyrtcdemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fly.anyrtcdemo.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity{


    private ImageView iv;
    private boolean change = false;
    private TextView tv;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        ImageView iv2 = findViewById(R.id.iv2);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
                return true;
            }
        });

        iv2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
                return true;
            }
        });



        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(change){
                            change = false;
                            tv.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                        }else{
                            change = true;
                            tv.setTextColor(Color.RED);
                        }
                    }
                });
            }
        };

        timer.schedule(task,1,750);
    }
}
