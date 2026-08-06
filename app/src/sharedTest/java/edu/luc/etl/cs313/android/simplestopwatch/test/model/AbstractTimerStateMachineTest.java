package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.TickListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.state.TimerStateMachine;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeoutModel;

/**
 * Testcase superclass for the timer state machine model. Uses two mock objects for
 * the model's dependencies.
 *
 * @author laufer
 * */
 //@see/http://xunitpatterns.com/Testcase%20Superclass.html
public abstract class AbstractTimerStateMachineTest {

    private TimerStateMachine model;

    private UnifiedMockDependency dependency;

    private FakeTimeoutModel timeoutDependency;

    @Before
    public void setUp() throws Exception {
        dependency = new UnifiedMockDependency();
        timeoutDependency = new FakeTimeoutModel();
    }

    @After
    public void tearDown() {
        dependency = null;
        timeoutDependency = null;
    }

    /**
     * Setter for dependency injection. Usually invoked by concrete testcase
     * subclass.
     *
     * @param model
     */
    protected void setModel(final TimerStateMachine model) {
        this.model = model;
        if (model == null)
            return;
        this.model.setModelListener(dependency);
        this.model.actionInit();
    }

    protected UnifiedMockDependency getDependency() {
        return dependency;
    }

    protected FakeTimeoutModel getTimeoutDependency() {
        return timeoutDependency;
    }

    /**
     * Verifies that we're initially in the stopped state (and told the listener
     * about it).
     */
    @Test
    public void testPreconditions() {
        assertEquals(R.string.STOPPED, dependency.getState());
    }

    /**
     * Verifies that repeated clicks while setting accumulate the time and stay
     * in SettingState, and that the inactivity timeout is running.
     */
    @Test
    public void testScenarioSetting() {
        model.onStartStop();
        assertEquals(R.string.SETTING, dependency.getState());
        assertTrue(timeoutDependency.isRunning());
        model.onStartStop();
        model.onStartStop();
        assertTimeEquals(3);
        assertEquals(R.string.SETTING, dependency.getState());
    }
    @Test
    public void testSettingMaxStartsImmediately() {
        for (int i = 0; i < 99; i++) {
            model.onStartStop();
        }
        assertEquals(R.string.RUNNING, dependency.getState());
        assertTrue(dependency.isStarted());
        assertTimeEquals(99);
    }
    @Test
    public void testTimeNeverExceeds99() {
        for (int i = 0; i < 99; i++) {
            model.onStartStop();
        }
        assertTimeEquals(99);
    }
    @Test
    public void testSingleTickDecrementsByOne() {
        model.onStartStop();
        model.onStartStop();
        model.onStartStop();

        model.onTimeout();

        assertTimeEquals(3);

        model.onTick();
        assertTimeEquals(2);

        model.onTick();
        assertTimeEquals(1);
    }

    /**
     * Verifies that the inactivity timeout moves from SettingState to
     * RunningState and starts the clock.
     */
    @Test
    public void testScenarioSettingTimesOutToRunning() {
        model.onStartStop();
        model.onTimeout();
        assertEquals(R.string.RUNNING, dependency.getState());
        assertTrue(dependency.isStarted());
    }

    /**
     * Verifies the following scenario: set time to 5, run it down, expect
     * time 0 and AlarmingState.
     */
    @Test
    public void testScenarioRun() {
        assertTimeEquals(0);
        assertFalse(dependency.isStarted());
        model.onStartStop(); //SETTING with time = 1
        model.onStartStop();
        model.onStartStop();
        model.onStartStop();
        model.onStartStop(); // time = 5
        model.onTimeout();   // RUNNING
        assertTrue(dependency.isStarted());
        onTickRepeat(5);
        assertTimeEquals(0);
        assertEquals(R.string.ALARMING, dependency.getState());
    }

    /**
     * Verifies the following scenario: set time, start running, cancel
     * partway through, expect time 0 and StoppedState.
     */
    @Test
    public void testScenarioRunReset() {
        model.onStartStop(); //SETTING, with time = 1
        model.onStartStop(); // time = 2
        model.onTimeout();   //RUNNING
        assertTrue(dependency.isStarted());
        model.onTick();
        assertTimeEquals(1);
        model.onStartStop(); // cancelled
        assertEquals(R.string.STOPPED, dependency.getState());
        assertFalse(dependency.isStarted());
        assertTimeEquals(0);
    }

    /**
     * Verifies that a click while alarming silences the alarm and returns to
     * StoppedState.
     */
    @Test
    public void testScenarioAlarmingToStopped() {
        model.onStartStop(); //SETTING, time = 1
        model.onTimeout();   // RUNNING
        model.onTick();      // time = 0, ALARMING
        assertEquals(R.string.ALARMING, dependency.getState());
        model.onStartStop(); // silenced
        assertEquals(R.string.STOPPED, dependency.getState());
        assertTimeEquals(0);
    }

    /**
     * Sends the given number of tick events to the model.
     *
     *  @param n the number of tick events
     */
    protected void onTickRepeat(final int n) {
        for (var i = 0; i < n; i++)
            model.onTick();
    }

    /**
     * Checks whether the model has invoked the expected time-keeping
     * methods on the mock object.
     */
    protected void assertTimeEquals(final int t) {
        assertEquals(t, dependency.getTime());
    }
}

/**
 * Manually implemented mock object that unifies the TimeModel, ClockModel, and
 * StopwatchModelListener dependencies of the timer state machine model.
 *
 * @author laufer
 */
class UnifiedMockDependency implements TimeModel, ClockModel, StopwatchModelListener {

    private int timeValue = -1, stateId = -1;

    private int runningTime = 0;

    private boolean started = false;

    public int getState() {
        return stateId;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void onTimeUpdate(final int timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public void onStateUpdate(final int stateId) {
        this.stateId = stateId;
    }

    private TickListener tickListener;

    @Override
    public void setTickListener(final TickListener listener) {
        tickListener = listener;
    }

    public void fireTick() {
        if (tickListener != null) {
            tickListener.onTick();
        }
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public void resetTime() {
        runningTime = 0;
    }

    @Override
    public void incTime() {
        if (runningTime < 99) {
            runningTime++;
        }
    }

    @Override
    public void decTime() {
        if (runningTime > 0) {
            runningTime--;
        }
    }

    @Override
    public int getTime() {
        return runningTime;
    }

    @Override
    public void setTime(int time) {
        if (time >= 0 && time <= 99) {
            runningTime = time;
        }
    }

    @Override
    public boolean isZero() {
        return runningTime == 0;
    }

    @Override
    public boolean isMax() {
        return runningTime == 99;
    }
}

/**
 * Manually implemented mock object for the TimeoutModel dependency of the
 * timer state machine model.
 *
 * @author laufer
 */
class FakeTimeoutModel implements TimeoutModel {

    private boolean running = false;

    private Runnable onTimeout;

    @Override
    public void setOnTimeout(final Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    void fireTimeout() {
        if (onTimeout != null) {
            onTimeout.run();
        }
    }
}