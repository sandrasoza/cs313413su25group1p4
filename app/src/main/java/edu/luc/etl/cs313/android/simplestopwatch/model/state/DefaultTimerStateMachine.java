package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeoutModel;

/**
 * An implementation of the state machine for the timer.
 * Extended for Project 4 to also depend on a TimeoutModel, used by
 * SettingState to detect 3 seconds of inactivity.
 * @author laufer
 */
public class DefaultTimerStateMachine implements TimerStateMachine {

    public DefaultTimerStateMachine(final TimeModel timeModel, final ClockModel clockModel,
                                    final TimeoutModel timeoutModel) {
        this.timeModel = timeModel;
        this.clockModel = clockModel;
        this.timeoutModel = timeoutModel;
        this.timeoutModel.setOnTimeout(this::onTimeout);
    }

    private final TimeModel timeModel;

    private final ClockModel clockModel;

    private final TimeoutModel timeoutModel;

    /**
     * The internal state of this adapter component. Required for the State pattern.
     */
    private TimerState state;

    protected void setState(final TimerState state) {
        this.state = state;
        listener.onStateUpdate(state.getId());
    }

    private StopwatchModelListener listener;

    @Override
    public void setModelListener(final StopwatchModelListener listener) {
        this.listener = listener;
    }

    // forward event uiUpdateListener methods to the current state
    // these must be synchronized because events can come from the
    // UI thread or a timer thread (clock tick or inactivity timeout)
    @Override public synchronized void onStartStop() { state.onStartStop(); }
    @Override public synchronized void onLapReset()  { state.onLapReset(); }
    @Override public synchronized void onTick()      { state.onTick(); }
    @Override public synchronized void onTimeout()   { state.onTimeout(); }

    @Override public void updateUIRuntime() { listener.onTimeUpdate(timeModel.getTime()); }

    // known states
    private final TimerState STOPPED  = new StoppedState(this);
    private final TimerState SETTING  = new SettingState(this);
    private final TimerState RUNNING  = new RunningState(this);
    private final TimerState ALARMING = new AlarmingState(this);

    // transitions
    @Override public void toStoppedState()  { setState(STOPPED); }
    @Override public void toSettingState()  { setState(SETTING); }
    @Override public void toRunningState()  { setState(RUNNING); }
    @Override public void toAlarmingState() { setState(ALARMING); }

    // actions
    @Override public void actionInit()  { toStoppedState(); actionReset(); }
    @Override public void actionReset() { timeModel.resetTime(); actionUpdateView(); }
    @Override public void actionInc()   { timeModel.incTime(); actionUpdateView(); }
    @Override public void actionDec()   { timeModel.decTime(); actionUpdateView(); }

    @Override public void actionStart() { clockModel.start(); }
    @Override public void actionStop()  { clockModel.stop(); }

    @Override public void actionStartTimeout() { timeoutModel.start(); }
    @Override public void actionStopTimeout()  { timeoutModel.stop(); }

    //wire to a real alarm/sound mechanism
    @Override public void actionAlarmOn()  { }
    @Override public void actionAlarmOff() { }

    @Override public void actionUpdateView() { state.updateView(); }

    // guards
    @Override public boolean isTimeZero() { return timeModel.isZero(); }
}