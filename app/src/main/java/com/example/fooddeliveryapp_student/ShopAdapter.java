package com.example.fooddeliveryapp_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<Shop> shops;
    private OnShopClickListener listener;

    public interface OnShopClickListener {
        void onShopClick(Shop shop);
    }

    public ShopAdapter(List<Shop> shops, OnShopClickListener listener) {
        this.shops = shops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        holder.bind(shop, listener);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public void updateList(List<Shop> newList) {
        shops = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        private ImageView shopImage;
        private TextView shopName;
        private TextView shopCategory;
        private RatingBar shopRating;
        private TextView priceRange;
        private TextView deliveryTime;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopImage = itemView.findViewById(R.id.shop_image);
            shopName = itemView.findViewById(R.id.shop_name);
            shopCategory = itemView.findViewById(R.id.shop_category);
            shopRating = itemView.findViewById(R.id.shop_rating);
            priceRange = itemView.findViewById(R.id.price_range);
            deliveryTime = itemView.findViewById(R.id.delivery_time);
        }

        public void bind(Shop shop, OnShopClickListener listener) {
            // Load shop image using Glide
            Glide.with(itemView.getContext())
                    .load(shop.getImageUrl())
                    .placeholder(R.drawable.heartlogo)
                    .into(shopImage);

            shopName.setText(shop.getName());
            shopCategory.setText(shop.getCategory());
            shopRating.setRating((float) shop.getRating());
            priceRange.setText(String.format("₹%.2f - ₹%.2f",
                    shop.getAveragePrice() * 0.8,
                    shop.getAveragePrice() * 1.2));


            itemView.setOnClickListener(v -> listener.onShopClick(shop));
        }
    }
}