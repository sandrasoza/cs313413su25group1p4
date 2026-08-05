package edu.luc.etl.cs313.android.simplestopwatch.common;

/** Implement methods to play a single short beep (when entering RUNNING) and continuous beeping (for ALARMING). */
public interface BeeperService {
    /** Plays a single short beep notification. */
    void playBeep();

    /** Starts playing a continuous alarm sound. */
    void startAlarm();

    /** Stops the continuous alarm sound. */
    void stopAlarm();
}