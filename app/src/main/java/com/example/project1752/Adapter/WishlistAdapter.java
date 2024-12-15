package com.example.project1752.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.project1752.Domain.ItemsDomain;
import com.example.project1752.Activity.DetailActivity;
import com.example.project1752.databinding.ViewholderCartBinding;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.Viewholder> {
    private ArrayList<ItemsDomain> listItemSelected;

    public WishlistAdapter(ArrayList<ItemsDomain> listItemSelected, Context context) {
        this.listItemSelected = listItemSelected;
    }

    @NonNull
    @Override
    public WishlistAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.Viewholder holder, int position) {
        ItemsDomain currentItem = listItemSelected.get(position);

        holder.binding.titleTxt.setText(currentItem.getTitle());
        holder.binding.feeEachItem.setText("$" + currentItem.getPrice());
        holder.binding.numberItemTxt.setVisibility(View.GONE); // Hide item count for wishlist

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(holder.itemView.getContext())
                .load(currentItem.getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.binding.pic);

        // Hide plus and minus buttons
        holder.binding.plusCartBtn.setVisibility(View.GONE);
        holder.binding.minusCartBtn.setVisibility(View.GONE);

        // Navigate to DetailActivity when an item is clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", currentItem);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
