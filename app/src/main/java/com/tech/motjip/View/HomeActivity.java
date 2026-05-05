package com.tech.motjip.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.tech.motjip.Controller.MotJipApiService;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kakao.sdk.user.UserApiClient;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Handler.PreferenceManager;
import com.tech.motjip.R;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

@AndroidEntryPoint
public class HomeActivity extends BaseActivity {

    private final String TAG = "HOME_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🚀 로그아웃 버튼 연결
        Button btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performLogout());
        }
    }

    /**
     * 🚀 서버 및 클라이언트 통합 로그아웃 로직
     */
    private void performLogout() {
        // 1. 금고에서 이메일 가져오기 (서버에 누구인지 알려줘야 함)
        String email = PreferenceManager.getUserEmail(this);

        if (email == null || email.isEmpty()) {
            // 이메일이 없으면 서버 통신이 불가능하므로 바로 로컬 정리 진행
            finishLogout();
            return;
        }

        // 2. 서버에 로그아웃 요청 (DB의 리프레시 토큰 파쇄 요청)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 에뮬레이터 로컬 주소
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MotJipApiService apiService = retrofit.create(MotJipApiService.class);

        apiService.logout(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ 서버측 토큰 파쇄 완료");
                } else {
                    Log.e(TAG, "❌ 서버 로그아웃 실패 코드: " + response.code());
                }
                // 서버 성공 여부와 관계없이 사용자는 로그아웃 처리 (앱 사용은 가능해야 하므로)
                finishLogout();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "⚠️ 네트워크 에러로 서버 통신 실패", t);
                // 네트워크가 끊겨도 일단 로컬 데이터는 지워서 로그아웃시킴
                finishLogout();
            }
        });
    }

    /**
     * 🚀 로컬 데이터 정리 및 로그인 화면 이동
     */
    private void finishLogout() {
        // 1. 우리 앱 내부 금고(액세스 토큰, 리프레시 토큰, 이메일, 닉네임) 모두 삭제
        PreferenceManager.clearTokens(this);
        Log.d(TAG, "로컬 금고 비우기 완료");

        // 2. 구글 세션 로그아웃
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> Log.d(TAG, "구글 세션 종료"));

        // 3. 카카오 세션 로그아웃
        UserApiClient.getInstance().logout(error -> {
            if (error != null) Log.e(TAG, "카카오 로그아웃 실패", error);
            else Log.d(TAG, "카카오 세션 종료");
            return null;
        });

        // 4. 로그인 화면(MainActivity)으로 이동 및 스택 클리어
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}