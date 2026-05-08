package com.tech.motjip.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Dto.RequestDto.LogoutRequestDto;
import com.tech.motjip.Dto.ResponseDto.LoginResponseDto;
import com.tech.motjip.MainActivity;
import com.tech.motjip.MyInfoActivity;
import com.tech.motjip.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvEmail;
    private TextView tvNickname;

    private Button btnLogout;

    private ImageButton btnSettings;

    private static final String PREF_NAME = "AppPrefs";

    private static final String ACCESS_TOKEN =
            "ACCESS_TOKEN";

    private static final String REFRESH_TOKEN =
            "REFRESH_TOKEN";

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        tvEmail =
                view.findViewById(R.id.tvEmail);

        tvNickname =
                view.findViewById(R.id.tvNickname);

        btnLogout =
                view.findViewById(R.id.btnLogout);

        btnSettings =
                view.findViewById(R.id.btnSettings);

        btnLogout.setOnClickListener(v -> logout());

        btnSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            MyInfoActivity.class
                    );

            startActivity(intent);
        });
    }

    @Override
    public void onResume() {

        super.onResume();

        loadMyInfo();
    }

    private void loadMyInfo() {

        RetrofitClient.getApiService(requireContext())
                .getCurrentUser()
                .enqueue(new Callback<LoginResponseDto>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponseDto> call,
                            Response<LoginResponseDto> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            LoginResponseDto user =
                                    response.body();

                            String email =
                                    user.getEmail();

                            String nickname =
                                    user.getNickname();

                            tvEmail.setText(
                                    email != null
                                            && !email.isEmpty()
                                            ? email
                                            : "조회된 계정"
                            );

                            tvNickname.setText(
                                    nickname != null
                                            && !nickname.isEmpty()
                                            ? nickname
                                            : "미설정"
                            );

                        } else {

                            tvEmail.setText("조회 실패");

                            tvNickname.setText("조회 실패");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponseDto> call,
                            Throwable t
                    ) {

                        tvEmail.setText("서버 오류");

                        tvNickname.setText("서버 오류");
                    }
                });
    }

    private void logout() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        requireActivity().MODE_PRIVATE
                );

        String refreshToken =
                preferences.getString(
                        REFRESH_TOKEN,
                        null
                );

        if (refreshToken == null
                || refreshToken.trim().isEmpty()) {

            clearTokensAndMoveToMain();

            return;
        }

        LogoutRequestDto requestDto =
                new LogoutRequestDto(refreshToken);

        RetrofitClient.getApiService(requireContext())
                .logout(requestDto)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        clearTokensAndMoveToMain();
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        clearTokensAndMoveToMain();
                    }
                });
    }

    private void clearTokensAndMoveToMain() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        requireActivity().MODE_PRIVATE
                );

        preferences.edit()
                .remove(ACCESS_TOKEN)
                .remove(REFRESH_TOKEN)
                .apply();

        Intent intent =
                new Intent(
                        requireContext(),
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        Toast.makeText(
                requireContext(),
                "로그아웃되었습니다.",
                Toast.LENGTH_SHORT
        ).show();

        requireActivity().finish();
    }
}