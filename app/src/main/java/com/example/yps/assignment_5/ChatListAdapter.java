package com.example.yps.assignment_5;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by yPs on 4/23/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder> {

    private ArrayList<Chat> chatList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    String TAG = "mTag";

    class ChatListHolder extends RecyclerView.ViewHolder {
        TextView messageRowTV,senderRowTV;

        ChatListHolder(View view) {
            super(view);
            senderRowTV =(TextView) view.findViewById(R.id.senderRowTV);
            messageRowTV = (TextView) view.findViewById(R.id.messageRowTV);
        }
    }

    ChatListAdapter(ArrayList<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public ChatListAdapter.ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_row, parent, false);

        return new ChatListAdapter.ChatListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ChatListHolder holder, int position) {
        Chat chat = chatList.get(position);
     /*   String nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Log.i(TAG,"OnBindViewHolder:: NickName:: "+nickname+"  "+chat.getSender());
        if(TextUtils.equals(nickname,chat.getSender())){
            holder.senderRowTV.setGravity(Gravity.END);
            holder.messageRowTV.setGravity(Gravity.END);
        }*/

       /* holder.senderRowTV.setGravity(Gravity.END);
        holder.messageRowTV.setGravity(Gravity.END);*/

        holder.senderRowTV.setText(chat.getSender()+"->");
        holder.messageRowTV.setText(chat.getMessage());
    }


}
