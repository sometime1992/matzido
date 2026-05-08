//package com.tech.motjip;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.kakao.vectormap.LatLng;
//import com.kakao.vectormap.MapView;
//import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
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
//    @Inject
//    TestController controller;
//
//    private IThreadCallback callback;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_test);
//
//        MapView mapView = findViewById(R.id.map_view);
//        EditText etSearch = findViewById(R.id.et_search);
//        Button btnSearch = findViewById(R.id.btn_search);
//
//        LinearLayout resultContainer = findViewById(R.id.ll_search_result_container);
//        LayoutInflater inflater = LayoutInflater.from(this);
//
//        // 지도 로드 끝나면 버튼 이벤트 활성화
//        callback = new IThreadCallback() {
//            @Override
//            public void ThreadEnds() {
//                btnSearch.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Log.d("Test", "test");
//                        String keyword = etSearch.getText().toString();
//
//                        if (keyword.isEmpty()) {
//                            Toast.makeText(TestActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        IThreadReturn1Callback<List<KeywordMapVO>> result = new IThreadReturn1Callback<List<KeywordMapVO>>() {
//                            @Override
//                            public void ThreadEnds(List<KeywordMapVO> result) {
//                                if (result.isEmpty()) {
//                                    Log.d("TestLog", "결과없음");
//                                    return;
//                                }
//
//                                // 명시적으로 주 쓰레드에서 뷰 데이터처리
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        resultContainer.removeAllViews();
//
//                                        for (KeywordMapVO vo : result) {
//                                            View itemView = inflater.inflate(R.layout.item_search_result, resultContainer, false);
//                                            TextView tvCategory = itemView.findViewById(R.id.tv_category);
//                                            TextView tvName = itemView.findViewById(R.id.tv_place_name);
//                                            TextView tvAddress = itemView.findViewById(R.id.tv_address);
//
//                                            tvCategory.setText(vo.getCategory_name());
//                                            tvName.setText(vo.getPlace_name());
//                                            tvAddress.setText(vo.getRoad_address_name());
//
//                                            resultContainer.addView(itemView);
//                                        }
//
//                                        controller.clearMarkers();
//                                        controller.drawMarker(result);
//                                        LatLng position = MapHelper.getLatLng(result.get(0).getX(), result.get(0).getY());
//                                        controller.moveMapCamara(position);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Log.e("MapDebug", "원인: ", e);
//                            }
//                        };
//
//                        controller.searchMapData(keyword, result);
//
//                    }
//                });
//            }
//        };
//
//        controller.mapStart(mapView, callback);
//    }
//
//
//    // 추가로 지도 리사이클 핸들링 필요
//    // https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/#3-지도-시작-및-kakaomap-객체-가져오기
//}