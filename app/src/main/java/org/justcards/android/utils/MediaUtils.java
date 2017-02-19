package org.justcards.android.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import org.justcards.android.R;

/**
 * Created by baphna on 12/2/2016.
 */
public class MediaUtils {

    private Context mContext;
    private AudioManager audio;
    private Vibrator vibrator;

    private static final int VIBRATION_DURATION = 500;

    public MediaUtils(Context mContext) {
        this.mContext = mContext;
        this.audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        this.vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private boolean shouldPlayOrVibrate() {
        switch (audio.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return true;
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrator.vibrate(VIBRATION_DURATION);
            case AudioManager.RINGER_MODE_SILENT:
            default:
                return false;
        }
    }

    public void playTingTone() {
        if (shouldPlayOrVibrate()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.filling_your_inbox);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void playNotAllowedTone() {
        if (shouldPlayOrVibrate()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.happy_taxi_horn);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void playGlassBreakingTone() {
        if (shouldPlayOrVibrate()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.glass_breaking);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }

    public void playClapsTone() {
        if (shouldPlayOrVibrate()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.crowd_applause);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }
}