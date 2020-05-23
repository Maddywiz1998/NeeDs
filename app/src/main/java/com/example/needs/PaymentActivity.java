package com.example.needs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.needs.Cart.OrderDetails;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class PaymentActivity extends AppCompatActivity {
    private OrderDetails orderDetails;
    private RadioGroup paymentOption;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference mDataBaseRef = mDataBase.getReference("Users").child(mAuth.getCurrentUser().getUid());
    private PaymentsClient paymentsClient;
    private final int paytmRequestCode = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        orderDetails = (OrderDetails) getIntent().getSerializableExtra("orderDetails");
        String key = mDataBaseRef.child("Orders").push().getKey();
        orderDetails.setUid(key);

        paymentOption = findViewById(R.id.paymentOptions);

        Wallet.WalletOptions walletOptions= new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        paymentsClient=Wallet.getPaymentsClient(this,walletOptions );


    }

    public void onPayClick(View view) {
        int selectedId = paymentOption.getCheckedRadioButtonId();
        switch (selectedId) {
            case R.id.paytm:

                new Paytm().execute();
                break;
            case R.id.googlePay:
                break;
            default:
                Toast.makeText(this, "Select one of the payment options first", Toast.LENGTH_SHORT).show();
        }
    }

    private void paytmPayment() {



            /*Call Backs*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == paytmRequestCode && data != null) {
            JSONObject reader = null;
            try {
                reader = new JSONObject(data.getStringExtra("response"));

                JSONObject body = reader.getJSONObject("body");
                JSONObject resultInfo = body.getJSONObject("resultInfo");
                String resultMsg = resultInfo.getString("resultMsg");
                if (resultMsg.equalsIgnoreCase("Success")) {
                    orderDetails.setStatus("paid");
                    new DataBaseInteractions().addOrder(orderDetails);
                } else {
                    new DataBaseInteractions().addOrder(orderDetails);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }
    }


    class Paytm extends AsyncTask<Void, String, Void> {

        final String MerchantKey = "cBGKY&oOui0N!VsH";
        final String MID = "VlioyF43067128434657";


        private void initializeTransitionAPI() throws JSONException, MalformedURLException {
           String params="ORDER_ID="+orderDetails.getUid()+
                   "&CUST_ID="+mAuth.getCurrentUser().getUid()+
                   "&TXN_AMOUNT="+orderDetails.getAmount()+
                   "&EMAIL="+mAuth.getCurrentUser().getEmail()+
                   "&MOBILE_NO="+mAuth.getCurrentUser().getPhoneNumber();                 ;
            String checksum = null;
            try {
                URL url=new URL("https://us-central1-needs-4cd70.cloudfunctions.net/generateCheckSum?"+params);
                HttpURLConnection conn = null;
                StringBuffer response = new StringBuffer();
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(false);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                    // handle the response
                    int status = conn.getResponseCode();
                    if (status != 200) {
                        throw new IOException("Post failed with error code " + status);
                    } else {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    //Here is your json in string format
                    checksum=response.toString().replace("\"","");
                    publishProgress(checksum);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }



        @Override
        protected Void doInBackground(Void... voids) {
            try {
                initializeTransitionAPI();
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String checksum=values[0];

            PaytmPGService Service = PaytmPGService.getStagingService();
            HashMap<String, String> paramMap = new HashMap<>();
            //paramMap.put("REQUEST_TYPE", "Payment");
            paramMap.put( "MID" , MID);
            paramMap.put( "ORDER_ID" , orderDetails.getUid());
            paramMap.put( "CUST_ID" , mAuth.getCurrentUser().getUid());
            paramMap.put( "MOBILE_NO" , mAuth.getCurrentUser().getPhoneNumber());
            paramMap.put( "EMAIL" , mAuth.getCurrentUser().getEmail());
            paramMap.put( "CHANNEL_ID" , "WAP");
            paramMap.put( "TXN_AMOUNT" , orderDetails.getAmount());
            paramMap.put( "WEBSITE" , "WEBSTAGING");
            paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
            paramMap.put( "CALLBACK_URL", "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
            paramMap.put( "CHECKSUMHASH" , checksum);
            PaytmOrder Order = new PaytmOrder(paramMap);
            Service.initialize(Order, null);
            Service.startPaymentTransaction(PaymentActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                @Override
                public void onTransactionResponse(Bundle bundle) {
                    JSONObject reader = null;
                    try {
                        reader = new JSONObject(bundle.toString());

                        JSONObject body = reader.getJSONObject("body");
                        JSONObject resultInfo = body.getJSONObject("resultInfo");
                        String resultMsg = resultInfo.getString("resultMsg");
                        if (resultMsg.equalsIgnoreCase("Success")) {
                            orderDetails.setStatus("paid");
                            new DataBaseInteractions().addOrder(orderDetails);
                        } else {
                            new DataBaseInteractions().addOrder(orderDetails);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void networkNotAvailable() {
                    Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

                }



                @Override
                public void clientAuthenticationFailed(String s) {
                    Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + s, Toast.LENGTH_LONG).show();

                }

                @Override
                public void someUIErrorOccurred(String s) {
                    Toast.makeText(getApplicationContext(), "UI Error " + s, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onErrorLoadingWebPage(int i, String s, String s1) {
                    Toast.makeText(getApplicationContext(), "Unable to load webpage " + s, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onTransactionCancel(String s, Bundle bundle) {

                }
            });

        }
    }

}
