package com.example.needs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "test";
    private TextInputEditText first_name, sign_input_email, sign_input_password,sign_confirm_password;
    private String name, email, password,confirmpassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");
    private FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();
    private DatabaseReference mDataBaseRef = mDataBase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        //find view by id
        first_name = findViewById(R.id.input_first_name);
        sign_input_email = findViewById(R.id.sign_input_email);
        sign_input_password = findViewById(R.id.sign_input_password);
        sign_confirm_password=findViewById(R.id.sign_confirm_password);
        progressBar = findViewById(R.id.progressBar);

    }

    public void backToLogin(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void student_detail(View view) {

        progressBar.setVisibility(View.VISIBLE);

        name = first_name.getText().toString().trim();
        email = sign_input_email.getText().toString().trim();
        password = sign_input_password.getText().toString().trim();
        confirmpassword=sign_confirm_password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            sign_input_email.setError("Required Field");
            sign_input_email.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);


        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            sign_input_email.setError("Enter a Valid Email Address!");
            sign_input_email.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);


        }
        else if (TextUtils.isEmpty(password)) {

            sign_input_password.setError("Required Field");
            sign_input_password.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);

        } else if(!PASSWORD_PATTERN.matcher(password).matches()){
            sign_input_password.setError("Password pattern does not match!");
            progressBar.setVisibility(View.INVISIBLE);

        }else if(!password.equals(confirmpassword)) {
            sign_confirm_password.setError("Password Does not match!");
            progressBar.setVisibility(View.INVISIBLE);


        }else
         {

            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Check email for verification", Toast.LENGTH_SHORT).show();
                                                    updateUI();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                mDataBaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Name").setValue(name);
                                mDataBaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Email").setValue(email);
                                mDataBaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Password").setValue(password);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateUI() {
        Intent main = new Intent(SignUpActivity.this, LoginActivity.class);
        main.putExtra("usName", name);
        startActivity(main);
        finish();
    }

}
