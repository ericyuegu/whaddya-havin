package com.ericyuegu.whaddyahavin.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ericyuegu.whaddyahavin.R;
import com.squareup.picasso.Picasso;

public class MealViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public MealViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setMeal(Context ctx, String title, String desc, String image) {
        TextView meal_title = mView.findViewById(R.id.meal_title);
        TextView meal_desc = mView.findViewById(R.id.meal_desc);
        ImageView meal_Image = mView.findViewById(R.id.meal_image);

        meal_title.setText(title);
        meal_desc.setText((desc));
        Picasso.get().load(image).into(meal_Image);
    }

//    public void setTitle(String title) {
//        TextView meal_title = mView.findViewById(R.id.meal_title);
//        meal_title.setText(title);
//    }
//
//    public void setDesc(String desc) {
//        TextView meal_desc = mView.findViewById(R.id.meal_desc);
//        meal_desc.setText((desc));
//    }
//
//    public void setImage(Context ctx, String image) {
//        ImageView meal_Image = mView.findViewById(R.id.meal_image);
//        Picasso.get().load(image).into(meal_Image);
//    }
}
