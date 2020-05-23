package com.example.needs.Cart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.needs.CheckoutActivity;
import com.example.needs.DailogBoxes.DialogBoxes;
import com.example.needs.PaymentActivity;
import com.example.needs.R;
import com.example.needs.onItemClickListener;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<OrderDetails> arrayList;


    public OrderAdapter(Context context, ArrayList<OrderDetails> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_single_row, parent, false);

        OrderAdapter.MyViewHolder viewHolder = new OrderAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.date.setText("Date: "+arrayList.get(position).getDate());
        holder.time.setText("Time: "+arrayList.get(position).getTime());
        holder.status.setText("Status: "+arrayList.get(position).getStatus());
        holder.deliveryAddress.setText("Address: "+arrayList.get(position).getDelivery_address());
        holder.total.setText("Amount: "+arrayList.get(position).getAmount());
        holder.deliveryStatus.setText("Delivery Status: "+arrayList.get(position).getDelivery_status());
        holder.cancelOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DialogBoxes().sureCancel(context,arrayList.get(position).getUid());

            }
        });



        if(arrayList.get(position).getStatus().equalsIgnoreCase("paid")){
            ((ViewGroup)holder.payOrder.getParent()).removeView(holder.payOrder);
        }
        else{
            holder.payOrder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra("orderDetails",arrayList.get(position));
                    context.startActivity(intent);

                }
            });
        }
        if(!(arrayList.get(position).getPick_up_status()==null)){
            holder.pickUpStatus.setText("PickUp Status: "+arrayList.get(position).getPick_up_status());
            if(arrayList.get(position).getPick_up_status().equalsIgnoreCase("pickedup")){
            ((ViewGroup)holder.cancelOrder.getParent()).removeView(holder.cancelOrder);}
            else if(arrayList.get(position).getDelivery_status().equalsIgnoreCase("delivered")){
                    ((ViewGroup)holder.cancelOrder.getParent()).removeView(holder.cancelOrder);
                }

        }
        holder.itemRecycleView.setLayoutManager(new LinearLayoutManager(context));
        CartAdapter cartAdapter=new CartAdapter(context,arrayList.get(position).getItems(),true);
        holder.itemRecycleView.setAdapter(cartAdapter);



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView status,pickUpStatus,deliveryAddress,deliveryStatus,date,time,total;
        RecyclerView itemRecycleView;
        Button cancelOrder,payOrder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            status=itemView.findViewById(R.id.statusTV);
            pickUpStatus=itemView.findViewById(R.id.pickStatusTV);
            total=itemView.findViewById(R.id.amountTV);
            deliveryAddress=itemView.findViewById(R.id.addressTv);
            deliveryStatus=itemView.findViewById(R.id.deliveryStatusTv);
            date=itemView.findViewById(R.id.dateTV);
            time=itemView.findViewById(R.id.timeTV);
            cancelOrder=itemView.findViewById(R.id.cancelOrder);
            payOrder=itemView.findViewById(R.id.payOrder);
            itemRecycleView=itemView.findViewById(R.id.itemRecycleView);


        }



    }
}
