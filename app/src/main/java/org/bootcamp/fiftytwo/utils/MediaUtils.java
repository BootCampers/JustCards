package org.bootcamp.fiftytwo.utils;

import android.content.Context;
import android.media.MediaPlayer;

import org.bootcamp.fiftytwo.R;

/**
 * Created by baphna on 12/2/2016.
 */

public class MediaUtils {

    private Context mContext;

    public MediaUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void playTingTone(){
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.filling_your_inbox);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    public void playNotAllowedTone(){
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.happy_taxi_horn);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    public void playGlassBreakingTone(){
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.glass_breaking);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }
}
