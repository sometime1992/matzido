package com.tech.motjip.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.tech.motjip.Controller.MotJipApiService;
import com.tech.motjip.Handler.BaseActivity;

import com.tech.motjip.Handler.PreferenceManager;
import com.tech.motjip.Model.TokenResponse;
import com.tech.motjip.Model.MemberJoinRequest;
import com.tech.motjip.R;


import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {

    private final String TAG = "LOGIN_DEBUG";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🚀 1. 자동 로그인 체크 (금고 확인)
        if (PreferenceManager.getAccessToken(this) != null) {
            String savedNickname = PreferenceManager.getNickname(this);
            if (savedNickname != null && !savedNickname.isEmpty()) {
                Log.d(TAG, "자동 로그인: 홈 화면으로 이동");
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                Log.d(TAG, "자동 로그인: 프로필 설정으로 이동");
                startActivity(new Intent(this, ProfileActivity.class));
            }
            finish();
            return;
        }

        // 2. 로그인 정보가 없으면 레이아웃 표시 (중복 제거됨)
        setContentView(R.layout.activity_main);

        // 구글 로그인 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("578669991449-hd5p76amsc8mcfmp00lbbpnahlj9edcg.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    }
                }
        );

        ImageView btnKakao = findViewById(R.id.btn_kakao);
        if (btnKakao != null) btnKakao.setOnClickListener(v -> showLoginDialog("카카오"));

        ImageView btnGoogle = findViewById(R.id.btn_google);
        if (btnGoogle != null) btnGoogle.setOnClickListener(v -> showLoginDialog("구글"));
    }

    // [중략: Dialog 및 소셜 로그인 로직은 동일]
    private void showLoginDialog(String platform) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login_confirm);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);
        if (tvMessage != null)
            tvMessage.setText(platform.equals("구글") ? "'맛남의 광장'이 구글을 사용하여\n로그인하려고 합니다." : "'맛남의 광장'이 카카오톡을 사용하여\n로그인하려고 합니다.");
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        if (btnCancel != null) btnCancel.setOnClickListener(v -> dialog.dismiss());
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                if (platform.equals("카카오")) startKakaoLogin();
                else startGoogleLogin();
            });
        }
        dialog.show();
    }

    private void startGoogleLogin() {
        googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent());
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) sendDataToServer(account.getEmail(), "", "", 2L);
        } catch (ApiException e) {
            Log.w(TAG, "구글 로그인 실패 코드=" + e.getStatusCode());
            Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void startKakaoLogin() {
        Function2<OAuthToken, Throwable, Unit> callback = (token, error) -> {
            if (error == null && token != null) fetchKakaoUserInfo();
            return null;
        };
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this))
            UserApiClient.getInstance().loginWithKakaoTalk(this, callback);
        else UserApiClient.getInstance().loginWithKakaoAccount(this, callback);
    }

    private void fetchKakaoUserInfo() {
        UserApiClient.getInstance().me((user, error) -> {
            if (user != null && user.getKakaoAccount() != null && user.getKakaoAccount().getEmail() != null) {
                sendDataToServer(user.getKakaoAccount().getEmail(), "", "", 1L);
            } else {
                Toast.makeText(this, "이메일 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }

    /**
     * 🚀 서버 통신 핵심 로직
     */
    private void sendDataToServer(String email, String nickname, String profileImgUrl, long providerId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MotJipApiService apiService = retrofit.create(MotJipApiService.class);
        MemberJoinRequest request = new MemberJoinRequest(email, nickname, profileImgUrl, 1, providerId);

        apiService.joinMember(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = response.body().getAccessToken();
                    String refreshToken = response.body().getRefreshToken();
                    String serverNickname = response.body().getNickname(); // 🚀 서버에서 온 값

                    // 로그를 상단으로 이동 (분석하기 편하게)
                    Log.d("LOGIN_CHECK", "서버가 보내준 닉네임: " + serverNickname);
                    Log.d("TOKEN_DEBUG", "========================================");
                    Log.d("TOKEN_DEBUG", "액세스 토큰: " + accessToken);
                    Log.d("TOKEN_DEBUG", "리프레시 토큰: " + refreshToken);
                    Log.d("TOKEN_DEBUG", "========================================");

                    // 1. 금고에 토큰과 이메일 저장
                    PreferenceManager.saveTokens(MainActivity.this, accessToken, refreshToken, email);

                    // 2. 닉네임 유무에 따라 길 가르기
                    if (serverNickname != null && !serverNickname.isEmpty()) {
                        // 기존 유저: 닉네임 저장하고 바로 홈으로
                        PreferenceManager.saveNickname(MainActivity.this, serverNickname);
                        Log.d(TAG, "기존 유저: 홈으로 이동");
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    } else {
                        // 신규 유저: 프로필 설정으로
                        Log.d(TAG, "신규 유저: 프로필 설정 이동");
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.putExtra("user_email", email);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Log.e(TAG, "응답 에러: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "연결 실패", t);
            }
        });
    }

}


