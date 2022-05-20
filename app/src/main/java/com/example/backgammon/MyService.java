package com.example.backgammon;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.security.Provider;

public class MyService extends Service {
    private MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) { // i don't use this function
        /*
        this function is called when you bind the Service
        param: intent : Intent
        return: null
        */
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
        this function will run when the Service starts and it will start a media player which will
        play music in loop until stopped
        param: intent : Intent, flags : int, startId : int
        return: int
        */
        player = MediaPlayer.create(this,R.raw.backgroundaudio);
        player.setLooping(true);
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        /*
        this function will run on the distraction of this Service and will stop the media player
        param: none
        return: void
        */
        super.onDestroy();
        player.stop();
    }
}
