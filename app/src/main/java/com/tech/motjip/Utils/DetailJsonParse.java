package com.tech.motjip.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tech.motjip.Model.PlaceDetailModel.MenuVO;
import com.tech.motjip.Model.PlaceDetailModel.OpenTimeVO;
import com.tech.motjip.Model.PlaceDetailModel.PlaceDetailVO;
import com.tech.motjip.Model.PlaceDetailModel.ReviewVO;

import java.util.ArrayList;
import java.util.List;

// 카카오 장소 상세(panel3) JSON을 PlaceDetailVO로 변환합니다.
public class DetailJsonParse {

    // JSON 전체를 파싱하여 PlaceDetailVO를 반환합니다.
    public PlaceDetailVO parse(JsonObject json) {
        PlaceDetailVO vo = new PlaceDetailVO();
        parsePhoneNumber(json, vo);
        parseMenus(json, vo);
        parseOpenHours(json, vo);
        parsePhotos(json, vo);
        parseReviews(json, vo);
        return vo;
    }

    // summary.phone_numbers[0].tel
    private void parsePhoneNumber(JsonObject json, PlaceDetailVO vo) {
        if (!json.has("summary")) return;
        JsonObject summary = json.getAsJsonObject("summary");
        if (!summary.has("phone_numbers")) return;
        JsonArray phones = summary.getAsJsonArray("phone_numbers");
        if (phones.isEmpty()) return;
        vo.setPhoneNumber(phones.get(0).getAsJsonObject().get("tel").getAsString());
    }

    // menu.menus.items[] → MenuVO 리스트
    private void parseMenus(JsonObject json, PlaceDetailVO vo) {
        if (!json.has("menu")) return;
        JsonObject menu = json.getAsJsonObject("menu");
        if (!menu.has("menus")) return;
        JsonObject menus = menu.getAsJsonObject("menus");
        if (!menus.has("items")) return;

        List<MenuVO> result = new ArrayList<>();
        for (JsonElement el : menus.getAsJsonArray("items")) {
            JsonObject item = el.getAsJsonObject();
            MenuVO m = new MenuVO();
            m.setMenuName(item.get("name").getAsString());
            m.setMenuPrice(item.get("price").getAsInt());
            result.add(m);
        }
        vo.setMenus(result);
    }

    // open_hours.headline + open_hours.week_from_today.week_periods[].days[] → 평탄화
    private void parseOpenHours(JsonObject json, PlaceDetailVO vo) {
        if (!json.has("open_hours")) return;
        JsonObject openHours = json.getAsJsonObject("open_hours");

        // headline (현재 영업 상태)
        if (openHours.has("headline")) {
            JsonObject h = openHours.getAsJsonObject("headline");
            if (h.has("code")) vo.setOpenStatus(h.get("code").getAsString());
            if (h.has("display_text")) vo.setOpenDisplay(h.get("display_text").getAsString());
            if (h.has("display_text_info")) vo.setOpenTimeText(h.get("display_text_info").getAsString());
        }

        // week_from_today.week_periods[].days[] (요일별 영업시간)
        if (openHours.has("week_from_today")) {
            JsonObject week = openHours.getAsJsonObject("week_from_today");
            if (!week.has("week_periods")) return;

            List<OpenTimeVO> times = new ArrayList<>();
            for (JsonElement period : week.getAsJsonArray("week_periods")) {
                JsonObject pObj = period.getAsJsonObject();
                if (!pObj.has("days")) continue;
                for (JsonElement dayEl : pObj.getAsJsonArray("days")) {
                    JsonObject d = dayEl.getAsJsonObject();
                    OpenTimeVO t = new OpenTimeVO();
                    if (d.has("day_of_the_week_desc")) t.setDay(d.get("day_of_the_week_desc").getAsString());
                    if (d.has("on_days") && d.getAsJsonObject("on_days").has("start_end_time_desc")) {
                        t.setTime(d.getAsJsonObject("on_days").get("start_end_time_desc").getAsString());
                    }
                    times.add(t);
                }
            }
            vo.setOpenTimes(times);
        }
    }

    // photos.photos[].url 만 추출
    private void parsePhotos(JsonObject json, PlaceDetailVO vo) {
        if (!json.has("photos")) return;
        JsonObject photos = json.getAsJsonObject("photos");
        if (!photos.has("photos")) return;

        List<String> urls = new ArrayList<>();
        for (JsonElement el : photos.getAsJsonArray("photos")) {
            JsonObject photo = el.getAsJsonObject();
            if (photo.has("url")) urls.add(photo.get("url").getAsString());
        }
        vo.setPhotoUrls(urls);
    }

    // kakaomap_review.reviews[] → ReviewVO 리스트 (작성자 정보 평탄화)
    private void parseReviews(JsonObject json, PlaceDetailVO vo) {
        if (!json.has("kakaomap_review")) return;
        JsonObject kakaoReview = json.getAsJsonObject("kakaomap_review");
        if (!kakaoReview.has("reviews")) return;

        List<ReviewVO> result = new ArrayList<>();
        for (JsonElement el : kakaoReview.getAsJsonArray("reviews")) {
            JsonObject review = el.getAsJsonObject();
            ReviewVO r = new ReviewVO();

            if (review.has("star_rating")) r.setStarRating(review.get("star_rating").getAsInt());
            if (review.has("contents")) r.setContents(review.get("contents").getAsString());
            if (review.has("updated_at")) r.setUpdatedAt(review.get("updated_at").getAsString());

            // 작성자 정보 (meta.owner) 평탄화
            if (review.has("meta") && review.getAsJsonObject("meta").has("owner")) {
                JsonObject owner = review.getAsJsonObject("meta").getAsJsonObject("owner");
                if (owner.has("nickname")) r.setNickname(owner.get("nickname").getAsString());
                if (owner.has("profile_image_url")) r.setProfileImageUrl(owner.get("profile_image_url").getAsString());
                if (owner.has("review_count")) r.setReviewCount(owner.get("review_count").getAsInt());
                if (owner.has("average_score")) r.setAverageScore(owner.get("average_score").getAsDouble());
            }

            result.add(r);
        }
        vo.setReviews(result);
    }
}
