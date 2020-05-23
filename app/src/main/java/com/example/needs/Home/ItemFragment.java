package com.example.needs.Home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.needs.DailogBoxes.DialogBoxes;
import com.example.needs.EmptyFragments.CommingSoonFragment;
import com.example.needs.Product.Item;
import com.example.needs.Product.SubCatagory;
import com.example.needs.R;
import com.example.needs.onItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemFragment extends Fragment {
    private RecyclerView recyclerView;
    private onItemClickListener clickListener;
    private SubCatagory row;
    private String path;
    private Bundle bundle;
    private Boolean isService=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display, container, false);


        //find view by id

        recyclerView = view.findViewById(R.id.display_rcv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        bundle = getArguments();
        String selected=bundle.getString("selectedItem");
        path = "Services/" +selected ;
        if(selected.equalsIgnoreCase("kapdewala")||selected.equalsIgnoreCase("Jootawala"))
            isService=true;

        new GetItem().execute(path);





        return view;
    }


    class GetItem extends AsyncTask<String, ArrayList<SubCatagory>, Boolean> {
        ArrayList<SubCatagory> arrayList = new ArrayList<>();
        private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
        private DatabaseReference mDataBaseRef;
        private int pPrice=0;



        @Override
        protected Boolean doInBackground(String... strings) {

            mDataBaseRef = mDataBase.getReference(strings[0]);

            mDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> ref_names = new ArrayList<>(), des_text = new ArrayList<>(), grid_images = new ArrayList<>();
                    ArrayList<String[]>price = new ArrayList<>();



                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (!ds.getKey().equalsIgnoreCase("desc") && !ds.getKey().equalsIgnoreCase("image")) {
                            ref_names.add(ds.getKey());
                            grid_images.add(ds.child("image").getValue(String.class));
                            des_text.add(ds.child("desc").getValue(String.class));

                            ArrayList<String> subprices= new ArrayList<>();

                            for(DataSnapshot subprice :ds.child("price").getChildren())
                                subprices.add(subprice.getValue(String.class));


                            price.add(getStringArray(subprices));

                        }
                    }

                    for (int i = 0; i < ref_names.size(); i++) {
                        row = new SubCatagory(ref_names.get(i), des_text.get(i), grid_images.get(i),price.get(i));
                        arrayList.add(row);
                    }
                    //publishProgress
                    publishProgress(arrayList);


                }
                public  String[] getStringArray(ArrayList<String> arr)
                {

                    // declaration and initialise String Array
                    String str[] = new String[arr.size()];

                    // Convert ArrayList to object array
                    Object[] objArr = arr.toArray();

                    // Iterating and converting to String
                    int i = 0;
                    for (Object obj : objArr) {
                        str[i++] = (String)obj;
                    }

                    return str;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return true;
        }

        @Override
        protected void onProgressUpdate(final ArrayList<SubCatagory>... result) {
            super.onProgressUpdate(result);
            if (result[0].size() == 0) {
                Fragment fragment = new CommingSoonFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                fm.popBackStack();
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();


            } else {


                clickListener = new onItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        SubCatagory subCatagory;
                        Item row;


                        switch (view.getId()) {
                            case R.id.row_container:
                               subCatagory = result[0].get(position);
                                row=new Item(subCatagory.getName(),subCatagory.getDesc(),subCatagory.getPrice()[getpPrice()],subCatagory.getImage(),isService);
                                new DialogBoxes().itemDialog(getContext(), row, false);
                                break;
                            case R.id.check_out:
                              subCatagory = result[0].get(position);
                                row=new Item(subCatagory.getName(),subCatagory.getDesc(),subCatagory.getPrice()[getpPrice()],subCatagory.getImage(),isService);
                                new DialogBoxes().itemDialog(getContext(), row, true);
                                break;
                            case R.id.priceRadioGroup:
                                setpPrice(position);

                                break;

                        }
                    }
                };

                //setting adapter
                CardAdapter adapter = new CardAdapter(getActivity(), result[0], clickListener, true);
                recyclerView.setAdapter(adapter);
            }


        }
        private void setpPrice(int p){
            pPrice=p;
        }
        private int getpPrice(){return pPrice;}

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

        }

    }




}
