package com.shop.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.R;

// View Holder for orders
public class UserOrdersViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, userTotalPrice, userDateTime, userPhone, userAddress, userEmail, userState, userPayment;
    public Button showProductsBtn;

    public UserOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.order_username);
        userPhone= itemView.findViewById(R.id.order_phone);
        userTotalPrice = itemView.findViewById(R.id.order_total_price);
        userDateTime = itemView.findViewById(R.id.order_date_time);
        userAddress = itemView.findViewById(R.id.order_address);
        userEmail = itemView.findViewById(R.id.order_email);
        userState = itemView.findViewById(R.id.order_state);
        userPayment = itemView.findViewById(R.id.order_payment_method);
        showProductsBtn = itemView.findViewById(R.id.admin_show_products_btn);
    }
}
