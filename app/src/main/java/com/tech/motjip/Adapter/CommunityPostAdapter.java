package com.tech.motjip.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import com.tech.motjip.API.RetrofitClient;
import com.tech.motjip.Controller.FavoriteController;
import com.tech.motjip.Model.CommunityPost;
import com.tech.motjip.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityPostAdapter
        extends RecyclerView.Adapter<CommunityPostAdapter.ViewHolder> {

    public interface OnPostClickListener {

        void onPostClick(CommunityPost post);
    }

    private final Context context;

    private final FavoriteController favoriteController;

    private final OnPostClickListener onPostClickListener;

    private final List<CommunityPost> postList =
            new ArrayList<>();

    public CommunityPostAdapter(
            Context context,
            OnPostClickListener onPostClickListener
    ) {

        this.context = context;

        this.onPostClickListener =
                onPostClickListener;

        this.favoriteController =
                new FavoriteController(context);
    }

    public void clearPosts() {

        postList.clear();

        notifyDataSetChanged();
    }

    public void addPosts(List<CommunityPost> posts) {

        int startPosition =
                postList.size();

        postList.addAll(posts);

        notifyItemRangeInserted(
                startPosition,
                posts.size()
        );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_community_post,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        CommunityPost post =
                postList.get(position);

        holder.tvTitle.setText(
                post.getTitle()
        );

        holder.tvTagRegion.setText(
                post.getTag()
                        + " · "
                        + post.getRegion()
        );

        holder.tvPlace.setText(
                post.getPlaceName()
        );

        holder.tvDate.setText(
                post.getMeetingAt()
        );

        if (post.isMine()) {

            holder.ivFavorite.setVisibility(
                    View.GONE
            );

            holder.ivMore.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.ivFavorite.setVisibility(
                    View.VISIBLE
            );

            holder.ivMore.setVisibility(
                    View.GONE
            );

            if (post.isFavorite()) {

                holder.ivFavorite.setColorFilter(
                        Color.parseColor("#FF9800")
                );

            } else {

                holder.ivFavorite.setColorFilter(
                        Color.parseColor("#BBBBBB")
                );
            }
        }

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

            Log.d(
                    "CommunityImageUrl",
                    fullImageUrl
            );

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

            Glide.with(context)
                    .load(glideUrl)
                    .diskCacheStrategy(
                            DiskCacheStrategy.ALL
                    )
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .error(R.drawable.chef_illustration)
                    .into(holder.ivPostImage);

        } else {

            holder.ivPostImage.setImageResource(
                    R.drawable.chef_illustration
            );
        }

        holder.ivFavorite.setOnClickListener(v -> {

            if (post.getComId() == null) {

                Toast.makeText(
                        context,
                        "게시글 정보가 올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean currentFavoriteState =
                    post.isFavorite();

            String message;

            if (currentFavoriteState) {

                message = "즐겨찾기를 취소하시겠습니까?";

            } else {

                message = "즐겨찾기를 추가하시겠습니까?";
            }

            new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setNegativeButton("취소", null)
                    .setPositiveButton(
                            "확인",
                            (dialog, which) -> {

                                favoriteController.toggleFavoriteCommunityPost(
                                        post.getComId(),
                                        new Callback<Boolean>() {

                                            @Override
                                            public void onResponse(
                                                    Call<Boolean> call,
                                                    Response<Boolean> response
                                            ) {

                                                if (
                                                        response.isSuccessful()
                                                                && response.body() != null
                                                ) {

                                                    boolean isFavorite =
                                                            response.body();

                                                    post.setFavorite(
                                                            isFavorite
                                                    );

                                                    if (isFavorite) {

                                                        holder.ivFavorite.setColorFilter(
                                                                Color.parseColor("#FF9800")
                                                        );

                                                        Toast.makeText(
                                                                context,
                                                                "즐겨찾기에 추가되었습니다.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                    } else {

                                                        holder.ivFavorite.setColorFilter(
                                                                Color.parseColor("#BBBBBB")
                                                        );

                                                        Toast.makeText(
                                                                context,
                                                                "즐겨찾기가 취소되었습니다.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }

                                                } else {

                                                    Toast.makeText(
                                                            context,
                                                            "즐겨찾기 처리 실패: "
                                                                    + response.code(),
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(
                                                    Call<Boolean> call,
                                                    Throwable t
                                            ) {

                                                Toast.makeText(
                                                        context,
                                                        "서버 연결 실패: "
                                                                + t.getMessage(),
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                );
                            }
                    )
                    .show();
        });

        holder.ivMore.setOnClickListener(v -> {

            PopupMenu popupMenu =
                    new PopupMenu(
                            context,
                            holder.ivMore
                    );

            popupMenu.getMenu().add(
                    "수정하기"
            );

            popupMenu.getMenu().add(
                    "삭제하기"
            );

            popupMenu.setOnMenuItemClickListener(item -> {

                String title =
                        item.getTitle().toString();

                if (title.equals("수정하기")) {

                    Toast.makeText(
                            context,
                            "수정하기 클릭",
                            Toast.LENGTH_SHORT
                    ).show();

                } else if (title.equals("삭제하기")) {

                    Toast.makeText(
                            context,
                            "삭제하기 클릭",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return true;
            });

            popupMenu.show();
        });

        holder.itemView.setOnClickListener(v -> {

            if (onPostClickListener != null) {

                onPostClickListener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {

        return postList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView ivPostImage;

        TextView tvTitle;
        TextView tvTagRegion;
        TextView tvPlace;
        TextView tvDate;

        ImageView ivFavorite;

        ImageView ivMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPostImage =
                    itemView.findViewById(
                            R.id.ivPostImage
                    );

            tvTitle =
                    itemView.findViewById(
                            R.id.tvTitle
                    );

            tvTagRegion =
                    itemView.findViewById(
                            R.id.tvTagRegion
                    );

            tvPlace =
                    itemView.findViewById(
                            R.id.tvPlace
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            ivFavorite =
                    itemView.findViewById(
                            R.id.ivFavorite
                    );

            ivMore =
                    itemView.findViewById(
                            R.id.ivMore
                    );
        }
    }
}