package com.shop.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.R;
import com.shop.interfaces.ItemClickListener;

// View Holder for comments
public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ItemClickListener listener;
    public ImageView userImage;
    public TextView userName, commentContent, commentDate;

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.comment_user_img);
        userName = itemView.findViewById(R.id.comment_username);
        commentContent = itemView.findViewById(R.id.comment_content);
        commentDate = itemView.findViewById(R.id.comment_date);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
