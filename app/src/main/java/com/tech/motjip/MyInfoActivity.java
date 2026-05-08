package com.tech.motjip;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MyInfoActivity extends AppCompatActivity {

    private Button btnEditNickname;
    private Button btnEditProfileImage;
    private Button btnDeleteAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_info);

        btnEditNickname = findViewById(R.id.btnEditNickname);
        btnEditProfileImage = findViewById(R.id.btnEditProfileImage);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnEditNickname.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            MyInfoActivity.this,
                            EditNicknameActivity.class
                    );

            startActivity(intent);
        });

        btnEditProfileImage.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "프로필 이미지 수정 기능",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "회원탈퇴 기능",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}