package com.example.needs.DailogBoxes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.needs.Cart.CartDetails;
import com.example.needs.DataBaseInteractions;
import com.example.needs.CheckoutActivity;
import com.example.needs.Product.Item;
import com.example.needs.R;

import java.util.ArrayList;

public class DialogBoxes {
    AlertDialog.Builder builder;
    public void itemDialog(final Context context, final Item item, final Boolean isCheckout) {
        int q=0;
        final Item row=item;
        builder= new AlertDialog.Builder(context);
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inf.inflate(R.layout.item_dialog_box, null);
        builder.setView(view);
        ImageView itemImage;
        TextView itemName,itemPrice;
        Button add,subtract;
        final EditText quantity;
        itemImage=view.findViewById(R.id.itemImage);
        itemName=view.findViewById(R.id.itemName);
        itemPrice=view.findViewById(R.id.itemPrice);
        add=view.findViewById(R.id.addValue);
        subtract=view.findViewById(R.id.reduceValue);

        quantity=view.findViewById(R.id.quantity);
        Glide.with(context).load(row.getImage()).into(itemImage);

        itemName.setText(row.getName());
        itemPrice.setText(row.getPrice());
        if(isCheckout){
           builder.setPositiveButton("CheckOut", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   int qu=Integer.parseInt(quantity.getText().toString());
                   if(qu<11){
                     CartDetails cartDetails=new DataBaseInteractions().makeCart(row,qu);
                       Intent intent=new Intent(context,CheckoutActivity.class);
                       ArrayList<CartDetails>arrayList=new ArrayList<CartDetails>();
                       arrayList.add(cartDetails);
                       intent.putExtra("cart",arrayList);
                       context.startActivity(intent);
                       //Toast.makeText(context, "checkout clicked", Toast.LENGTH_SHORT).show();

                   }else {
                       int q=Integer.parseInt(quantity.getText().toString());
                       quantity.setText(Integer.toString(q%10));

                   }

               }
           });
        }else {
            builder.setPositiveButton("Add to cart", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(Integer.parseInt(quantity.getText().toString())<11){
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                new DataBaseInteractions().addToCart(row,Integer.parseInt(quantity.getText().toString()));
                            }
                        });
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(context, "quantity should be less then 11", Toast.LENGTH_SHORT).show();


                    }

                }
            });

        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q=Integer.parseInt(quantity.getText().toString());
                if(q<10)
              quantity.setText(Integer.toString(q+1));
            }
        });
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q=Integer.parseInt(quantity.getText().toString());
                if(q>1)
                quantity.setText(Integer.toString(q-1));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        builder.show();
    }

    public void sureDelete(final Context context, final String uid){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete From cart");
        builder.setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                new DataBaseInteractions().deleteFromCart(uid);
                            }
                        });
                        Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }


    public void sureCancel(final Context context, final String uid){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Cancel your order");
        builder.setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                new DataBaseInteractions().cancelOrder(uid);
                            }
                        });
                        Toast.makeText(context, "Order Canceled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }
}
