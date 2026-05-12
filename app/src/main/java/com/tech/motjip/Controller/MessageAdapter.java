package com.tech.motjip.Controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.Model.Message;
import com.tech.motjip.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;
    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // 내 ID와 보낸 사람 ID가 같으면 오른쪽(SENT) 말풍선, 다르면 왼쪽(RECEIVED) 말풍선
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        String formattedTime = formatTimeString(message.getSentAt());

        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).tvContent.setText(message.getContent());
            if (((SentViewHolder) holder).tvTime != null) {
                ((SentViewHolder) holder).tvTime.setText(formattedTime);
            }
        } else {
            ((ReceivedViewHolder) holder).tvContent.setText(message.getContent());

            String nickname = message.getSenderNickname();
            if (nickname == null || nickname.trim().isEmpty() || nickname.equalsIgnoreCase("null")) {
                nickname = message.getSenderId();
            }
            ((ReceivedViewHolder) holder).tvName.setText(nickname);

            if (((ReceivedViewHolder) holder).tvTime != null) {
                ((ReceivedViewHolder) holder).tvTime.setText(formattedTime);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // 🚀 서버의 KST 시간을 예쁘게 포맷
    private String formatTimeString(String timeString) {
        if (timeString == null || timeString.isEmpty()) return "";
        try {
            if (timeString.contains(".")) {
                timeString = timeString.substring(0, timeString.indexOf("."));
            }
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
            Date date = serverFormat.parse(timeString);
            SimpleDateFormat localFormat = new SimpleDateFormat("a h:mm", Locale.KOREA);
            return localFormat.format(date);
        } catch (Exception e) {
            return timeString;
        }
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        SentViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_message_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvContent, tvTime;
        ReceivedViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_sender_name);
            tvContent = itemView.findViewById(R.id.tv_message_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}