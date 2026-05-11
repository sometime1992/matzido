//package com.tech.motjip;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//
//import com.kakao.vectormap.LatLng;
//import com.kakao.vectormap.MapView;
//import com.tech.motjip.Controller.TestController;
//import com.tech.motjip.Handler.BaseActivity;
//import com.tech.motjip.Model.KeywordMapVO;
//import com.tech.motjip.Thread.IThreadCallback;
//import com.tech.motjip.Thread.IThreadReturn1Callback;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class TestActivity extends BaseActivity {
//
//    TestController controller = new TestController(this);
//
//    private MapView mapView;
//    private IThreadCallback callback;
//
//    // GPS 좌표 요청 코드
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        mapView = findViewById(R.id.map_view);
//        EditText etSearch = findViewById(R.id.et_search);
//        Button btnSearch = findViewById(R.id.btn_search);
//        Button btnToggle = findViewById(R.id.btn_toggle_suggestion);
//        View overlaySuggestion = findViewById(R.id.overlay_suggestion);
//        LinearLayout suggestionContainer = findViewById(R.id.ll_suggestion_container);
//        LinearLayout resultContainer = findViewById(R.id.ll_search_result_container);
//        View loadingView = findViewById(R.id.view_loading);
//        LayoutInflater inflater = LayoutInflater.from(this);
//
//        // 검색 관련 처리 콜백 여기서 처리
//        IThreadReturn1Callback<List<KeywordMapVO>> searchCallback =
//                controller.createSearchCallback(resultContainer, suggestionContainer, overlaySuggestion, inflater);
//
//        // 지도 로드 될때 처리
//        callback = new IThreadCallback() {
//            @Override
//            public void ThreadEnds() {
//                // 버튼으로 검색 오버레이 처리
//                btnToggle.setOnClickListener(v -> {
//                    if (overlaySuggestion.getVisibility() == View.VISIBLE) {
//                        closeSearchOverlay(overlaySuggestion, btnToggle);
//                    } else {
//                        openSearchOverlay(overlaySuggestion, btnToggle);
//                    }
//                });
//
//                // 검색창 클릭시 오버레이 처리
//                etSearch.setOnFocusChangeListener((v, hasFocus) -> {
//                    if (hasFocus) openSearchOverlay(overlaySuggestion, btnToggle);
//                });
//                etSearch.setOnClickListener(v -> openSearchOverlay(overlaySuggestion, btnToggle));
//
//                // 입력이 멈춘 뒤 일정 시간이 지났을 때 검색을 실행하는 디바운스 핸들러
//                // Runnable을 배열로 감싸야 객체가 바뀌지 않음 정적 배열은 한번 할당하면 객체가 안바뀜
//                Handler debounceHandler = new Handler(Looper.getMainLooper());
//                Runnable[] debounceRunnable = {null};
//
//                // 텍스트 체인지이벤트 처리
//                etSearch.addTextChangedListener(new TextWatcher() {
//                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        // 이전에 예약된 검색 요청 취소 (연속 입력 시 중복 API 호출 방지)
//                        if (debounceRunnable[0] != null) {
//                            debounceHandler.removeCallbacks(debounceRunnable[0]);
//                        }
//
//                        String keyword = s.toString().trim();
//
//                        if (keyword.isEmpty()) {
//                            suggestionContainer.removeAllViews();
//                            return;
//                        }
//
//                        // 0.5초 후 실행 예약 — 그 사이 입력이 오면 위에서 취소되고 다시 예약됨
//                        debounceRunnable[0] = () -> controller.searchMapData(keyword, new IThreadReturn1Callback<List<KeywordMapVO>>() {
//                            @Override
//                            public void ThreadEnds(List<KeywordMapVO> result) {
//                                runOnUiThread(() -> {
//                                    suggestionContainer.removeAllViews();
//                                    for (KeywordMapVO vo : result) {
//                                        View item = inflater.inflate(R.layout.item_suggestion, suggestionContainer, false);
//                                        ((TextView) item.findViewById(R.id.tv_suggestion_name)).setText(vo.getPlace_name());
//                                        // 검색 리스트 아이템에 온클릭 이벤트 처리(검색)
//                                        item.setOnClickListener(v -> doSearch(vo.getPlace_name(), searchCallback));
//                                        suggestionContainer.addView(item);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Log.e("Suggestion", "검색 오류", e);
//                            }
//                        });
//                        // 0.5초 단위로 처리
//                        debounceHandler.postDelayed(debounceRunnable[0], 500);
//                    }
//
//                    @Override public void afterTextChanged(Editable s) {}
//                });
//                // 검색버튼 처리
//                btnSearch.setOnClickListener(v -> {
//                    String keyword = etSearch.getText().toString().trim();
//                    if (keyword.isEmpty()) {
//                        return;
//                    }
//                    doSearch(keyword, searchCallback);
//                });
//
//                // 맵 로딩 완료 후 로딩 뷰 제거
//                loadingView.setVisibility(View.GONE);
//            }
//        };
//
//        // GPS 권한 처리 시작
//        getGPSPosition();
//    }
//
//    // 입력값에 따른 검색을 수행합니다.
//    private void doSearch(String keyword, IThreadReturn1Callback<List<KeywordMapVO>> callback) {
//        controller.searchMapData(keyword, callback);
//    }
//
//    // GPS와 관련된 권한을 처리합니다.
//    private void getGPSPosition() {
//        // 만약 GPS 권한이 없다면
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // 권한 요청을 시도하고 함수를 종료합니다.
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        // 권한이 있다면 맵이동 처리 관련 로직을 수행합니다.
//        fetchLocationAndStartMap();
//    }
//
//    // 권한 요청에 대한 처리를 시도합니다.
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // GPS권한 요청이라면
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            // 사용자가 허용했다면
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //GPS 기반으로 맵 시작
//                fetchLocationAndStartMap();
//            } else {
//                // 거부했다면 기본 좌표로 맵 시작
//                controller.mapStart(mapView, callback);
//            }
//        }
//    }
//
//    // GPS 좌표에 따른 맵 이동을 처리합니다.
//    private void fetchLocationAndStartMap() {
//        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        controller.fetchLocation(lm, new IThreadReturn1Callback<LatLng>() {
//            @Override
//            public void ThreadEnds(LatLng latLng) {
//                // 성공했을경우 맵로드 가 GPS 기반 좌표값에 따라 이동됨
//                controller.mapStart(mapView, callback, latLng);
//            }
//            @Override
//            public void onError(Exception e) {
//                // 실패했을 경우 기본 좌표 사용
//                controller.mapStart(mapView, callback);
//            }
//        });
//    }
//
//    private void openSearchOverlay(View overlay, Button toggle) {
//        overlay.setVisibility(View.VISIBLE);
//        toggle.setText("▲");
//    }
//
//    private void closeSearchOverlay(View overlay, Button toggle) {
//        overlay.setVisibility(View.GONE);
//        toggle.setText("▼");
//    }
//
//    // 추가로 지도 리사이클 핸들링 필요
//    // https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/#3-지도-시작-및-kakaomap-객체-가져오기
//}
