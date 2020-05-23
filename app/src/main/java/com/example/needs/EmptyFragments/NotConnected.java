package com.example.needs.EmptyFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.needs.R;

public class NotConnected extends Fragment {
@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
    TextView textView=view.findViewById(R.id.text);
    ImageView imageView=view.findViewById(R.id.image);
    imageView.setImageResource(R.drawable.ic_error);
    textView.setText("Connect to Internet");

        return view;
        }}