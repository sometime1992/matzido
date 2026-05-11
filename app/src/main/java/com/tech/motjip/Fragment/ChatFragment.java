package com.tech.motjip.Fragment; // 🚀 패키지명 변경됨

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

import com.tech.motjip.API.ApiService; // 🚀 팀 API로 연결
import com.tech.motjip.Model.ChatRoom;
import com.tech.motjip.R;
import com.tech.motjip.MessageActivity; // 🚀 채팅방 화면 경로 업데이트

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> roomList = new ArrayList<>();
    private final String BASE_URL = "http://10.0.2.2:8080/";

    public ChatFragment() {
        // 빈 생성자
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // 1. XML 뷰 연결
        recyclerView = view.findViewById(R.id.rv_chat_list);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ChatRoomAdapter(roomList);
            recyclerView.setAdapter(adapter);
        }

        // 2. 방 만들기 버튼
        Button btnCreateRoom = view.findViewById(R.id.btn_create_room);
        if (btnCreateRoom != null) {
            btnCreateRoom.setOnClickListener(v -> showCreateRoomDialog());
        }

        // 3. 서버에서 방 목록 불러오기
        loadChatRooms();

        return view;
    }

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

    private void createNewRoom(String roomName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 🚀 MotJipApiService 대신 팀 프로젝트의 ApiService 사용
        ApiService apiService = retrofit.create(ApiService.class);

        ChatRoom newRoom = new ChatRoom();
        newRoom.setRoomName(roomName);

        apiService.createChatRoom(newRoom).enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "방이 생성되었습니다!", Toast.LENGTH_SHORT).show();
                    loadChatRooms();
                } else {
                    Log.e("CHAT_ERROR", "서버 응답 에러 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatRoom> call, Throwable t) {
                Toast.makeText(getContext(), "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatRooms() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 🚀 ApiService 사용
        ApiService apiService = retrofit.create(ApiService.class);
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

    // --- 리사이클러뷰 어댑터 ---
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
                // 🚀 MessageActivity 실행
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