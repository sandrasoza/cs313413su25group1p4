package edu.luc.etl.cs313.android.simplestopwatch.model;

import android.content.Context;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.BeeperService;
import edu.luc.etl.cs313.android.simplestopwatch.common.DefaultBeeperService;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.DefaultClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.state.DefaultTimerStateMachine;
import edu.luc.etl.cs313.android.simplestopwatch.model.state.TimerStateMachine;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.DefaultTimeModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeoutHandler;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeoutModel;

/**
 * An implementation of the model facade.
 *
 * @author laufer
 */
public class ConcreteTimerModelFacade implements TimerModelFacade {

    private final TimerStateMachine stateMachine;

    private final ClockModel clockModel;

    private final TimeModel timeModel;

    private final TimeoutModel timeoutModel;

    private final BeeperService beeperService;

    /**
     * Production constructor. Requires a Context so DefaultBeeperService can resolve
     * system ringtone/alarm resources.
     */
    public ConcreteTimerModelFacade(final Context context) {
        timeModel = new DefaultTimeModel();
        clockModel = new DefaultClockModel();
        timeoutModel = new TimeoutHandler();
        beeperService = new DefaultBeeperService(context);
        stateMachine = new DefaultTimerStateMachine(timeModel, clockModel, timeoutModel);
        clockModel.setTickListener(stateMachine);
    }

    /**
     * Package-private constructor for test injection (see TimerModelFacadeTest).
     * Lets tests supply mocks for every collaborator instead of standing up a real
     * Context/Ringtone.
     */
    public ConcreteTimerModelFacade(final TimeModel timeModel, final ClockModel clockModel,
                             final TimeoutModel timeoutModel, final BeeperService beeperService,
                             final TimerStateMachine stateMachine) {
        this.timeModel = timeModel;
        this.clockModel = clockModel;
        this.timeoutModel = timeoutModel;
        this.beeperService = beeperService;
        this.stateMachine = stateMachine;
        this.clockModel.setTickListener(stateMachine);
    }

    @Override
    public void start() {
        stateMachine.actionInit();
    }

    @Override
    public void setModelListener(final StopwatchModelListener listener) {
        // wrap the real listener so we can react to state transitions and drive the
        // beeper, without the state machine itself needing to know about BeeperService
        stateMachine.setModelListener(new BeeperDrivingListener(listener, beeperService));
    }

    @Override
    public void onStartStop() {
        stateMachine.onStartStop();
    }

    @Override
    public void onLapReset() {
        stateMachine.onLapReset();
    }

    @Override
    public void startTicks() {
        clockModel.start();
    }

    @Override
    public void stopTicks() {
        clockModel.stop();
        // Ensure alarm audio never keeps playing/leaking past teardown
        beeperService.stopAlarm();
    }

    /**
     * Wraps the real StopwatchModelListener so every onStateUpdate() notification can
     * also drive the BeeperService. Tracks the previous state id because RunningState's
     * onTick() calls toRunningState() (and thus fires onStateUpdate(RUNNING)) on every
     * tick, not just on entry -- without this check we'd beep once per second instead
     * of once when RUNNING is first entered.
     */
    private static class BeeperDrivingListener implements StopwatchModelListener {

        private final StopwatchModelListener delegate;
        private final BeeperService beeperService;

        // no state has this id, so the first real onStateUpdate() always counts as a transition
        private int lastStateId = -1;

        BeeperDrivingListener(final StopwatchModelListener delegate, final BeeperService beeperService) {
            this.delegate = delegate;
            this.beeperService = beeperService;
        }

        @Override
        public void onTimeUpdate(final int timeValue) {
            delegate.onTimeUpdate(timeValue);
        }

        @Override
        public void onStateUpdate(final int stateId) {
            if (stateId != lastStateId) {
                if (stateId == R.string.RUNNING) {
                    beeperService.playBeep();
                } else if (stateId == R.string.ALARMING) {
                    beeperService.startAlarm();
                } else {
                    // entering STOPPED or SETTING: make sure no alarm is left playing
                    beeperService.stopAlarm();
                }
                lastStateId = stateId;
            }
            delegate.onStateUpdate(stateId);
        }
    }
}