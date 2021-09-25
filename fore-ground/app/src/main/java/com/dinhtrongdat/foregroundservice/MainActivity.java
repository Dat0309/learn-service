package com.dinhtrongdat.foregroundservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText edtDataIntent;
    private Button btnStartSV, btnStopSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUI();
    }

    private void initUI() {
        edtDataIntent = findViewById(R.id.edt_data_intent);
        btnStartSV = findViewById(R.id.start_service);
        btnStopSV = findViewById(R.id.stop_service);

        btnStartSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStartSV();
            }

        });
        btnStopSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStopSV();
            }
        });
    }

    private void clickStopSV() {
        Intent intent = new Intent(this,MyService.class);
        stopService(intent);
    }

    private void clickStartSV() {
        Song song = new Song("The Chain","FretWordMac",R.drawable.the_chain,R.raw.the_chain);

        Intent intent = new Intent(this,MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        startService(intent);
    }

}