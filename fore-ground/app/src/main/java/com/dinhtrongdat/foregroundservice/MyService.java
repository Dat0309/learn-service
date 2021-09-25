package com.dinhtrongdat.foregroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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
                startMusic(song);
                sendNotification(song);
            }
        }

        return START_NOT_STICKY;

    }

    private void startMusic(Song song) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        mediaPlayer.start();
    }

    private void sendNotification(Song song) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),song.getImage());
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.icons8_play);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.icons8_close);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notifi);
        remoteViews.setTextViewText(R.id.txtTitleSong, song.getTitle());
        remoteViews.setTextViewText(R.id.txtSingleSong, song.getSingle());
        remoteViews.setImageViewBitmap(R.id.img_song, bitmap);
        remoteViews.setImageViewBitmap(R.id.btnPlay,bitmap1);
        remoteViews.setImageViewBitmap(R.id.btnClose,bitmap2);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
