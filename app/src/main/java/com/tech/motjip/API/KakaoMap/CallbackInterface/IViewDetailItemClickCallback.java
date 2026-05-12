package com.tech.motjip.API.KakaoMap.CallbackInterface;

import com.tech.motjip.Model.KeywordMapVO;

// 검색 리스트 아이템 클릭 시 상세페이지 표시를 위한 콜백
public interface IViewDetailItemClickCallback {
    void onItemClick(KeywordMapVO vo);
}
