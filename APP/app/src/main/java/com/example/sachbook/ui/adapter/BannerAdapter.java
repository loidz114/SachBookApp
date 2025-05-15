package com.example.sachbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Integer> bannerResources;
    private final Context context;

    public BannerAdapter(Context context, List<Integer> bannerResources) {
        this.context = context;
        this.bannerResources = bannerResources;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        int resourceId = bannerResources.get(position % bannerResources.size());
        holder.imageView.setImageResource(resourceId);
    }

    @Override
    public int getItemCount() {
        return bannerResources.isEmpty() ? 0 : Integer.MAX_VALUE; // Infinite scroll
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}