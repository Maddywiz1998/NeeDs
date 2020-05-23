package com.example.needs.Home;


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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.needs.R;
import com.example.needs.onItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private onItemClickListener clickListener;
    private CardDetail row;
    private FragmentManager fm;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_display, container, false);
        new GetData().execute();


        //find view by id
        recyclerView = view.findViewById(R.id.display_rcv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fm = getActivity().getSupportFragmentManager();
    }



    class GetData extends AsyncTask<Void, ArrayList<CardDetail>, Void> {
        ArrayList<CardDetail> arrayList = new ArrayList<>();
        private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        private DatabaseReference mDataBaseRef;


        @Override
        protected Void doInBackground(Void...voids) {
            mDataBaseRef = mDataBase.getReference("Services");

            mDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> ref_names = new ArrayList<>(), des_text = new ArrayList<>(),grid_images = new ArrayList<>();


                    for (DataSnapshot ds : dataSnapshot.getChildren()) {


                        ref_names.add(ds.getKey());
                        grid_images.add(ds.child("image").getValue(String.class));
                        des_text.add(ds.child("desc").getValue(String.class));
                    }
                    for (int i = 0; i < ref_names.size(); i++) {
                        row = new CardDetail( ref_names.get(i), des_text.get(i),grid_images.get(i));
                        arrayList.add(row);
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
        protected void onProgressUpdate(final ArrayList<CardDetail>... result) {
            super.onProgressUpdate(result);
            clickListener = new onItemClickListener() {
                @Override
                public void onClick(View view, int position) {


                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("selectedItem",result[0].get(position).getName());
                        Fragment fragment = new ItemFragment();
                        fragment.setArguments(bundle);

                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.addToBackStack(null);
                        ft.commit();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Failed to load data. Wait for a moment", Toast.LENGTH_SHORT).show();
                    }


                }
            };
            //setting adapter
            CardAdapter adapter = new CardAdapter(getActivity(), result[0], clickListener);
            recyclerView.setAdapter(adapter);


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);




        }
    }


}
