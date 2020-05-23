package com.example.needs;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.needs.Cart.OrderDetails;
import com.google.android.material.textfield.TextInputEditText;

public class AddAddressActivity extends AppCompatActivity {
    private TextInputEditText nameTIl,mobileTIl,pincodeTIl,houseTIl,localityTIl,landmarkTIl,cityTIl;
    private Spinner stateSpinner,slotSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        nameTIl=findViewById(R.id.nameTIL);
        mobileTIl=findViewById(R.id.phoneTIl);
        pincodeTIl=findViewById(R.id.pinCodeTIL);
        houseTIl=findViewById(R.id.houseTIL);
        localityTIl=findViewById(R.id.localityTIL);
        landmarkTIl=findViewById(R.id.landmarkTIL);
        cityTIl=findViewById(R.id.cityTil);
        stateSpinner=findViewById(R.id.stateSpinner);
        slotSpinner=findViewById(R.id.slotSpinner);
    }

    public void addAddress(View view) {
        String  name,mobile,pincode,house,locality,landmark,city,state,slot;
        name=nameTIl.getText().toString();
        mobile=mobileTIl.getText().toString();
        pincode=pincodeTIl.getText().toString();
        house=houseTIl.getText().toString();
        locality=localityTIl.getText().toString();
        landmark=landmarkTIl.getText().toString();
        city=cityTIl.getText().toString();
        state=stateSpinner.getSelectedItem().toString();
        slot=slotSpinner.getSelectedItem().toString();
        if (TextUtils.isEmpty(name)) {

            nameTIl.setError("Required Field");
            nameTIl.setFocusable(true);
            }
        else if (TextUtils.isEmpty(mobile)) {

            mobileTIl.setError("Required Field");
            mobileTIl.setFocusable(true);
        }
        else if (TextUtils.isEmpty(pincode)) {

            pincodeTIl.setError("Required Field");
            pincodeTIl.setFocusable(true);
        }
        if (TextUtils.isEmpty(house)) {

            houseTIl.setError("Required Field");
            houseTIl.setFocusable(true);
        }
        else if (TextUtils.isEmpty(locality)) {

            localityTIl.setError("Required Field");
            localityTIl.setFocusable(true);
        } else if (TextUtils.isEmpty(landmark)) {

            landmarkTIl.setError("Required Field");
            landmarkTIl.setFocusable(true);
        }
        else if (TextUtils.isEmpty(city)) {

            cityTIl.setError("Required Field");
            cityTIl.setFocusable(true);
        }
        else if (state.equalsIgnoreCase("Select Your State")) {
            TextView errorText = (TextView)stateSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("*Required Field");


        }
        else if (slot.equalsIgnoreCase("Select your preference")){
            TextView errorText = (TextView)slotSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("*Required Field");


        }
        else{
            String address=name+"\n"+house+","+landmark+","+locality+","+city+","+state+"-"+pincode+"."+"\nPhone Number: "+mobile;
            new DataBaseInteractions().addAddress(address);
            Toast.makeText(this, "Address added", Toast.LENGTH_SHORT).show();
            finish();

        }

    }
}
