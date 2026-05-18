package com.tech.motjip.Fragment; // 🚀 마스터 경로(Fragment 패키지)에 맞춤

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.API.ApiService; // 🚀 팀 통합 ApiService 사용
import com.tech.motjip.API.RetrofitClient; // 🚀 공용 레트로핏 클라이언트 추가
import com.tech.motjip.MessageActivity;
import com.tech.motjip.Model.ChatRoom;
import com.tech.motjip.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> roomList = new ArrayList<>();

    public ChatFragment() {
        // 마스터에 있던 기본 생성자 유지
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // 1. XML 아이디에 맞춰 뷰 연결
        recyclerView = view.findViewById(R.id.rv_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatRoomAdapter(roomList);
        recyclerView.setAdapter(adapter);

        // 2. 방 만들기 버튼 리스너 추가
        Button btnCreateRoom = view.findViewById(R.id.btn_create_room);
        if (btnCreateRoom != null) {
            btnCreateRoom.setOnClickListener(v -> showCreateRoomDialog());
        }

        // 3. 서버에서 방 목록 불러오기
        loadChatRooms();

        return view;
    }

    // 🚀 새로운 방 만들기 팝업창 띄우기
    private void showCreateRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("밥 파티 만들기");

        final EditText input = new EditText(getContext());
        input.setHint("방 제목을 입력하세요 (예: 강남역 국밥)");
        builder.setView(input);

        builder.setPositiveButton("생성", (dialog, which) -> {
            String roomName = input.getText().toString().trim();
            if (!roomName.isEmpty()) {
                createNewRoom(roomName);
            } else {
                Toast.makeText(getContext(), "제목을 입력해주세요!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // 🚀 서버에 새로운 방 저장 요청
    private void createNewRoom(String roomName) {
        // 🚀 개별 빌더 설정을 지우고, 토큰 신분증이 자동으로 탑재된 공용 apiService를 가져옵니다.
        ApiService apiService = RetrofitClient.getApiService(getContext());

        ChatRoom newRoom = new ChatRoom();
        newRoom.setRoomName(roomName);

        apiService.createChatRoom(newRoom).enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "방이 생성되었습니다!", Toast.LENGTH_SHORT).show();
                    loadChatRooms(); // 목록 새로고침
                } else {
                    Log.e("CHAT_ERROR", "서버 응답 에러 코드: " + response.code());
                    try {
                        Log.e("CHAT_ERROR", "에러 내용: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<ChatRoom> call, Throwable t) {
                Log.e("ChatFragment", "방 생성 실패", t);
                Toast.makeText(getContext(), "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatRooms() {
        // 🚀 목록 로딩 시에도 동일하게 공용 apiService를 사용해 토큰 누락을 방지합니다.
        ApiService apiService = RetrofitClient.getApiService(getContext());

        apiService.getChatRoomList().enqueue(new Callback<List<ChatRoom>>() {
            @Override
            public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roomList.clear();
                    roomList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                Log.e("ChatFragment", "방 목록 로드 실패", t);
            }
        });
    }

    // --- 리사이클러뷰 어댑터 (내부 클래스) ---
    class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
        private List<ChatRoom> rooms;

        public ChatRoomAdapter(List<ChatRoom> rooms) { this.rooms = rooms; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatRoom room = rooms.get(position);
            String displayName = (room.getRoomName() != null) ? room.getRoomName() : room.getRoomId() + "번 방";
            holder.tvName.setText(displayName);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("roomId", room.getRoomId());
                intent.putExtra("roomName", displayName);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return rooms.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}