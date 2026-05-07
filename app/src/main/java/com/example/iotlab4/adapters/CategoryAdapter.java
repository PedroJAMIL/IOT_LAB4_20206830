package com.example.iotlab4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iotlab4.R;
import com.example.iotlab4.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnClick {
        void onClick(Category category);
    }

    private final List<Category> items;
    private final OnClick listener;

    public CategoryAdapter(List<Category> items, OnClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category c = items.get(position);
        holder.name.setText(c.getStrCategory());
        holder.description.setText(c.getStrCategoryDescription());
        Glide.with(holder.image.getContext())
                .load(c.getStrCategoryThumb())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCategory);
            name = itemView.findViewById(R.id.tvCategoryName);
            description = itemView.findViewById(R.id.tvCategoryDescription);
        }
    }
}