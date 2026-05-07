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
import com.example.iotlab4.models.Meal;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.VH> {

    public interface OnClick {
        void onClick(Meal meal);
    }

    private final List<Meal> items;
    private final OnClick listener;

    public MealAdapter(List<Meal> items, OnClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Meal m = items.get(position);
        holder.name.setText(m.getStrMeal());
        holder.id.setText("ID: " + m.getIdMeal());
        Glide.with(holder.image.getContext())
                .load(m.getStrMealThumb())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onClick(m));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView id;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgMeal);
            name = itemView.findViewById(R.id.tvMealName);
            id = itemView.findViewById(R.id.tvMealId);
        }
    }
}