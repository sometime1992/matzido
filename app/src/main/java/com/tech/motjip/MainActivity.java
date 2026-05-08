package com.tech.motjip;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.tech.motjip.Controller.MainController;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Utils.WebBrowserUtil;

import java.security.MessageDigest;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity
        implements MainController.MainControllerCallback {

    private static final String TAG = "MainActivityDebug";

    private MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getHashKey();

        Log.d(TAG, "========================================");
        Log.d(TAG, "onCreate() - MainActivity started");

        mainController = new MainController(this, this);

        Intent intent = getIntent();

        boolean isDeepLinkHandled =
                mainController.handleDeepLinkIfNeeded(intent);

        if (isDeepLinkHandled) {
            return;
        }

        mainController.checkAutoLogin();
    }

    private void getHashKey() {

        try {

            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNING_CERTIFICATES
            );

            for (android.content.pm.Signature signature :
                    info.signingInfo.getApkContentsSigners()) {

                MessageDigest md =
                        MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(
                        md.digest(),
                        Base64.NO_WRAP
                );

                Log.d(TAG, "카카오 HASH_KEY = " + hashKey);
            }

        } catch (Exception e) {
            Log.e("HASH_KEY", "오류", e);
        }
    }

    private void initializeLoginScreen() {

        setContentView(R.layout.activity_main);

        LinearLayoutCompat btnKakaoLogin =
                findViewById(R.id.btnKakaoLogin);

        LinearLayoutCompat btnGoogleLogin =
                findViewById(R.id.btnGoogleLogin);

        btnKakaoLogin.setOnClickListener(
                v -> mainController.loginWithKakaoSdk()
        );

        btnGoogleLogin.setOnClickListener(v -> {

            String url =
                    "https://accounts.google.com/o/oauth2/v2/auth?" +
                            "client_id=733059527774-sb6lg9a1nfiuicv713h62gr9kvjmfpul.apps.googleusercontent.com" +
                            "&redirect_uri=https://spout-distant-cost.ngrok-free.dev/login/oauth2/code/google" +
                            "&response_type=code" +
                            "&scope=openid%20email%20profile";

            WebBrowserUtil.openWebBrowser(this, url);
        });
    }

    private void moveNextByUser(LoginResponseDto user) {

        if (user.getNickname() == null ||
                user.getNickname().isEmpty()) {

            Log.d(TAG, "신규 회원 → 닉네임 설정 이동");

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            NicknameActivity.class
                    );

            intent.putExtra(
                    "member_id",
                    user.getMemberId()
            );

            startActivity(intent);

        } else {

            Log.d(TAG, "기존 회원 → 홈 이동");

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            HomeActivity.class
                    );

            intent.putExtra(
                    "LOGIN_USER_INFO",
                    user
            );

            startActivity(intent);
        }
    }

    @Override
    public void onNeedLoginScreen() {
        initializeLoginScreen();
    }

    @Override
    public void onLoginSuccess(LoginResponseDto user) {
        moveNextByUser(user);
    }

    @Override
    public void onNeedFinish() {
        finish();
    }
}