package com.tech.motjip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.Controller.MessageAdapter;
import com.tech.motjip.API.ApiService;
import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Handler.PreferenceManager;
import com.tech.motjip.Model.Message;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText etMessage;
    private Button btnSend;

    private String currentUserId; // 내부 식별용 (이제 숫자 ID 문자열이 들어옵니다)
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

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, "");
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
            fetchUserInfoAndStartChat();
        }
    }

    private void fetchUserInfoAndStartChat() {
        RetrofitClient.getApiService(this).getCurrentUser().enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDto user = response.body();

                    // 🚀 핵심 수정: 이메일 대신 팀원들이 만든 고유 회원 번호(memberId)를 가져와서 저장합니다!
                    if (user.getMemberId() != null) {
                        currentUserId = String.valueOf(user.getMemberId());
                    }
                    currentNickname = user.getNickname();

                    PreferenceManager.saveNickname(MessageActivity.this, currentNickname);
                }

                // 방어 코드: 숫자가 비어있거나 기존 이메일 형식이 남아있으면 안전하게 "0"으로 초기화
                if (currentUserId == null || currentUserId.trim().isEmpty() || currentUserId.contains("@")) {
                    currentUserId = "0";
                }
                if (currentNickname == null || currentNickname.trim().isEmpty()) {
                    currentNickname = PreferenceManager.getNickname(MessageActivity.this);
                    if (currentNickname == null) currentNickname = "익명";
                }

                // 내 말풍선 정렬용 어댑터 동기화
                adapter = new MessageAdapter(messageList, currentUserId);
                recyclerView.setAdapter(adapter);

                loadChatHistory();
                connectWebSocket();
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                Log.e(TAG, "유저 정보 로드 실패", t);
                currentUserId = "0";
                currentNickname = PreferenceManager.getNickname(MessageActivity.this);
                if (currentNickname == null) currentNickname = "익명";

                loadChatHistory();
                connectWebSocket();
            }
        });
    }

    private void loadChatHistory() {
        ApiService apiService = RetrofitClient.getApiService(this);
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
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                Log.e(TAG, "채팅 내역 로드 실패", t);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void connectWebSocket() {
        String url = "ws://localhost:8080/ws/chat/websocket";

        Map<String, String> connectHeaders = new HashMap<>();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("ACCESS_TOKEN", null);

        if (accessToken != null && !accessToken.trim().isEmpty()) {
            connectHeaders.put("Authorization", "Bearer " + accessToken);
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, connectHeaders);

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED: Log.d(TAG, "🎉 서버 연결 성공!"); break;
                case ERROR: Log.e(TAG, "🚨 에러 발생", lifecycleEvent.getException()); break;
                case CLOSED: Log.d(TAG, "🔌 연결 끊어짐."); break;
            }
        });

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
            if (stompClient != null && stompClient.isConnected()) {
                JSONObject payload = new JSONObject();
                payload.put("roomId", roomId);

                // 🚀 핵심 수정: 문자열 숫자를 진짜 숫자(Long)로 파싱하여 JSON에 실어 보냅니다!
                try {
                    payload.put("senderId", Long.parseLong(currentUserId));
                } catch (NumberFormatException e) {
                    payload.put("senderId", 0L);
                }

                payload.put("senderNickname", currentNickname);
                payload.put("messageContent", content);

                stompClient.send("/pub/chat/message", payload.toString()).subscribe();
                etMessage.setText("");
            } else {
                Toast.makeText(this, "서버와 연결이 끊겨 메시지를 보낼 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
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