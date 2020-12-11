package com.shop.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.R;
import com.shop.interfaces.ItemClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productNameTxt, productPriceTxt, productQuantityTxt;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        productNameTxt = itemView.findViewById(R.id.cart_product_name);
        productPriceTxt = itemView.findViewById(R.id.cart_product_price);
        productQuantityTxt = itemView.findViewById(R.id.cart_product_quantity);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
