package edu.luc.etl.cs313.android.simplestopwatch.common;

/**
 * A listener for stopwatch events coming from the UI.
 *
 * @author laufer
 */
// TODO (Ashley):
// Replace the two stopwatch button callbacks with a single timer button
// callback after TimerModelFacade integration is complete.
public interface StopwatchUIListener {
    void onStartStop();
    void onLapReset();
}
