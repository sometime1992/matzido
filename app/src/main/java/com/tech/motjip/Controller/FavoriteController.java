package com.tech.motjip.Controller;

import android.content.Context;

import com.tech.motjip.API.ApiService;
import com.tech.motjip.API.RetrofitClient;

import retrofit2.Callback;

public class FavoriteController {

    private final ApiService apiService;

    public FavoriteController(Context context) {

        apiService =
                RetrofitClient.getApiService(context);
    }

    public void toggleFavoriteCommunityPost(
            Long comId,
            Callback<Boolean> callback
    ) {

        apiService.toggleFavoriteCommunityPost(
                comId
        ).enqueue(callback);
    }
}