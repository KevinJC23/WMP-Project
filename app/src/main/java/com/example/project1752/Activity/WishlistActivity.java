package com.example.project1752.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project1752.Adapter.WishlistAdapter;
import com.example.project1752.Helper.Managmentwishlist;
import com.example.project1752.databinding.ActivityWishlistBinding;

public class WishlistActivity extends BaseActivity {
    ActivityWishlistBinding binding;
    private double tax;
    private Managmentwishlist managmentwishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentwishlist = new Managmentwishlist(this);

        setVarialbe();
        initWishlistList();
    }

    private void initWishlistList() {
        if (managmentwishlist.getListwishlist().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewWishlist.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewWishlist.setVisibility(View.VISIBLE);
            binding.wishlistView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.wishlistView.setAdapter(new WishlistAdapter(managmentwishlist.getListwishlist(), this));
        }
        binding.wishlistView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    private void setVarialbe() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

}