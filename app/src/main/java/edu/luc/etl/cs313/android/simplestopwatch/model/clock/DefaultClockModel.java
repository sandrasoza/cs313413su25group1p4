package edu.luc.etl.cs313.android.simplestopwatch.model.clock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An implementation of the internal clock.
 * Coordinates timing ticks with TimeModel and handles execution state.
 *
 * @author laufer
 */
public class DefaultClockModel implements ClockModel {

    private Timer timer;
    private TickListener listener;
    private boolean isRunning = false;

    @Override
    public void setTickListener(final TickListener listener) {
        this.listener = listener;
    }

    @Override
    public void start() {
        // Prevent starting if already running
        if (isRunning) {
            return;
        }

        // Only start if listener is set
        if (listener == null) {
            throw new IllegalStateException("TickListener must be set before starting the clock");
        }

        isRunning = true;
        timer = new Timer();

        // The clock model runs onTick every 1000 milliseconds
        timer.schedule(new TimerTask() {
            @Override public void run() {
                // fire event to coordinate timing ticks with TimeModel
                listener.onTick();
            }
        }, /*initial delay*/ 1000, /*periodic delay*/ 1000);
    }

    @Override
    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}