package com.example.needs.Cart;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.needs.CheckoutActivity;
import com.example.needs.DailogBoxes.DialogBoxes;
import com.example.needs.EmptyFragments.EmptyCartFragment;
import com.example.needs.HomeActivity;
import com.example.needs.Product.Item;
import com.example.needs.R;
import com.example.needs.onItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;

    public LinearLayout getContainer() {
        return container;
    }

    public void setContainer(LinearLayout container) {
        this.container = container;
    }

    private LinearLayout container;
    private ArrayList<CartDetails>cartDetailsArrayList;
    private onItemClickListener clickListener;
    private Fragment fragment = null;
    private FragmentManager fm;


    @Override
    @NonNull
    public void onAttach(Context context) {
        super.onAttach(context);
        fm = getActivity().getSupportFragmentManager();
        new GetData().execute();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display
                , container, false);
        //find view by id
        setContainer((LinearLayout) view.findViewById(R.id.container));

        recyclerView = view.findViewById(R.id.display_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));





        return view;
    }
    public ArrayList<CartDetails> getCartDetailsArrayList(){
        return cartDetailsArrayList;
    }


    class GetData extends AsyncTask<Void, ArrayList<CartDetails>,Void> {

        private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        private DatabaseReference mDataBaseRef=mDataBase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());



        @Override
        protected Void doInBackground(Void... voids) {
            mDataBaseRef.child("Cart").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<CartDetails> arrayList = new ArrayList<>();



                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            // do with your result
                            if (data.child("item").exists()&&data.child("quantity").exists()&&data.child("total").exists()&&data.child("uid").exists())
                                arrayList.add(data.getValue(CartDetails.class));
                    }

                    publishProgress(arrayList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });





            return null;
        }

        @Override
        protected void onProgressUpdate(final ArrayList<CartDetails>... result) {
            cartDetailsArrayList=result[0];
            super.onProgressUpdate(result);
            if (result[0].size() == 0 ) {
                getContainer().removeView(recyclerView);
                ImageView imageview = new ImageView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // Add image path from drawable folder.
                imageview.setImageResource(R.drawable.empty_cart);
                imageview.setLayoutParams(params);
                getContainer().addView(imageview);
            } else {

                clickListener = new onItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final CartDetails cartDetails=result[0].get(position);


                        switch (view.getId()) {
                            case R.id.check_out:
                                //Toast.makeText(getContext(), "checkout clicked", Toast.LENGTH_SHORT).show();
                                ArrayList<CartDetails> arrayList = new ArrayList<>();
                                arrayList.add(cartDetails);
                                Intent intent = new Intent(getContext(), CheckoutActivity.class);
                                intent.putExtra("cart", arrayList);
                                startActivity(intent);
                                break;
                            case R.id.deleteItem:
                                new DialogBoxes().sureDelete(getContext(),cartDetails.getUid());
                                break;
                        }


                }};
                //setting adapter
                CartAdapter adapter = new CartAdapter(getActivity(), result[0], clickListener);
                recyclerView.setAdapter(adapter);


            }



        }




    }
}

