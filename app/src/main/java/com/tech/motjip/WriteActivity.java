package com.tech.motjip;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tech.motjip.Controller.WriteController;
import com.tech.motjip.Utils.DialogUtil;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private Spinner spinnerTag;
    private Spinner spinnerRegion;

    private EditText etTitle;
    private EditText etLocation;
    private EditText etDate;
    private EditText etContent;

    private ImageView imgPreview;
    private TextView tvFileName;

    private Button btnAddImage;
    private Button btnUpload;
    private Button btnCancel;

    private Uri selectedImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    private WriteController writeController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        writeController = new WriteController(this);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {

                    if (uri != null) {

                        selectedImageUri = uri;

                        imgPreview.setImageURI(uri);

                        String fileName =
                                writeController.getFileName(uri);

                        tvFileName.setText(fileName);

                        DialogUtil.showCustomDialog(
                                this,
                                R.drawable.success,
                                "이미지 선택",
                                "이미지가 선택되었습니다.",
                                null
                        );
                    }
                }
        );

        btnBack = findViewById(R.id.btnBack);

        spinnerTag = findViewById(R.id.spinnerTag);
        spinnerRegion = findViewById(R.id.spinnerRegion);

        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etContent = findViewById(R.id.etContent);

        imgPreview = findViewById(R.id.imgPreview);
        tvFileName = findViewById(R.id.tvFileName);

        btnAddImage = findViewById(R.id.btnAddImage);
        btnUpload = findViewById(R.id.btnUpload);
        btnCancel = findViewById(R.id.btnCancel);

        String[] tags = {
                "한식",
                "중식",
                "양식",
                "일식",
                "분식",
                "카페"
        };

        ArrayAdapter<String> tagAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        tags
                );

        spinnerTag.setAdapter(tagAdapter);

        String[] regions = {
                "서울",
                "부산",
                "대구",
                "인천",
                "광주",
                "대전",
                "울산",
                "세종",
                "경기",
                "강원",
                "충북",
                "충남",
                "전북",
                "전남",
                "경북",
                "경남",
                "제주"
        };

        ArrayAdapter<String> regionAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        regions
                );

        spinnerRegion.setAdapter(regionAdapter);

        etDate.setFocusable(false);
        etDate.setClickable(true);

        etDate.setOnClickListener(v -> {

            Calendar now = Calendar.getInstance();

            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            int day = now.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            WriteActivity.this,
                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                TimePickerDialog timePickerDialog =
                                        new TimePickerDialog(
                                                WriteActivity.this,
                                                (timeView, selectedHour, selectedMinute) -> {

                                                    Calendar selectedDateTime =
                                                            Calendar.getInstance();

                                                    selectedDateTime.set(
                                                            selectedYear,
                                                            selectedMonth,
                                                            selectedDay,
                                                            selectedHour,
                                                            selectedMinute,
                                                            0
                                                    );

                                                    if (selectedDateTime.before(Calendar.getInstance())) {

                                                        DialogUtil.showCustomDialog(
                                                                WriteActivity.this,
                                                                R.drawable.fail,
                                                                "날짜 오류",
                                                                "현재 시간 이후로 선택해 주세요.",
                                                                null
                                                        );

                                                        return;
                                                    }

                                                    String selectedDateTimeText =
                                                            selectedYear + "-"
                                                                    + String.format("%02d", selectedMonth + 1)
                                                                    + "-"
                                                                    + String.format("%02d", selectedDay)
                                                                    + " "
                                                                    + String.format("%02d", selectedHour)
                                                                    + ":"
                                                                    + String.format("%02d", selectedMinute);

                                                    etDate.setText(selectedDateTimeText);

                                                },
                                                now.get(Calendar.HOUR_OF_DAY),
                                                now.get(Calendar.MINUTE),
                                                true
                                        );

                                timePickerDialog.show();

                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.getDatePicker().setMinDate(
                    now.getTimeInMillis()
            );

            datePickerDialog.show();
        });

        btnBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> finish());

        btnAddImage.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*")
        );

        btnUpload.setOnClickListener(v -> {

            String tag =
                    spinnerTag.getSelectedItem().toString();

            String region =
                    spinnerRegion.getSelectedItem().toString();

            String title =
                    etTitle.getText().toString().trim();

            String location =
                    etLocation.getText().toString().trim();

            String date =
                    etDate.getText().toString().trim();

            String content =
                    etContent.getText().toString().trim();

            if (title.isEmpty()) {

                DialogUtil.showCustomDialog(
                        this,
                        R.drawable.fail,
                        "입력 오류",
                        "제목을 입력해 주세요.",
                        null
                );

                return;
            }

            if (location.isEmpty()) {

                DialogUtil.showCustomDialog(
                        this,
                        R.drawable.fail,
                        "입력 오류",
                        "위치를 입력해 주세요.",
                        null
                );

                return;
            }

            if (date.isEmpty()) {

                DialogUtil.showCustomDialog(
                        this,
                        R.drawable.fail,
                        "입력 오류",
                        "날짜를 선택해 주세요.",
                        null
                );

                return;
            }

            if (content.isEmpty()) {

                DialogUtil.showCustomDialog(
                        this,
                        R.drawable.fail,
                        "입력 오류",
                        "내용을 입력해 주세요.",
                        null
                );

                return;
            }

            if (selectedImageUri == null) {

                DialogUtil.showCustomDialog(
                        this,
                        R.drawable.fail,
                        "입력 오류",
                        "이미지를 선택해 주세요.",
                        null
                );

                return;
            }

            btnUpload.setEnabled(false);

            writeController.uploadCommunityPost(
                    tag,
                    region,
                    title,
                    location,
                    date,
                    content,
                    selectedImageUri,
                    new Callback<Void>() {

                        @Override
                        public void onResponse(
                                Call<Void> call,
                                Response<Void> response
                        ) {

                            if (response.isSuccessful()) {

                                DialogUtil.showCustomDialog(
                                        WriteActivity.this,
                                        R.drawable.success,
                                        "등록 완료",
                                        "게시글이 성공적으로 등록되었습니다.",
                                        () -> {

                                            setResult(RESULT_OK);

                                            finish();
                                        }
                                );

                            } else {

                                btnUpload.setEnabled(true);

                                DialogUtil.showCustomDialog(
                                        WriteActivity.this,
                                        R.drawable.fail,
                                        "등록 실패",
                                        "게시글 등록에 실패했습니다.\n오류 코드 : " + response.code(),
                                        null
                                );
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<Void> call,
                                Throwable t
                        ) {

                            btnUpload.setEnabled(true);

                            DialogUtil.showCustomDialog(
                                    WriteActivity.this,
                                    R.drawable.fail,
                                    "서버 연결 실패",
                                    "서버와 연결할 수 없습니다.\n" + t.getMessage(),
                                    null
                            );
                        }
                    }
            );
        });
    }
}