package com.tech.motjip.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tech.motjip.Model.KeywordMapVO;
import com.tech.motjip.R;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<KeywordMapVO> items = new ArrayList<>();

    public void setItems(List<KeywordMapVO> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KeywordMapVO item = items.get(position);

        holder.tvCategory.setText(item.getCategory_name());
        holder.tvPlaceName.setText(item.getPlace_name());
        holder.tvAddress.setText(item.getRoad_address_name());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvAddress;
        TextView tvPlaceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPlaceName = itemView.findViewById(R.id.tv_place_name);
        }
    }
}
