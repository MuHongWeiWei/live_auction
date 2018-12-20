package com.example.fly.anyrtcdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.ImageHeadUtils;
import com.example.fly.anyrtcdemo.activity.AnyLiveStartActivity;
import com.example.fly.anyrtcdemo.firebaseClass.Type;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends Fragment {

    private static LiveFragment instance;
    private String header = null, nick;
    private List<Type> types;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        header = ImageHeadUtils.getVavatar();//取得使用者頭像

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        nick = currentUser.getDisplayName();//取得使用者暱稱

        RecyclerView recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        types = new ArrayList<>();

        String[] typeArray = getResources().getStringArray(R.array.type_array);
        types.add(new Type(typeArray[0], R.drawable.type_food));
        types.add(new Type(typeArray[1], R.drawable.type_life));
        types.add(new Type(typeArray[2], R.drawable.type_office));
        types.add(new Type(typeArray[3], R.drawable.type_clean));
        types.add(new Type(typeArray[4], R.drawable.type_home));
        types.add(new Type(typeArray[5], R.drawable.type_other));

        TypeAdapter adapter = new TypeAdapter();
        recycler.setAdapter(adapter);

        return view;
    }


    class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeHolder> {

        @NonNull
        @Override
        public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.livetype_icon, parent, false);
            return new TypeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TypeHolder holder, final int position) {
            final Type type = types.get(position);
            holder.nameText.setText(type.getName());
            holder.iconImage.setImageResource(type.getImage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toNewPage(type.getImage());
                }
            });
        }

        @Override
        public int getItemCount() {
            return types.size();
        }

        class TypeHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            ImageView iconImage;

            public TypeHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.type_name);
                iconImage = itemView.findViewById(R.id.type_icon);
            }
        }
    }

    private void toNewPage(int image) {
        intent = new Intent();
        switch (image) {
            case R.drawable.type_food:
                intent.putExtra("type", "生鮮食品");
                checkMember();
                break;
            case R.drawable.type_life:
                intent.putExtra("type", "生活用品");
                checkMember();
                break;
            case R.drawable.type_office:
                intent.putExtra("type", "辦公用品");
                checkMember();
                break;
            case R.drawable.type_clean:
                intent.putExtra("type", "清潔用品");
                checkMember();
                break;
            case R.drawable.type_home:
                intent.putExtra("type", "居家收納");
                checkMember();
                break;
            case R.drawable.type_other:
                intent.putExtra("type", "其他");
                checkMember();
                break;
        }

    }

    //判斷是否驗證過
    public void checkMember(){
        intent.putExtra("nickname", nick);
        intent.putExtra("headUrl", header);
        if (auth.getCurrentUser().isEmailVerified()) {
            intent.setClass(getActivity(), AnyLiveStartActivity.class);
            startActivity(intent);
        } else {
            CookieBar.build(getActivity()).setTitle("提示").setMessage("此帳號未驗證,是否需要驗證?").setLayoutGravity(Gravity.BOTTOM).setActionColor(R.color.orange).setDuration(10000).setIcon(R.drawable.login).setAction("確定", new OnActionClickListener() {
                @Override
                public void onClick() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, MemberDataFragment.getInstance()).commit();
                }
            }).show();
        }
    }

    public static LiveFragment getInstance() {
        if (instance == null) {
            instance = new LiveFragment();
        }
        return instance;
    }

}

