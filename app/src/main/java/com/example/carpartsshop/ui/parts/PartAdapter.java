package com.example.carpartsshop.ui.parts;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carpartsshop.data.Part;
import com.example.carpartsshop.databinding.ItemPartBinding;
import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.PartViewHolder> {

    public interface OnClickListener {
        void onPartClick(Part part);
    }

    private List<Part> parts;
    private OnClickListener listener;

    public PartAdapter(List<Part> parts, OnClickListener listener) {
        this.parts = parts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPartBinding b = ItemPartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PartViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull PartViewHolder holder, int position) {
        Part p = parts.get(position);
        holder.binding.textName.setText(p.getName());
        holder.binding.textPrice.setText(String.format("%.2f €", p.getPrice()));
        Glide.with(holder.itemView)
                .load(p.getImageUrl())
                .into(holder.binding.imagePart);
        holder.itemView.setOnClickListener(v -> listener.onPartClick(p));
    }

    @Override
    public int getItemCount() { return parts.size(); }

    static class PartViewHolder extends RecyclerView.ViewHolder {
        final ItemPartBinding binding;
        PartViewHolder(ItemPartBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }
}
