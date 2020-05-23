package com.example.needs.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.needs.R;

public class SubmitFeedBackFragment extends Fragment {

    private Button btnFeedback;
    private EditText reportText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_feedback, container, false);

        //find view by id
        btnFeedback = view.findViewById(R.id.btnReportSubmit);
        reportText = view.findViewById(R.id.report_text);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = reportText.getText().toString();

                //sending mail as feedback
                Intent in = new Intent(Intent.ACTION_SEND);
                in.putExtra(Intent.EXTRA_EMAIL, new String[]{"mohicomputer8@gmail.com"});
                in.putExtra(Intent.EXTRA_SUBJECT, "App Report");
                in.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                in.setType("message/rfc822");

                startActivity(Intent.createChooser(in, "Choose an Email client :"));


            }
        });
        return view;
    }
}
