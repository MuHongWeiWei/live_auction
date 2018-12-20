package com.example.fly.anyrtcdemo.adapter;

import android.annotation.SuppressLint;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.fly.anyrtcdemo.bean.ChatMessageBean;
import com.example.fly.anyrtcdemo.R;
import com.example.fly.anyrtcdemo.Utils.GlideUtils.GlideUtils;

import java.util.List;

public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ChatListHolder> {

    List<ChatMessageBean> chatMessageList;
    Context context;


    public LiveChatAdapter(List<ChatMessageBean> chatMessageList, Context context) {
        this.chatMessageList = chatMessageList;
        this.context = context;
    }


    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_live_chat, parent, false);
        ChatListHolder holder = new ChatListHolder(view);
        return holder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ChatListHolder holder, int position) {
        final ChatMessageBean chatMessageBean = chatMessageList.get(position);
        holder.txtChatName.setText(chatMessageBean.getmCustomName());
        GlideUtils.downLoadCircleImage(context,chatMessageBean.getmCustomHeader(),holder.imgHeader);
        if(chatMessageBean.getmMsgContent().equals("")) {
            holder.txtSpace.setVisibility(View.GONE);
            holder.txtChatMessage.setTextColor(R.color.btn_blue_pressed);
            holder.txtChatMessage.setText(chatMessageBean.getmCustomID() + context.getString(R.string.str_online));
        } else {

            holder.txtChatMessage.setText(chatMessageBean.getmMsgContent());
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();

    }

    public static class ChatListHolder extends RecyclerView.ViewHolder {
        ImageView imgHeader;
        TextView txtChatName;
        TextView txtSpace;
        TextView txtChatMessage;


        public ChatListHolder(View itemView) {
            super(itemView);
            imgHeader = (ImageView) itemView.findViewById(R.id.img_chat_header);
            txtChatName = (TextView) itemView.findViewById(R.id.txt_chat_name);
            txtSpace =  (TextView) itemView.findViewById(R.id.txt_space);
            txtChatMessage = (TextView) itemView.findViewById(R.id.txt_chat_message);

        }
    }

}
