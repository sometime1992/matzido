package com.tech.motjip;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Controller.CommunityController;
import com.tech.motjip.Model.CommunityPost;

public class CommunityDetailActivity
        extends AppCompatActivity {

    private ImageView ivPostImage;

    private TextView tvTitle;
    private TextView tvTagRegion;
    private TextView tvPlace;
    private TextView tvDate;
    private TextView tvContent;

    private Button btnJoin;

    private CommunityController communityController;

    private CommunityPost post;

    private Long comId;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_community_detail
        );

        communityController =
                new CommunityController(this);

        ivPostImage =
                findViewById(R.id.ivPostImage);

        tvTitle =
                findViewById(R.id.tvTitle);

        tvTagRegion =
                findViewById(R.id.tvTagRegion);

        tvPlace =
                findViewById(R.id.tvPlace);

        tvDate =
                findViewById(R.id.tvDate);

        tvContent =
                findViewById(R.id.tvContent);

        btnJoin =
                findViewById(R.id.btnJoin);

        post =
                (CommunityPost) getIntent()
                        .getSerializableExtra(
                                "communityPost"
                        );

        if (post == null || post.getComId() == null) {

            Toast.makeText(
                    this,
                    "게시글 정보가 올바르지 않습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        comId =
                post.getComId();

        setupPostData();

        setupJoinButton();

        setupJoinedState();
    }

    private void setupPostData() {

        tvTitle.setText(
                post.getTitle()
        );

        tvTagRegion.setText(
                post.getTag()
                        + " · "
                        + post.getRegion()
        );

        tvPlace.setText(
                post.getPlaceName()
        );

        tvDate.setText(
                post.getMeetingAt()
        );

        tvContent.setText(
                post.getContent()
        );

        String imageUrl =
                post.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            String baseUrl =
                    RetrofitClient.BASE_URL
                            .replace("/api/", "/")
                            .replace("/api", "");

            if (baseUrl.endsWith("/")) {

                baseUrl =
                        baseUrl.substring(
                                0,
                                baseUrl.length() - 1
                        );
            }

            if (!imageUrl.startsWith("/")) {

                imageUrl =
                        "/" + imageUrl;
            }

            String fullImageUrl =
                    baseUrl + imageUrl;

            GlideUrl glideUrl =
                    new GlideUrl(
                            fullImageUrl,
                            new LazyHeaders.Builder()
                                    .addHeader(
                                            "ngrok-skip-browser-warning",
                                            "true"
                                    )
                                    .build()
                    );

            Glide.with(this)
                    .load(glideUrl)
                    .diskCacheStrategy(
                            DiskCacheStrategy.ALL
                    )
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .error(R.drawable.chef_illustration)
                    .into(ivPostImage);

        } else {

            Glide.with(this)
                    .load(R.drawable.chef_illustration)
                    .into(ivPostImage);
        }
    }

    private void setupJoinedState() {

        if (post.isJoined()) {

            btnJoin.setText(
                    "이미 참여함"
            );

            btnJoin.setEnabled(false);
        }
    }

    private void setupJoinButton() {

        btnJoin.setOnClickListener(v -> {

            btnJoin.setEnabled(false);

            communityController.joinCommunity(
                    comId,
                    new CommunityController.JoinCommunityCallback() {

                        @Override
                        public void onSuccess() {

                            post.setJoined(true);

                            setResult(RESULT_OK);

                            Toast.makeText(
                                    CommunityDetailActivity.this,
                                    "모임 참여 완료",
                                    Toast.LENGTH_SHORT
                            ).show();

                            btnJoin.setText(
                                    "참여 완료"
                            );

                            btnJoin.setEnabled(false);
                        }

                        @Override
                        public void onAlreadyJoined() {

                            post.setJoined(true);

                            setResult(RESULT_OK);

                            Toast.makeText(
                                    CommunityDetailActivity.this,
                                    "이미 참여한 모임입니다.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            btnJoin.setEnabled(false);

                            btnJoin.setText(
                                    "이미 참여함"
                            );
                        }

                        @Override
                        public void onError(
                                String message
                        ) {

                            Toast.makeText(
                                    CommunityDetailActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                            btnJoin.setEnabled(true);
                        }
                    }
            );
        });
    }
}