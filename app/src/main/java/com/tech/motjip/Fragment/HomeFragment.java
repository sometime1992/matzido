package com.tech.motjip.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tech.motjip.R;
import com.tech.motjip.Thread.IThreadCallback;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.tech.motjip.API.KakaoMap.Utils.MapHelper;
import com.tech.motjip.Controller.TestController;
import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.Thread.IThreadReturn1Callback;

import java.util.List;

public class HomeFragment extends Fragment {

    TestController controller;

    private IThreadCallback callback;

    private MapView mapView;
    private EditText etSearch;
    private Button btnSearch;
    private LinearLayout resultContainer;
    private LayoutInflater inflater;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        controller = new TestController( requireActivity());

        mapView = view.findViewById(R.id.map_view);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        resultContainer = view.findViewById(R.id.ll_search_result_container);
        inflater = LayoutInflater.from(requireContext());

        callback = new IThreadCallback() {
            @Override
            public void ThreadEnds() {
                btnSearch.setOnClickListener(v -> {

                    String keyword = etSearch.getText().toString();

                    if (keyword.isEmpty()) {
                        Toast.makeText(requireContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    IThreadReturn1Callback<List<KeywordMapVO>> result =
                            new IThreadReturn1Callback<List<KeywordMapVO>>() {
                                @Override
                                public void ThreadEnds(List<KeywordMapVO> result) {
                                    if (result.isEmpty()) {
                                        Log.d("TestLog", "결과없음");
                                        return;
                                    }

                                    requireActivity().runOnUiThread(() -> {
                                        resultContainer.removeAllViews();

                                        for (KeywordMapVO vo : result) {
                                            View itemView = inflater.inflate(
                                                    R.layout.item_search_result,
                                                    resultContainer,
                                                    false
                                            );

                                            TextView tvCategory =
                                                    itemView.findViewById(R.id.tv_category);
                                            TextView tvName =
                                                    itemView.findViewById(R.id.tv_place_name);
                                            TextView tvAddress =
                                                    itemView.findViewById(R.id.tv_address);

                                            tvCategory.setText(vo.getCategory_name());
                                            tvName.setText(vo.getPlace_name());
                                            tvAddress.setText(vo.getRoad_address_name());

                                            resultContainer.addView(itemView);
                                        }

                                        controller.clearMarkers();
                                        controller.drawMarker(result);

                                        LatLng position = MapHelper.getLatLng(
                                                result.get(0).getX(),
                                                result.get(0).getY()
                                        );

                                        controller.moveMapCamara(position);
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("MapDebug", "원인: ", e);
                                }
                            };

                    controller.searchMapData(keyword, result);
                });
            }
        };

        // 맵뷰 로드되고나서 맵 로딩
        mapView.post(() -> {
            controller.mapStart(mapView, callback);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    public void onDestroyView() {
        // controller.onDestroy();
        super.onDestroyView();
    }
}