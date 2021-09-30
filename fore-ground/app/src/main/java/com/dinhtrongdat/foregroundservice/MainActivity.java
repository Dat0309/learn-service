package com.dinhtrongdat.foregroundservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText edtDataIntent;
    private Button btnStartSV, btnStopSV;
    RelativeLayout layout_bottom;
    ImageView imgSong, imgPlay, imgClear;
    TextView txtTitle, txtSingle;

    private Song mSong;
    private boolean isPlaying;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null){
                return;
            }
            mSong = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_act"));
        initUI();
    }

    private void initUI() {
        edtDataIntent = findViewById(R.id.edt_data_intent);
        btnStartSV = findViewById(R.id.start_service);
        btnStopSV = findViewById(R.id.stop_service);
        layout_bottom = findViewById(R.id.layout_bottom);
        imgSong = findViewById(R.id.img_song);
        imgPlay = findViewById(R.id.btnPlay);
        imgClear = findViewById(R.id.btnClose);
        txtTitle = findViewById(R.id.txtTitleSong);
        txtSingle = findViewById(R.id.txtSingleSong);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic){
            case MyService.ACTION_START:
                layout_bottom.setVisibility(View.VISIBLE);
                setStatusButton();
                showInfoSong();
                break;
            case MyService.ACTION_CLEAR:
                layout_bottom.setVisibility(View.INVISIBLE);
                break;
            case MyService.ACTION_PAUSE:
                setStatusButton();
                break;
            case MyService.ACTION_RESUME:
                setStatusButton();
                break;
        }
    }

    private void showInfoSong(){
        if(mSong == null){
            return;
        }
        imgSong.setImageResource(mSong.getImage());
        txtTitle.setText(mSong.getTitle());
        txtSingle.setText(mSong.getSingle());

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    sendActionToService(MyService.ACTION_PAUSE);
                }
                sendActionToService(MyService.ACTION_RESUME);
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionToService(MyService.ACTION_CLEAR);
            }
        });
    }

    private void setStatusButton(){
        if(isPlaying){
            imgPlay.setImageResource(R.drawable.icons8_pause);
        }
        imgPlay.setImageResource(R.drawable.icons8_play);
    }

    private void sendActionToService(int action){
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }
}