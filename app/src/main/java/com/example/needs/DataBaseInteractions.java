package com.example.needs;

import com.example.needs.Cart.CartDetails;
import com.example.needs.Cart.OrderDetails;
import com.example.needs.Product.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DataBaseInteractions {
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference mDataBaseRef=mDataBase.getReference("Users").child(mAuth.getCurrentUser().getUid());

    public void addToCart(Item item,int quantity){
        CartDetails cartDetails=makeCart(item,quantity);
        String key = mDataBaseRef.child("Cart").push().getKey();
        cartDetails.setUid(key);
        mDataBaseRef.child("Cart").child(cartDetails.getUid()).setValue(cartDetails);



    }
    public  void deleteFromCart(String cartId){
        Query query = mDataBaseRef.child("Cart").child(cartId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public CartDetails makeCart(Item item,int quantity){

        String numberOnly= item.getPrice().replaceAll("[^0-9]", "");
        Double amount=Double.parseDouble(numberOnly)*quantity;
        CartDetails cartDetails=new CartDetails(item,"",Integer.toString(quantity),Double.toString(amount));
        return cartDetails;

    }
    public String addOrder(OrderDetails orderDetails){
        String key = mDataBaseRef.child("Orders").push().getKey();
        orderDetails.setUid(key);
        mDataBaseRef.child("Orders").child(orderDetails.getUid()).setValue(orderDetails);
        for(CartDetails ob :orderDetails.getItems()){
        if(ob.getUid()!="")
            deleteFromCart(ob.getUid());}
        return key;

    }
    public void cancelOrder(String orderUid){
        Query query = mDataBaseRef.child("Orders").child(orderUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void addAddress(String address){
        String key = mDataBaseRef.child("Address").push().getKey();
        mDataBaseRef.child("Address").child(key).setValue(address);

    }

}
