package edu.luc.etl.cs313.android.simplestopwatch.model.time;

import edu.luc.etl.cs313.android.simplestopwatch.common.Startable;
import edu.luc.etl.cs313.android.simplestopwatch.common.Stoppable;

/**
 * Timeout model used to manage a single-shot timeout (used by Setting state).
 * Implements startable and stoppable.
 * Expose a way to register the callback invoked when the timeout expires.
 */
public interface TimeoutModel extends Startable, Stoppable {

    // Register a callback to be invoked when the timeout elapses.
    void setOnTimeout(Runnable onTimeout);

    // Returns true if timeout countdown is currently running.
    boolean isRunning();
}
