package edu.luc.etl.cs313.android.simplestopwatch.common;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Concrete implementation of the BeeperService interface using Android's RingtoneManager.
 *
 * Responsible for handling actual Android OS media playback for notification beeps
 * and continuous alarms while keeping media resources encapsulated away.
 */
public class DefaultBeeperService implements BeeperService {

    /**
     * Context used to resolve system audio services without leaking Activity context.
     */
    private final Context context;

    /**
     * Holds an active reference to the looping alarm sound.
     */
    private Ringtone alarmRingtone;

    /**
     * Constructs the audio service.
     */
    public DefaultBeeperService(final Context context) {
        // Enforce application context to prevent retaining short-lived Activity references
        this.context = context.getApplicationContext();
    }

    /**
     * Plays a single short notification beep.
     */
    @Override
    public void playBeep() {
        try {
            // Retrieve default system notification sound URI
            final Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone r = RingtoneManager.getRingtone(context, notification);

            // Play sound asynchronously if system returned a valid ringtone instance
            if (r != null) {
                r.play();
            }
        } catch (Exception e) {
            // Catch media/permission exceptions gracefully so audio failure never crashes the application
            e.printStackTrace();
        }
    }

    /**
     * Starts playing a continuous alarm sound.
     * If the default system alarm is unavailable, falls back to the default notification.
     */
    @Override
    public void startAlarm() {
        try {
            // Avoid restarting if alarm is already playing
            if (alarmRingtone == null || !alarmRingtone.isPlaying()) {

                // Attempt to fetch default alarm; fallback if alarm is unset on device
                final Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                final Uri uriToPlay = (alarmUri != null) ? alarmUri : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                // Instantiate ringtone and store reference for termination later
                alarmRingtone = RingtoneManager.getRingtone(context, uriToPlay);
                if (alarmRingtone != null) {
                    alarmRingtone.play();
                }
            }
        } catch (Exception e) {
            // Log issues without breaking state
            e.printStackTrace();
        }
    }

    /**
     * Stops the active continuous alarm sound if currently playing.
     */
    @Override
    public void stopAlarm() {
        // Check state before stopping
        if (alarmRingtone != null && alarmRingtone.isPlaying()) {
            alarmRingtone.stop();
        }
    }
}
