package com.example.carpartsshop.ui.parts;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carpartsshop.data.CartManager;
import com.example.carpartsshop.data.CartManager.CartItem;
import com.example.carpartsshop.databinding.ItemCartBinding;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartVH> {

    public interface OnCartChange {
        void onItemRemoved();
    }

    private final List<CartItem> items;
    private final OnCartChange listener;

    public CartAdapter(List<CartItem> items, OnCartChange listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding b = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartVH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull CartVH holder, int position) {
        CartItem ci = items.get(position);
        holder.binding.textName.setText(ci.part.getName());
        holder.binding.textQty.setText("x" + ci.quantity);
        double subtotal = ci.part.getPrice() * ci.quantity;
        holder.binding.textSubtotal.setText(String.format("%.2f €", subtotal));

        holder.binding.buttonRemove.setOnClickListener(v -> {
            CartManager.getInstance().removeAll(ci.part);
            items.remove(position);
            notifyItemRemoved(position);
            listener.onItemRemoved();
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class CartVH extends RecyclerView.ViewHolder {
        final ItemCartBinding binding;
        CartVH(ItemCartBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
