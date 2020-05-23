package com.example.needs.Cart;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.needs.DailogBoxes.DialogBoxes;
import com.example.needs.EmptyFragments.EmptyOrderFragment;
import com.example.needs.R;
import com.example.needs.onItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private onItemClickListener clickListener;
    private Fragment fragment = null;
    private FragmentManager fm;


    @Override
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
        recyclerView = view.findViewById(R.id.display_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }


    class GetData extends AsyncTask<Void, ArrayList<OrderDetails>,Boolean> {

        private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        private DatabaseReference mDataBaseRef=mDataBase.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());



        @Override
        protected Boolean doInBackground(Void... voids) {
            mDataBaseRef.child("Orders").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<OrderDetails> arrayList = new ArrayList<>();

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if(data.child("items").exists()&&data.child("status").exists())
                              arrayList.add(data.getValue(OrderDetails.class));


                        }

                    }

                    publishProgress(arrayList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });




            return true;
        }

        @Override
        protected void onProgressUpdate(final ArrayList<OrderDetails>... result) {
            super.onProgressUpdate(result);
            if (result[0].size() == 0) {
                fragment = new EmptyOrderFragment();
                FragmentTransaction ft = fm.beginTransaction();
                fm.popBackStack();
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();


            } else {

                clickListener = new onItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        OrderDetails row= result[0].get(position);

                        switch (view.getId()) {
                            case R.id.row_container:
                                Toast.makeText(getContext(), "Item description", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.check_out:
                                Toast.makeText(getContext(), "checkout clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.deleteItem:
                                final OrderDetails orderDetails=result[0].get(position);
                                new DialogBoxes().sureCancel(getContext(),orderDetails.getUid());
                                break;
                        }


                    }};
                OrderAdapter adapter=new OrderAdapter(getContext(),result[0]);
                recyclerView.setAdapter(adapter);


            }


        }



        @Override
        protected void onPostExecute( Boolean result) {
            super.onPostExecute(result);


        }

    }
}
