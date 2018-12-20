package com.example.fly.anyrtcdemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.firebaseClass.Account;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import github.ishaan.buttonprogressbar.ButtonProgressBar;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private ButtonProgressBar signUp;
    private TextInputLayout accoutLayout;
    private TextInputLayout passwordLayout;
    private EditText accountEdit;
    private EditText passwordEdit;
    private ButtonProgressBar login;
    private SoundPool soundPool;
    private int music;
    private FirebaseAuth auth = FirebaseAuth.getInstance();;

    //Gmail
    private static final int SIGN_IN_GOOGLE_CODE = 1;
    private SignInButton Google_login;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        signUp();
        loginMail();
        Gmail();

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        music = soundPool.load(this, R.raw.donln, 1);

        SharedPreferences login = getSharedPreferences("login", MODE_PRIVATE);
        accountEdit.setText(login.getString("account",""));
        passwordEdit.setText(login.getString("password",""));
    }

    private void initView() {
        signUp = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);
        accountEdit =  findViewById(R.id.account_edit);
        passwordEdit =  findViewById(R.id.password_edit);
        accoutLayout =  findViewById(R.id.account_layout);
        passwordLayout =  findViewById(R.id.password_layout);
        accoutLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        Google_login = findViewById(R.id.Google_login);
    }

    private void loginMail(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        soundPool.play(music,1,1,1,0,1);
                        final String account = accountEdit.getText().toString();
                        final String password = passwordEdit.getText().toString();
                        if(TextUtils.isEmpty(account)){
                            accoutLayout.setError("請輸入帳號");
                            return;
                        }
                        if(TextUtils.isEmpty(password)){
                            passwordLayout.setError("請輸入密碼");
                            return;
                        }
                        auth.signInWithEmailAndPassword(account,password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            login.stopLoader();
                                            SharedPreferences login = getSharedPreferences("login", MODE_PRIVATE);
                                            login.edit().putString("account",account)
                                                    .putString("password",password)
                                                    .apply();
                                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            login.reset();
                                            CookieBar.build(LoginActivity.this)
                                                    .setTitle("登入失敗")
                                                    .setLayoutGravity(Gravity.BOTTOM)
                                                    .setDuration(3000)
                                                    .show();
                                        }
                                    }
                                });
                    }
                },1000);
            }
        });
    }



    private void signUp(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        soundPool.play(music,1,1,1,0,1);
                        final String email = accountEdit.getText().toString();
                        final String password = passwordEdit.getText().toString();
                        if(TextUtils.isEmpty(email)){
                            accoutLayout.setError("請輸入帳號");
                            return;
                        }
                        if(TextUtils.isEmpty(password)){
                            passwordLayout.setError("請輸入密碼");
                            return;
                        }

                        auth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            signUp.stopLoader();
                                            Account account = new Account(email,password);
                                            FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid()).setValue(account);//隨機取得一個ID並且寫入帳號與密碼
                                            CookieBar.build(LoginActivity.this)
                                                    .setTitle("註冊成功")
                                                    .setLayoutGravity(Gravity.BOTTOM)
                                                    .setDuration(3000)
                                                    .show();
                                        }else{
                                            signUp.reset();
                                            CookieBar.build(LoginActivity.this)
                                                    .setTitle("註冊失敗")
                                                    .setLayoutGravity(Gravity.BOTTOM)
                                                    .setDuration(3000)
                                                    .show();
                                        }
                                    }

                                });
                    }
                },1500);
            }
        });
    }


    public void Gmail(){
        Google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(music,1,1,1,0,1);
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //validando el request code
        if (requestCode == SIGN_IN_GOOGLE_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInGoogleFirebase(googleSignInResult);
        }
    }
    private void signInGoogleFirebase(GoogleSignInResult googleSignInResult){
        if (googleSignInResult.isSuccess()){
            AuthCredential authCredential =
                    GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(),null);
            auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "網路差", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this, "無此帳號", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }



}
