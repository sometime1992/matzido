package com.tech.motjip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.Controller.MessageAdapter;
import com.tech.motjip.API.ApiService;
import com.tech.motjip.Handler.PreferenceManager;
import com.tech.motjip.Model.Message;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText etMessage;
    private Button btnSend;

    private String currentUserId;
    private String currentNickname;
    private long roomId = -1;

    private StompClient stompClient;
    private final String TAG = "ChatTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ImageView btnBack = findViewById(R.id.btn_custom_back);
        TextView tvTitle = findViewById(R.id.tv_room_title);
        recyclerView = findViewById(R.id.rv_message_list);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        currentUserId = PreferenceManager.getUserEmail(this);
        currentNickname = PreferenceManager.getNickname(this);
        if (currentNickname == null) currentNickname = "익명";

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String roomName = getIntent().getStringExtra("roomName");
        if (roomName != null) tvTitle.setText(roomName);
        roomId = getIntent().getLongExtra("roomId", -1);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.postDelayed(() -> {
                    if (messageList.size() > 0) {
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }, 100);
            }
        });

        etMessage.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSend.performClick();
                return true;
            }
            return false;
        });

        btnSend.setOnClickListener(v -> {
            String content = etMessage.getText().toString().trim();
            if (!content.isEmpty() && roomId != -1) {
                sendMessage(content);
            }
        });

        if (roomId != -1) {
            loadChatHistory();
            connectWebSocket();
        }
    }

    private void loadChatHistory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getChatMessages(roomId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> historyList = response.body();

                    for (Message msg : historyList) {
                        if (msg.getSenderId() != null && msg.getSenderId().equals(currentUserId)) {
                            msg.setViewType(0);
                        } else {
                            msg.setViewType(1);
                        }
                    }

                    messageList.clear();
                    messageList.addAll(historyList);
                    adapter.notifyDataSetChanged();

                    if (messageList.size() > 0) {
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {}
        });
    }

    @SuppressLint("CheckResult")
    private void connectWebSocket() {
        String url = "ws://10.0.2.2:8080/ws/chat/websocket";
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        // 🚀 1. 상태 감지만 담당 (구독은 여기서 안 함)
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED: Log.d(TAG, "🎉 서버 연결 성공!"); break;
                case ERROR: Log.e(TAG, "🚨 에러 발생", lifecycleEvent.getException()); break;
                case CLOSED: Log.d(TAG, "🔌 연결 끊어짐."); break;
            }
        });

        // 🚀 2. 메시지 수신 구독 (OPENED 밖으로 빼서 딱 1번만 구독하게 강제함)
        stompClient.topic("/sub/chat/room/" + roomId).subscribe(topicMessage -> {
            String receivedData = topicMessage.getPayload();
            try {
                JSONObject jsonObject = new JSONObject(receivedData);
                String sender = jsonObject.getString("senderId");
                String text = jsonObject.getString("messageContent");
                String nickname = jsonObject.optString("senderNickname", "익명");
                String sentAt = jsonObject.optString("sentAt", "");

                int viewType = sender.equals(currentUserId) ? 0 : 1;

                Message receivedMsg = new Message(text, sender, nickname, viewType);
                receivedMsg.setSentAt(sentAt);

                runOnUiThread(() -> {
                    messageList.add(receivedMsg);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
            } catch (Exception e) {}
        });

        stompClient.connect();
    }

    private void sendMessage(String content) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("roomId", roomId);
            payload.put("senderId", currentUserId);
            payload.put("senderNickname", currentNickname);
            payload.put("messageContent", content);

            stompClient.send("/pub/chat/message", payload.toString()).subscribe();
            etMessage.setText("");
        } catch (Exception e) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stompClient != null && stompClient.isConnected()) {
            stompClient.disconnect();
        }
    }
}