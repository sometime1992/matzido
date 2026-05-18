package com.tech.motjip;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Controller.MainController;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Utils.DialogUtil;
import com.tech.motjip.Utils.LoginStateManager;
import com.tech.motjip.Utils.WebBrowserUtil;

import java.security.MessageDigest;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity
        implements MainController.MainControllerCallback {

    private static final String TAG = "MainActivityDebug";

    private MainController mainController;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        getHashKey();

        Log.d(
                TAG,
                "onCreate() - MainActivity started"
        );

        mainController =
                new MainController(
                        this,
                        this
                );

        Intent intent =
                getIntent();

        String authExpiredMessage =
                intent.getStringExtra(
                        RetrofitClient.AUTH_EXPIRED_MESSAGE
                );

        // 토큰 만료 → 자동 로그아웃 처리
        if (authExpiredMessage != null
                && !authExpiredMessage.isEmpty()) {

            LoginStateManager.setLoginStatus(
                    this,
                    LoginStateManager.LOGOUT
            );

            initializeLoginScreen();

            DialogUtil.showCustomDialog(
                    this,
                    R.drawable.fail,
                    "자동 로그아웃",
                    authExpiredMessage,
                    null
            );

            return;
        }

        boolean isDeepLinkHandled =
                mainController.handleDeepLinkIfNeeded(
                        intent
                );

        if (isDeepLinkHandled) {

            return;
        }

        mainController.checkAutoLogin();
    }

    private void getHashKey() {

        try {

            PackageInfo info =
                    getPackageManager()
                            .getPackageInfo(
                                    getPackageName(),
                                    PackageManager.GET_SIGNING_CERTIFICATES
                            );

            for (android.content.pm.Signature signature
                    : info.signingInfo.getApkContentsSigners()) {

                MessageDigest md =
                        MessageDigest.getInstance("SHA");

                md.update(
                        signature.toByteArray()
                );

                String hashKey =
                        Base64.encodeToString(
                                md.digest(),
                                Base64.NO_WRAP
                        );

                Log.d(
                        TAG,
                        "카카오 HASH_KEY = " + hashKey
                );
            }

        } catch (Exception e) {

            Log.e(
                    "HASH_KEY",
                    "오류",
                    e
            );
        }
    }

    private void initializeLoginScreen() {

        setContentView(
                R.layout.activity_main
        );

        LinearLayoutCompat btnKakaoLogin =
                findViewById(
                        R.id.btnKakaoLogin
                );

        LinearLayoutCompat btnGoogleLogin =
                findViewById(
                        R.id.btnGoogleLogin
                );

        btnKakaoLogin.setOnClickListener(
                v -> mainController.loginWithKakaoSdk()
        );

        btnGoogleLogin.setOnClickListener(v -> {
            String url = "https://accounts.google.com/o/oauth2/v2/auth"
                    + "?client_id=578669991449-hd5p76amsc8mcfmp00lbbpnahlj9edcg.apps.googleusercontent.com" // <-- 여기 수정!
                    + "&redirect_uri=http://localhost:8080/login/oauth2/code/google"
                    + "&response_type=code"
                    + "&scope=openid%20email%20profile";

            Log.d("MainActivityDebug", "Request URL: " + url);
            WebBrowserUtil.openWebBrowser(this, url);
        });
    }

    private void moveNextByUser(
            LoginResponseDto user
    ) {

        // 로그인 성공 상태 저장
        LoginStateManager.setLoginStatus(
                this,
                LoginStateManager.LOGIN
        );

        if (user.getNickname() == null
                || user.getNickname().isEmpty()) {

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

        LoginStateManager.setLoginStatus(
                this,
                LoginStateManager.LOGOUT
        );

        initializeLoginScreen();
    }

    @Override
    public void onLoginSuccess(
            LoginResponseDto user
    ) {

        moveNextByUser(
                user
        );
    }

    @Override
    public void onLoginFail(
            String message
    ) {

        LoginStateManager.setLoginStatus(
                this,
                LoginStateManager.LOGOUT
        );

        DialogUtil.showCustomDialog(
                this,
                R.drawable.fail,
                "로그인 실패",
                message,
                null
        );
    }

    @Override
    public void onNeedFinish() {

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // 들어온 신호를 이 액티비티의 인텐트로 설정

        // 기철님이 만드신 컨트롤러의 로직을 여기서 그대로 호출만 해주면 됩니다.
        if (mainController != null) {
            mainController.handleDeepLinkIfNeeded(intent);
        }
    }


}