package com.example.needs.Cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.needs.R;
import com.example.needs.onItemClickListener;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<CartDetails> arrayList;
    private onItemClickListener clickListener;
    private Boolean inCheckout=false,isOrder=false;

    public CartAdapter(Context context, ArrayList<CartDetails> arrayList, Boolean isOrder) {
        this.context = context;
        this.arrayList = arrayList;
        this.isOrder = isOrder;
    }

    public CartAdapter(Context context, ArrayList<CartDetails> arrayList, onItemClickListener clickListener, Boolean inCheckout) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
        this.inCheckout = inCheckout;
    }

    public CartAdapter(Context context, ArrayList<CartDetails> arrayList, onItemClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
       
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_single_row, parent, false);

        CartAdapter.MyViewHolder viewHolder = new CartAdapter.MyViewHolder(view, clickListener);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // adding animation here
        //animation for images
        holder.itemImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_animation));

        //animation for whole card view
        holder.row_container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        CartDetails cartDetails = arrayList.get(position);
        Glide.with(context).load(cartDetails.getItem().getImage()).into(holder.itemImage);
        holder.itemName.setText(cartDetails.getItem().getName());
        holder.amount.setText("Total: ₹ "+cartDetails.getTotal());
        holder.itemPrice.setText(" ₹ " +cartDetails.getItem().getPrice());
        holder.quantity.setText("Quantity: "+cartDetails.getQuantity());



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImage,checkout,deleteItem;
        TextView itemName,itemPrice,quantity,amount;
        onItemClickListener clickListener;
        LinearLayout row_container ;
        public MyViewHolder(@NonNull View itemView,onItemClickListener clickListener) {
            super(itemView);
            itemImage=itemView.findViewById(R.id.item_img);

            checkout=itemView.findViewById(R.id.check_out);
            deleteItem=itemView.findViewById(R.id.deleteItem);
            itemName=itemView.findViewById(R.id.item_name);
            itemPrice=itemView.findViewById(R.id.item_price);
            quantity=itemView.findViewById(R.id.quantity);
            row_container = itemView.findViewById(R.id.row_container);
            amount=itemView.findViewById(R.id.amount);
            this.clickListener=clickListener;

            if(inCheckout){
                ((ViewGroup) itemView).removeView(checkout);
                row_container.setOnClickListener(this);
                deleteItem.setOnClickListener(this);}
            else if(isOrder){
                ((ViewGroup) itemView).removeView(checkout);
                ((ViewGroup) itemView).removeView(deleteItem);
            }
            else{
                row_container.setOnClickListener(this);
                deleteItem.setOnClickListener(this);
            checkout.setOnClickListener(this);}
        }

        @Override
        public void onClick(View v) {

            clickListener.onClick(v, getAdapterPosition());

        }
    }
}
