package com.example.problemfix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ServiceCreationActivity extends AppCompatActivity implements View.OnClickListener {

    public Bitmap file_bitmap;
    private Button reservationButton, complanitsButton, reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_creation);
        reservationButton = findViewById(R.id.reservationButton);
        complanitsButton = findViewById(R.id.complaintsButton);
        reportButton = findViewById(R.id.reportButton);

        reservationButton.setOnClickListener(this);
        complanitsButton.setOnClickListener(this);
        reportButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.reservationButton) {
            intent = new Intent(this, ReserverationActivity.class);
        } else if (v.getId() == R.id.complaintsButton) {
            intent = new Intent(this, ReserverationActivity.class);
        } else {
            intent = new Intent(this, ReportActivity.class);
        }


        startActivity(intent);


    }


}
