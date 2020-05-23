package com.example.needs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private ImageButton btn_forget;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        //find view by id
        edtEmail = findViewById(R.id.edtEmail);
        btn_forget = findViewById(R.id.forget_button);

        final String email = edtEmail.getText().toString().trim();

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(email) ) {
                    Toast.makeText(ForgetPasswordActivity.this.getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();

                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgetPasswordActivity.this.getApplicationContext(), "Enter a valid email!", Toast.LENGTH_SHORT).show();


                }
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "An email has been sent to " + email, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void backToLogin(View view) {
        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

