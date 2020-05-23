package com.example.needs.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.needs.Product.Item;
import com.example.needs.Product.SubCatagory;
import com.example.needs.R;
import com.example.needs.onItemClickListener;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CardDetail> cardDetailArrayList;
    private ArrayList<SubCatagory> items;
    private onItemClickListener clickListener;
    private Boolean isItem = false;


    public CardAdapter(Context context, ArrayList<CardDetail> cardDetailArrayList, onItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.cardDetailArrayList = cardDetailArrayList;

    }

    public CardAdapter(Context context, ArrayList<SubCatagory> arrayList, onItemClickListener clickListener, Boolean isItem) {
        this.context = context;
        this.clickListener = clickListener;
        this.items = arrayList;
        this.isItem = isItem;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_single_row, parent, false);
        if (isItem) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_row, parent, false);
        }
        MyViewHolder viewHolder = new MyViewHolder(view, clickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // adding animation here
        //animation for images
        holder.grid_image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_animation));

        //animation for whole card view
        holder.row_container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));



        if (isItem) {
            SubCatagory item=items.get(position);
            Glide.with(context).load(item.getImage()).into(holder.grid_image);
            holder.reference_text.setText(item.getName());
            holder.description.setText(item.getDesc());
            if(item.getPrice().length>1){
                holder.row_container.removeView(holder.price);

                for (int i = 0; i <item.getPrice().length ; i++) {
                    RadioButton rdbtn = new RadioButton(context);
                    rdbtn.setId(i);
                    rdbtn.setText("₹"+ item.getPrice()[i]);
                    if(i==0)
                        rdbtn.setChecked(true);
                    holder.priceRadioGroup.addView(rdbtn);
                }

            }else{
                holder.row_container.removeView(holder.priceRadioGroup);
            holder.price.setText(" ₹ " + item.getPrice()[0]);}
        }else {
            CardDetail cardDetail = cardDetailArrayList.get(position);
            Glide.with(context).load(cardDetail.getImage()).into(holder.grid_image);
            holder.reference_text.setText(cardDetail.getName());
            holder.description.setText(cardDetail.getDesc());}

    }

    @Override
    public int getItemCount() {
        if (isItem)
            return items.size();
        else
           return cardDetailArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
        ImageView grid_image, checkout;
        TextView reference_text, description, price;
        onItemClickListener clickListener;
        LinearLayout row_container;
        RadioGroup priceRadioGroup;

        public MyViewHolder(@NonNull View itemView, onItemClickListener clickListener) {
            super(itemView);
            //find view by id
            row_container = itemView.findViewById(R.id.row_container);
            grid_image = itemView.findViewById(R.id.grid_img);
            reference_text = itemView.findViewById(R.id.ref_text);
            description = itemView.findViewById(R.id.des_text);

            this.clickListener = clickListener;
            if (isItem) {
                price = itemView.findViewById(R.id.price_text);
                checkout = itemView.findViewById(R.id.check_out);
                checkout.setOnClickListener(this);
                priceRadioGroup=itemView.findViewById(R.id.priceRadioGroup);
                priceRadioGroup.setOnCheckedChangeListener(this);

            }

                row_container.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            clickListener.onClick(group,checkedId);
        }
    }


}
