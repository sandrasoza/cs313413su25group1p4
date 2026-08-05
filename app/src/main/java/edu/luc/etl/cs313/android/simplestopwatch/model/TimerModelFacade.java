package edu.luc.etl.cs313.android.simplestopwatch.model;

import edu.luc.etl.cs313.android.simplestopwatch.common.Startable;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelSource;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchUIListener;

/**
 * A thin model facade. Following the Facade pattern,
 * this isolates the complexity of the model from its clients (usually the adapter).
 *
 * @author laufer
 */
public interface TimerModelFacade extends Startable, StopwatchUIListener, StopwatchModelSource {
    /**
     * Starts tick engine on startup.
     */
    void startTicks();

    /**
     * Stops tick engine on teardown.
     */
    void stopTicks();
}