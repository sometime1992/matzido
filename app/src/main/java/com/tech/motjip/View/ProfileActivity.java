package com.tech.motjip.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// 🚀 새 주소(Model, Controller)로 깔끔하게 연결!
import com.tech.motjip.Controller.MotJipApiService;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Handler.PreferenceManager;
import com.tech.motjip.Model.MemberJoinRequest;
import com.tech.motjip.Model.TokenResponse;
import com.tech.motjip.R;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class ProfileActivity extends BaseActivity {

    private final String TAG = "PROFILE_SETUP";
    private String userEmail;
    private EditText etNickname;
    private TextView tvNicknameCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // 1. 뷰 초기화
        etNickname = findViewById(R.id.etnickname);
        tvNicknameCount = findViewById(R.id.tvNicknameCount);
        LinearLayout btnStart = findViewById(R.id.btnStart);

        // 2. 이메일 정보 가져오기 (결합 포인트)
        userEmail = getIntent().getStringExtra("user_email");
        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = PreferenceManager.getUserEmail(this);
        }

        // 유저 정보가 없으면 다시 로그인 유도
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "❌ 유저 정보를 불러올 수 없습니다.");
            Toast.makeText(this, "로그인 정보가 유효하지 않습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();

            PreferenceManager.clearTokens(this);
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
            return;
        }

        // 3. 시작하기 버튼 클릭 이벤트
        if (btnStart != null) {
            btnStart.setOnClickListener(v -> {
                String nickname = etNickname.getText().toString().trim();

                if (nickname.length() < 2) {
                    Toast.makeText(this, "닉네임을 2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateNicknameToServer(nickname);
            });
        }
    }

    /**
     * 🚀 서버에 닉네임 저장을 요청하는 메서드 (새로 만든 API 서비스 적용)
     */
    private void updateNicknameToServer(String nickname) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 에뮬레이터 로컬 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 🚀 수정: 우리가 새로 합쳐둔 통합 컨트롤러(MotJipApiService) 사용!
        MotJipApiService apiService = retrofit.create(MotJipApiService.class);

        // 🚀 수정: 더 이상 MainActivity.MemberJoinRequest가 아님! Model에 있는 독립된 클래스 사용
        MemberJoinRequest request = new MemberJoinRequest(userEmail, nickname, null, 1, 1L);

        // 🚀 수정: ProfileApiService가 아니라 통합된 apiService 사용
        // (참고: 기존 코드는 Call<Void>를 기대했지만, 통일성을 위해 일단 그대로 둡니다. 나중에 백엔드와 상의해서 수정할 수 있습니다.)
        apiService.updateProfile(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    PreferenceManager.saveNickname(ProfileActivity.this, nickname);
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                } else if (response.code() == 404) {
                    Toast.makeText(ProfileActivity.this, "존재하지 않는 유저입니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
                    PreferenceManager.clearTokens(ProfileActivity.this);
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "에러 코드: " + response.code());
                    Toast.makeText(ProfileActivity.this, "저장 실패 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e(TAG, "네트워크 에러", t);
                Toast.makeText(ProfileActivity.this, "통신 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}