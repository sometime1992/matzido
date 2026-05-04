package com.tech.motjip;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.tech.motjip.API.HttpHelper.GetJsonAsync;
import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Handler.BaseActivity;
import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Thread.IThreadCallback;
import com.tech.motjip.Thread.IThreadReturn1Callback;
import com.tech.motjip.UI.SearchResultAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TestActivity extends BaseActivity {

    @Inject
    TestController controller;

    private IThreadCallback callback;

    private SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        MapView mapView = findViewById(R.id.map_view);
        EditText etSearch = findViewById(R.id.et_search);
        Button btnSearch = findViewById(R.id.btn_search);

        RecyclerView recyclerView = findViewById(R.id.rv_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchResultAdapter();
        recyclerView.setAdapter(adapter);

        // 지도 로드 끝나면 버튼 이벤트 활성화
        callback = new IThreadCallback() {
            @Override
            public void ThreadEnds() {
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Test", "test");
                        String keyword = etSearch.getText().toString();

                        if (keyword.isEmpty()) {
                            Toast.makeText(TestActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        IThreadReturn1Callback<List<KeywordMapVO>> result = new IThreadReturn1Callback<List<KeywordMapVO>>() {
                            @Override
                            public void ThreadEnds(List<KeywordMapVO> result) {
                                if (result.isEmpty()) {
                                    Log.d("TestLog", "결과없음");
                                    return;
                                }

                                adapter.setItems(result);

                                controller.clearMarkers();
                                controller.drawMarker(result);
                                LatLng position = MapHelper.getLatLng(result.get(0).getX(), result.get(0).getY());
                                controller.moveMapCamara(position);
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        };

                        controller.searchMapData(keyword, result);

                    }
                });
            }
        };

        controller.mapStart(mapView, callback);
    }


    // 추가로 지도 리사이클 핸들링 필요
    // https://apis.map.kakao.com/android_v2/docs/getting-started/quickstart/#3-지도-시작-및-kakaomap-객체-가져오기
}