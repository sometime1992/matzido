package com.tech.motjip;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Dto.RequestDto.UpdateMyNicknameRequestDto;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNicknameActivity extends AppCompatActivity {

    private EditText etNickname;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_nickname);

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

            if (inputNickname.length() < 2 ||
                    inputNickname.length() > 10) {

                Toast.makeText(
                        this,
                        "닉네임은 2~10자 이내로 입력해 주세요.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            UpdateMyNicknameRequestDto requestDto =
                    new UpdateMyNicknameRequestDto(
                            inputNickname
                    );

            RetrofitClient.getApiService(this)
                    .updateMyNickname(requestDto)
                    .enqueue(new Callback<LoginResponseDto>() {

                        @Override
                        public void onResponse(
                                Call<LoginResponseDto> call,
                                Response<LoginResponseDto> response
                        ) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                Toast.makeText(
                                        EditNicknameActivity.this,
                                        "닉네임 수정 완료",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();

                            } else {

                                Toast.makeText(
                                        EditNicknameActivity.this,
                                        "닉네임 수정 실패",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<LoginResponseDto> call,
                                Throwable t
                        ) {

                            Toast.makeText(
                                    EditNicknameActivity.this,
                                    "서버 연결 실패",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });
    }
}