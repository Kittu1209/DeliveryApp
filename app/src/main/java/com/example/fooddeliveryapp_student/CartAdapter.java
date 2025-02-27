package com.example.fooddeliveryapp_student;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartList;

    public CartAdapter(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText("₹" + item.getPrice());
        holder.quantity.setText("Qty: " + item.getQuantity());
        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() { return cartList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            quantity = itemView.findViewById(R.id.item_quantity);
            image = itemView.findViewById(R.id.item_image);
        }
    }
}
