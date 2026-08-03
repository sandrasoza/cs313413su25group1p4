package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

/**
 * Allows the user to set the desired time. Each click increments the time
 * (limited at 99) and restarts the 3-second inactivity timeout. If 3
 * seconds pass without a click, the timer beeps once and starts running.
 */
class SettingState implements StopwatchState {

    public SettingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;

    @Override
    public void onStartStop() {
        sm.actionStopTimeout();
        sm.actionInc();
        sm.actionStartTimeout();
        sm.toSettingState();
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
        // 3 seconds of inactivity: beep once (include sound)
        sm.actionStart();
        sm.toRunningState();
    }

    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.SETTING;
    }
}