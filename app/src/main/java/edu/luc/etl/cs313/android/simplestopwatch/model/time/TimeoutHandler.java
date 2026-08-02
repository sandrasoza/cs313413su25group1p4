package edu.luc.etl.cs313.android.simplestopwatch.model.time;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A single-shot timeout handler used by the "setting" state.
 * It starts a one-off timer (default 3 seconds)
 * Invokes the provided Runnable when the timeout elapses.
 * The handler implements TimeoutModel so it can be used by the state machine.
 */
public class TimeoutHandler implements TimeoutModel {

    // Default timeout used by the Setting state (3 seconds).
    public static final long DEFAULT_TIMEOUT_MS = 3000L;

    private Timer timer;
    private final long timeoutMs;
    private Runnable onTimeout;
    private boolean running = false;


    // Create a TimeoutHandler with the default 3s timeout.
    // The onTimeout must be provided via setOnTimeout(...) before start() is called.

    public TimeoutHandler() {
        this(DEFAULT_TIMEOUT_MS, null);
    }

    // Create a TimeoutHandler with the default 3s timeout and a callback.
    // @param onTimeout the callback invoked when the timeout expires (it maybe null until set)
    public TimeoutHandler(final Runnable onTimeout) {
        this(DEFAULT_TIMEOUT_MS, onTimeout);
    }

     // Create a TimeoutHandler with a custom timeout and optional callback.
     // @param timeoutMs timeout in milliseconds
     // @param onTimeout callback invoked when the timeout expires (it may be null until set)

    public TimeoutHandler(final long timeoutMs, final Runnable onTimeout) {
        this.timeoutMs = timeoutMs;
        this.onTimeout = onTimeout;
    }

    @Override
    public synchronized void setOnTimeout(final Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    @Override
    public synchronized void start() {
        // Prevent starting if already running
        if (running) {
            return; // idempotent
        }

        if (onTimeout == null) {
            throw new IllegalStateException("onTimeout must be set before starting the timeout");
        }

        running = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // clear running state and timer before invoking the callback
                synchronized (TimeoutHandler.this) {
                    running = false;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
                // invoke callback outside synchronized block
                Runnable cb;
                synchronized (TimeoutHandler.this) {
                    cb = onTimeout;
                }
                if (cb != null) {
                    cb.run();
                }
            }
        }, timeoutMs);
    }

    @Override
    public synchronized void stop() {
        // Do nothing if the countdown is not running
        if (!running) {
            return;
        }
        running = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return running;
    }
}
