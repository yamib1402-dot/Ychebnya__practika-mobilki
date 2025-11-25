package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BrickAdapter extends RecyclerView.Adapter<BrickAdapter.ViewHolder> {

    private List<Brick> bricks;

    public BrickAdapter(List<Brick> bricks) {
        this.bricks = bricks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_brick, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Brick brick = bricks.get(position);
        holder.tvName.setText(brick.getName());
        holder.tvType.setText("Тип: " + brick.getType());
        holder.tvColor.setText("Цвет: " + brick.getColor());
        holder.tvWeight.setText("Вес: " + brick.getWeight() + " кг");
        holder.tvPrice.setText("Цена: " + brick.getPrice() + " руб");

        String stockStatus = brick.isInStock() ? "В наличии" : "Нет в наличии";
        holder.tvStock.setText(stockStatus);
    }

    @Override
    public int getItemCount() {
        return bricks != null ? bricks.size() : 0;
    }

    public void updateData(List<Brick> newBricks) {
        this.bricks = newBricks;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvType, tvColor, tvWeight, tvPrice, tvStock;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvType = view.findViewById(R.id.tvType);
            tvColor = view.findViewById(R.id.tvColor);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvStock = view.findViewById(R.id.tvStock);
        }
    }
}