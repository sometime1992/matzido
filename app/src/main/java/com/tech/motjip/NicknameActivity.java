package com.tech.motjip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tech.motjip.Controller.NicknameController;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;

public class NicknameActivity extends AppCompatActivity
        implements NicknameController.NicknameControllerCallback {

    private static final String TAG = "NicknameActivityDebug";

    private Long memberId;
    private String nickname;

    private EditText etNickname;
    private Button btnSubmit;

    private NicknameController nicknameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nickname);

        nicknameController = new NicknameController(this, this);

        Log.d(TAG, "========================================");
        Log.d(TAG, "onCreate() - NicknameActivity started");

        try {

            memberId = getIntent().getLongExtra("member_id", -1L);
            nickname = getIntent().getStringExtra("nickname");

            Log.d(TAG, "[INTENT]");
            Log.d(TAG, "memberId: " + memberId);
            Log.d(TAG, "nickname: " + nickname);

            if (nickname != null && !nickname.trim().isEmpty()) {

                Log.d(TAG, "이미 닉네임 있음 → Home 이동");

                Intent homeIntent =
                        new Intent(
                                this,
                                HomeActivity.class
                        );

                startActivity(homeIntent);

                finish();

                return;
            }

        } catch (Exception e) {

            Log.e(TAG, "Intent 처리 오류", e);
        }

        etNickname = findViewById(R.id.etNickname);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {

            String inputNickname =
                    etNickname.getText()
                            .toString()
                            .trim();

            if (inputNickname.isEmpty()) {

                Toast.makeText(
                        this,
                        "닉네임을 입력해 주세요.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (inputNickname.length() < 2 || inputNickname.length() > 10) {

                Toast.makeText(
                        this,
                        "닉네임은 2~10자 이내로 입력해 주세요.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            nicknameController.updateNickname(
                    memberId,
                    inputNickname
            );
        });
    }

    @Override
    public void onNicknameSuccess(LoginResponseDto user) {

        Toast.makeText(
                NicknameActivity.this,
                "닉네임이 설정되었습니다.",
                Toast.LENGTH_SHORT
        ).show();

        Intent homeIntent =
                new Intent(
                        NicknameActivity.this,
                        HomeActivity.class
                );

        homeIntent.putExtra("LOGIN_USER_INFO", user);

        startActivity(homeIntent);

        finish();
    }

    @Override
    public void onNicknameFail(String message) {

        Toast.makeText(
                NicknameActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}