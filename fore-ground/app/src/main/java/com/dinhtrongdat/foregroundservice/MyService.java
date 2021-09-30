package com.dinhtrongdat.foregroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.dinhtrongdat.foregroundservice.MyApp.CHANNEL_ID;

public class MyService extends Service {

    private MediaPlayer mediaPlayer;

    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_RESUME = 2;
    private static final int ACTION_CLEAR = 3;
    private boolean isPlaying;
    private Song mSong;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            Song song = (Song) bundle.get("object_song");

            if(song != null) {
                mSong = song;
                startMusic(song);
                sendNotification(song);
            }
        }
        int actionMusic = intent.getIntExtra("action_music_service", 0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;

    }

    private void startMusic(Song song) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }

        mediaPlayer.start();
        isPlaying = true;
    }

    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;

            case ACTION_RESUME:
                resumeMusic();
                break;

            case ACTION_CLEAR:
                stopSelf();
                break;
        }
    }

    /**
     *
     * Pause Service
     *
    * */
    private void pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(mSong);
        }
    }

    /**
    * Resume Service
    * */
    private void resumeMusic(){
        if(mediaPlayer != null && !isPlaying){
            mediaPlayer.start();
            isPlaying = true;
            sendNotification(mSong);
        }
    }

    private void sendNotification(Song song) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImage());

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.icons8_play);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.icons8_close);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),R.drawable.icons8_pause);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notifi);
        remoteViews.setTextViewText(R.id.txtTitleSong, song.getTitle());
        remoteViews.setTextViewText(R.id.txtSingleSong, song.getSingle());
        remoteViews.setImageViewBitmap(R.id.img_song, bitmap);
        remoteViews.setImageViewBitmap(R.id.btnPlay,bitmap3);
        remoteViews.setImageViewBitmap(R.id.btnClose,bitmap2);

        if(isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.btnPlay, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewBitmap(R.id.btnPlay,bitmap3);
        }
        else{
            remoteViews.setOnClickPendingIntent(R.id.btnPlay, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewBitmap(R.id.btnPlay,bitmap1);
        }

        remoteViews.setOnClickPendingIntent(R.id.btnClose, getPendingIntent(this, ACTION_CLEAR));

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    private  PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this,MyReceiver.class);
        intent.putExtra("action_music",action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_atc");
        intent.putExtra()
    }
}
