package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.BeeperService;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.ConcreteTimerModelFacade;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.state.TimerStateMachine;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeoutModel;

/**
 * Unit tests for ConcreteTimerModelFacade.
 *
 * Verifies that the facade correctly delegates to its collaborators (state machine,
 * clock model, beeper service), and that the listener it installs on the state machine
 * correctly drives the BeeperService off of state transitions. Behavioral logic of the
 * collaborators themselves (countdown bounds, transitions, timeout) is covered separately
 * by DefaultTimeModelTest, DefaultClockModelTest, and DefaultTimerStateMachineTest.
 */
public class TimerModelFacadeTest {

    private TimeModel timeModel;
    private ClockModel clockModel;
    private TimeoutModel timeoutModel;
    private BeeperService beeperService;
    private TimerStateMachine stateMachine;

    private ConcreteTimerModelFacade facade;

    @Before
    public void setUp() {
        timeModel = mock(TimeModel.class);
        clockModel = mock(ClockModel.class);
        timeoutModel = mock(TimeoutModel.class);
        beeperService = mock(BeeperService.class);
        stateMachine = mock(TimerStateMachine.class);

        facade = new ConcreteTimerModelFacade(timeModel, clockModel, timeoutModel, beeperService, stateMachine);
    }

    @Test
    public void constructorWiresClockTickListenerToStateMachine() {
        verify(clockModel).setTickListener(stateMachine);
    }

    @Test
    public void startDelegatesToStateMachineInit() {
        facade.start();
        verify(stateMachine).actionInit();
    }

    @Test
    public void onStartStopDelegatesToStateMachine() {
        facade.onStartStop();
        verify(stateMachine).onStartStop();
    }

    @Test
    public void onLapResetDelegatesToStateMachine() {
        facade.onLapReset();
        verify(stateMachine).onLapReset();
    }

    @Test
    public void startTicksStartsTheClock() {
        facade.startTicks();
        verify(clockModel).start();
        verifyNoMoreInteractions(beeperService);
    }

    @Test
    public void stopTicksStopsTheClockAndSilencesAnyAlarm() {
        facade.stopTicks();
        verify(clockModel).stop();
        verify(beeperService).stopAlarm();
    }

    // -- setModelListener wrapping behavior --
    // the facade installs a wrapper (BeeperDrivingListener) on the state machine rather
    // than the caller's listener directly, so these tests capture that wrapper and drive
    // it directly to verify both forwarding and beeper-triggering behavior.

    private StopwatchModelListener captureInstalledListener(final StopwatchModelListener realListener) {
        facade.setModelListener(realListener);
        final ArgumentCaptor<StopwatchModelListener> captor = ArgumentCaptor.forClass(StopwatchModelListener.class);
        verify(stateMachine).setModelListener(captor.capture());
        return captor.getValue();
    }

    @Test
    public void onTimeUpdateIsForwardedUntouched() {
        final StopwatchModelListener realListener = mock(StopwatchModelListener.class);
        final StopwatchModelListener installed = captureInstalledListener(realListener);

        installed.onTimeUpdate(42);

        verify(realListener).onTimeUpdate(42);
        verifyNoMoreInteractions(beeperService);
    }

    @Test
    public void onStateUpdateIsForwardedToTheRealListener() {
        final StopwatchModelListener realListener = mock(StopwatchModelListener.class);
        final StopwatchModelListener installed = captureInstalledListener(realListener);

        installed.onStateUpdate(R.string.RUNNING);

        verify(realListener).onStateUpdate(R.string.RUNNING);
    }

    @Test
    public void enteringRunningPlaysASingleBeep() {
        final StopwatchModelListener installed = captureInstalledListener(mock(StopwatchModelListener.class));

        installed.onStateUpdate(R.string.RUNNING);

        verify(beeperService).playBeep();
        verify(beeperService, never()).startAlarm();
    }

    @Test
    public void repeatedRunningNotificationsDoNotRepeatTheBeep() {
        // RunningState.onTick() calls toRunningState() every tick while counting down,
        // so onStateUpdate(RUNNING) fires repeatedly -- this must only beep once, on
        // the actual transition into RUNNING, not on every tick.
        final StopwatchModelListener installed = captureInstalledListener(mock(StopwatchModelListener.class));

        installed.onStateUpdate(R.string.RUNNING);
        installed.onStateUpdate(R.string.RUNNING);
        installed.onStateUpdate(R.string.RUNNING);

        verify(beeperService, times(1)).playBeep();
    }

    @Test
    public void enteringAlarmingStartsTheContinuousAlarm() {
        final StopwatchModelListener installed = captureInstalledListener(mock(StopwatchModelListener.class));

        installed.onStateUpdate(R.string.ALARMING);

        verify(beeperService).startAlarm();
    }

    @Test
    public void leavingAlarmingSilencesTheAlarm() {
        final StopwatchModelListener installed = captureInstalledListener(mock(StopwatchModelListener.class));

        installed.onStateUpdate(R.string.ALARMING);
        installed.onStateUpdate(R.string.STOPPED);

        verify(beeperService).stopAlarm();
    }
}