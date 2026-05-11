package com.tech.motjip.Controller;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.Model.ChatRoom;
import com.tech.motjip.R;
import com.tech.motjip.MessageActivity;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatRoom> chatRooms;

    public ChatListAdapter(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatRoom room = chatRooms.get(position);
        holder.tvName.setText(room.getRoomName());
        holder.tvMessage.setText(room.getLastMessage());
        holder.tvTime.setText(room.getTime());

        // 🚀 아이템 클릭 리스너 추가
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MessageActivity.class);

            // 어떤 방에 들어가는지 이름을 넘겨줍니다.
            intent.putExtra("roomName", room.getRoomName());

            v.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() { return chatRooms.size(); }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMessage, tvTime;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_room_name);
            tvMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_chat_time);
        }
    }
}