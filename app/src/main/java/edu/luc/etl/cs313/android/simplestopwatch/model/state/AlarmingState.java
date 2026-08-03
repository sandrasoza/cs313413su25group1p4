package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

/**
 * Indicates when the timer has reached zero on its own and the alarm is sounding
 * non-stop. User will need to click in order to silence the alarm and return to StoppedState,
 * which is already zero.
 */
class AlarmingState implements StopwatchState {

    public AlarmingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;

    @Override
    public void onStartStop() {
        // button acts as a stop button, silencing the alarm
        sm.actionAlarmOff();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        onStartStop();
    }

    @Override
    public void onTick() {
        throw new UnsupportedOperationException("onTick");
    }

    @Override
    public void onTimeout() {
        throw new UnsupportedOperationException("onTimeout");
    }

    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.ALARMING;
    }
}