package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;
class RunningState implements StopwatchState {

    public RunningState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;

    @Override
    public void onStartStop() {
        // button acts as a cancel button
        sm.actionStop();
        sm.actionReset();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        onStartStop();
    }

    @Override
    public void onTick() {
        sm.actionDec();
        if (sm.isTimeZero()) {
            sm.actionStop();
            sm.actionAlarmOn();
            sm.toAlarmingState();
        } else {
            sm.toRunningState();
        }
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
        return R.string.RUNNING;
    }
}