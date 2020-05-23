package com.example.needs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.needs.Cart.CartAdapter;
import com.example.needs.Cart.CartDetails;
import com.example.needs.Cart.OrderDetails;
import com.example.needs.DailogBoxes.DialogBoxes;
import com.example.needs.Product.Item;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView itemRecycleView, addressRecycleView;
    private onItemClickListener itemClickListener,addressClickListener;
    private TextView totalAmount, deliveryCharge;
    private Button proceed;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat;
    private String address,status="unpaid",paymentId=null,deliveryStatus="pending",date,time,amount;
    private ArrayList<CartDetails> cart;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        cart = (ArrayList<CartDetails>) getIntent().getSerializableExtra("cart");
        proceed = findViewById(R.id.proceed);
        totalAmount = findViewById(R.id.totalAmount);
        deliveryCharge = findViewById(R.id.delivery_charge_textView);
        itemRecycleView = findViewById(R.id.cart_item_recycleView);
        addressRecycleView = findViewById(R.id.address_recycleView);
        itemRecycleView.setLayoutManager(new LinearLayoutManager(this));
        addressRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemClickListener = new onItemClickListener() {
            @Override
            public void onClick(View view, int position) {


                switch (view.getId()) {
                    case R.id.deleteItem:
                        Toast.makeText(CheckoutActivity.this, "delete item", Toast.LENGTH_SHORT).show();
                        cart.remove(position);
                        adapter.notifyDataSetChanged();
                        setDeliveryCharge();
                        setTotal();
                        if (cart.size() == 0)
                            onBackPressed();


                        //new DialogBoxes().sureDelete(CheckoutActivity.this,cartDetails.getUid());
                        break;
                }


            }
        };

       setProceedButton();
        adapter = new CartAdapter(this, cart, itemClickListener, true);
        itemRecycleView.setAdapter(adapter);
        setDeliveryCharge();
        setTotal();

        new GetAddress().execute();


    }

    private void setProceedButton() {
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate();
                getTime();
                OrderDetails orderDetails=new OrderDetails(cart,status,paymentId,deliveryStatus,address,amount,time,date);
                for(CartDetails ob:orderDetails.getItems()){
                    if(ob.getItem().getService()){
                        orderDetails.setPick_up_status("pending");
                        break;
                    }
                }
                DataBaseInteractions ob=new DataBaseInteractions();
                orderDetails.setUid(ob.addOrder(orderDetails));
                Intent intent = new Intent(CheckoutActivity.this,PaymentActivity.class);
                intent.putExtra("orderDetails",orderDetails);
                startActivity(intent);
            }
        });
    }

    public void getDate(){
        dateFormat = new SimpleDateFormat("h:mm a" );
        time = dateFormat.format(calendar.getTime());


    }
    public void getTime(){
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());


    }

    public void setDeliveryCharge() {
        if (cart.size() > 0)
            deliveryCharge.setText("Delivery Charge: ₹ 50");
        else
            deliveryCharge.setText("Delivery Charge: ₹ 0");
    }

    private void setTotal() {
        double total = 0.0;
        Iterator<CartDetails> itr = cart.iterator();
        while (itr.hasNext()) {
            total += Double.parseDouble(itr.next().getTotal());
        }
        String dc = deliveryCharge.getText().toString().replaceAll("[^0-9]", "");
        total += Double.parseDouble(dc);
        amount=total+"";
        totalAmount.setText("Total: ₹ " + total);


    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }


    class GetAddress extends AsyncTask<Void, ArrayList<String>, Void> {

        private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        private DatabaseReference mDataBaseRef = mDataBase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());


        @Override
        protected Void doInBackground(Void... voids) {
            mDataBaseRef.child("Address").addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    if (dataSnapshot.exists())
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            // do with your result
                            arrayList.add(data.getValue(String.class));
                        }
                    arrayList.add("+");


                    publishProgress(arrayList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(final ArrayList<String>... result) {
            super.onProgressUpdate(result);
            addressClickListener=new onItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (position==(result[0].size()-1)){
                        Intent intent =new Intent(CheckoutActivity.this,AddAddressActivity.class);
                        CheckoutActivity.this.startActivity(intent);
                        //Toast.makeText(CheckoutActivity.this, "Add Address", Toast.LENGTH_SHORT).show();
                        }

                    else{
                        address=result[0].get(position);
                        //Toast.makeText(CheckoutActivity.this, "Address clicked", Toast.LENGTH_SHORT).show();
                }}
            };


           
            AddressAdapter addressAdapter = new AddressAdapter(CheckoutActivity.this, result[0],addressClickListener);
            addressRecycleView.setAdapter(addressAdapter);
        }


    }

}

class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private Context context;
    private int row_index=-1;
    private onItemClickListener clickListener;
    private ArrayList<String> arrayList;


    public AddressAdapter(Context context, ArrayList<String> arrayList,onItemClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener=clickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_single_item, parent, false);

        AddressAdapter.MyViewHolder viewHolder = new AddressAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        
        holder.addressText.setText(arrayList.get(position));
        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view,position);

                row_index =position;
                notifyDataSetChanged();
            }
        });
        if(row_index ==position){
            holder.addressText.setTextColor(context.getColor(R.color.navfun));
        }

        else
        {
            holder.addressText.setTextColor(context.getColor(R.color.text_color));
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView addressText;
        LinearLayout address;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            address=itemView.findViewById(R.id.address);
            addressText = itemView.findViewById(R.id.address_text);

        }


    }
}
