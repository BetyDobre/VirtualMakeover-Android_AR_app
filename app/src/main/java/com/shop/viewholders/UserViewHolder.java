package com.shop.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.R;
import com.shop.interfaces.ItemClickListener;

// View Holder for app users
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private ItemClickListener itemClickListener;
    public TextView userName, userAddress, userEmail;
    public ImageView userImage;
    public Button showOrdersHistory;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.username);
        userAddress = itemView.findViewById(R.id.user_address);
        userEmail = itemView.findViewById(R.id.user_email);
        userImage = itemView.findViewById(R.id.user_image);
        showOrdersHistory = itemView.findViewById(R.id.show_orders_history__btn);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}