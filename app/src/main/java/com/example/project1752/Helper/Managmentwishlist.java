package com.example.project1752.Helper;

import android.content.Context;
import android.widget.Toast;

import com.example.project1752.Domain.ItemsDomain;

import java.util.ArrayList;

public class Managmentwishlist {

    private Context context;
    private TinyDB tinyDB;

    public Managmentwishlist(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertItem(ItemsDomain item) {
        ArrayList<ItemsDomain> listWishlist = getListwishlist();
        boolean existAlready = false;

        for (ItemsDomain wishlistItem : listWishlist) {
            if (wishlistItem.getTitle().equals(item.getTitle())) {
                existAlready = true;
                break;
            }
        }

        if (!existAlready) {
            listWishlist.add(item);
        }

        tinyDB.putListObject("WishlistList", listWishlist);
        Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsDomain> getListwishlist() {
        return tinyDB.getListObject("WishlistList");
    }

    public void minusItem(ArrayList<ItemsDomain> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listfood.get(position).getNumberinWishlist() == 1) {
            listfood.remove(position);
        } else {
            listfood.get(position).setNumberinWishlist(listfood.get(position).getNumberinWishlist() - 1);
        }
        tinyDB.putListObject("wishlistList", listfood);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsDomain> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listfood.get(position).setNumberinCart(listfood.get(position).getNumberinWishlist() + 1);
        tinyDB.putListObject("wishlistList", listfood);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsDomain> listfood2 = getListwishlist();
        double fee = 0;
        for (int i = 0; i < listfood2.size(); i++) {
            fee = fee + (listfood2.get(i).getPrice() * listfood2.get(i).getNumberinCart());
        }
        return fee;
    }
}
