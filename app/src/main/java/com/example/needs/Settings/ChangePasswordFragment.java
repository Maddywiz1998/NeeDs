package com.example.needs.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.needs.LoginActivity;
import com.example.needs.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordFragment extends Fragment {
    private Button btnChangePassword;
    private TextInputEditText currentPassword;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);


        Toast.makeText(getActivity(), "Password change not required while logged in via Google or FaceBook", Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        currentPassword = view.findViewById(R.id.currentPassword);


        final SharedPreferences preferences = getActivity().getSharedPreferences("User_Credential", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final String password = preferences.getString("password", "");
        final String email = preferences.getString("email", "");

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String cPassword = currentPassword.getText().toString().trim();

                if (TextUtils.isEmpty(cPassword)) {

                    Toast.makeText(getActivity(), "Fill current password first", Toast.LENGTH_SHORT).show();
                }
                if (cPassword.contentEquals(password)) {

                    if (TextUtils.isEmpty(email)) {

                        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "You might be logged in with Google or FB Account", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "An email has been sent to you", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();

                                    editor.clear();

                                } else {
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else
                    Toast.makeText(getActivity(), "Current password doesn't matched", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
