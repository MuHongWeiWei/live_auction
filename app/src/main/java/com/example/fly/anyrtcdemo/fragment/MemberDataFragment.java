package com.example.fly.anyrtcdemo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fly.anyrtcdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

public class MemberDataFragment extends Fragment {
    private static MemberDataFragment instance;

    private FragmentActivity fragmentActivity;
    private Button logout;
    private Button update_name;
    private TextView namefileInfo, emailfileInfo;
    private FirebaseUser user;
    private Button update_email;
    private TextView announce;
    private Button update_password;
    private Button check_email;
    private TextView checkInfo;

    // 在Fragment中取得Activity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_memberdata, container, false);

        namefileInfo = view.findViewById(R.id.name_info);
        emailfileInfo = view.findViewById(R.id.email_info);
        checkInfo = view.findViewById(R.id.checkInfo);
        logout = view.findViewById(R.id.logout);
        update_name = view.findViewById(R.id.update_name);
        update_email = view.findViewById(R.id.update_email);
        update_password = view.findViewById(R.id.update_password);
        check_email = view.findViewById(R.id.check_email);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            namefileInfo.setText(user.getDisplayName());
            emailfileInfo.setText(user.getEmail());
            if (user.isEmailVerified()) {
                checkInfo.setText("認證成功");
            } else {
                checkInfo.setText("未認證");
            }
        }


        //修改名稱按鈕
        update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View item = LayoutInflater.from(fragmentActivity).inflate(R.layout.update_name_layout, null);
                new AlertDialog.Builder(fragmentActivity)
                        .setView(item)
                        .setNeutralButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView name = item.findViewById(R.id.name);

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name.getText().toString())
                                        .build();

                                user.updateProfile(profileChangeRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    CookieBar.build(getActivity())
                                                            .setTitle("名稱修改成功,請重新認證並重開")
                                                            .setLayoutGravity(Gravity.BOTTOM)
                                                            .show();
                                                } else {
                                                    Toast.makeText(fragmentActivity, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .show();
            }
        });

        //修改電子信箱按鈕
        update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View item = LayoutInflater.from(fragmentActivity).inflate(R.layout.update_email_layout, null);
                new AlertDialog.Builder(fragmentActivity)
                        .setView(item)
                        .setNeutralButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final TextView email = item.findViewById(R.id.email);
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), "x");
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                user.updateEmail(email.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    CookieBar.build(getActivity())
                                                                            .setTitle("電子信箱修改成功,請重新認證並重開")
                                                                            .setLayoutGravity(Gravity.BOTTOM)
                                                                            .show();
                                                                } else {
                                                                    CookieBar.build(getActivity())
                                                                            .setTitle("電子郵件格式錯誤")
                                                                            .setLayoutGravity(Gravity.BOTTOM)
                                                                            .show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }).show();
            }
        });

        //修改密碼按鈕
        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View item = LayoutInflater.from(fragmentActivity).inflate(R.layout.update_password_layout, null);
                new AlertDialog.Builder(fragmentActivity)
                        .setView(item)
                        .setNeutralButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText password = item.findViewById(R.id.password);
                                user.updatePassword(password.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    CookieBar.build(getActivity())
                                                            .setTitle("密碼修改完成,請重新認證並重開")
                                                            .setLayoutGravity(Gravity.BOTTOM)
                                                            .show();
                                                } else {
                                                    CookieBar.build(getActivity())
                                                            .setTitle("密碼至少6位數")
                                                            .setLayoutGravity(Gravity.BOTTOM)
                                                            .show();
                                                }
                                            }
                                        });
                            }
                        }).show();
            }
        });

        //驗證電子信箱
        check_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.isEmailVerified() == false) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        CookieBar.build(getActivity())
                                                .setTitle("驗證信已發送至您的信箱")
                                                .setLayoutGravity(Gravity.BOTTOM)
                                                .show();
                                    } else {
                                        Toast.makeText(fragmentActivity, "Email 驗證" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(fragmentActivity,"已認證過",Toast.LENGTH_SHORT).show();
                }
            }

        });

        //登出按鈕
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookieBar.build(getActivity())
                        .setActionColor(R.color.icon_press)
                        .setTitle("確定要登出")
                        .setLayoutGravity(Gravity.BOTTOM)
                        .setAction("確定", new OnActionClickListener() {
                            @Override
                            public void onClick() {
                                getActivity().finish();
                            }
                        }).show();

            }
        });
        return view;
    }


    public static MemberDataFragment getInstance() {
        if(instance == null){
            instance = new MemberDataFragment();
        }
        return instance;
    }
}
