package edu.luc.etl.cs313.android.simplestopwatch.model.state;

/**
 * The restricted view states have of their surrounding state machine.
 * This is a client-specific interface in Peter Coad's terminology.
 *
 * @author laufer
 */
interface StopwatchSMStateView {


    // transitions
    void toStoppedState();
    void toSettingState();
    void toRunningState();
    void toAlarmingState();

    // actions
    void actionInit();
    void actionReset();
    void actionInc();
    void actionDec();
    /**
     * Starts the one-second countdown clock.
     */
    void actionStart();
    /**
     * Stops the one-second countdown clock.
     */
    void actionStop();
    void actionStartTimeout(); // (re)starts the 3-second inactivity timeout
    void actionStopTimeout();  // cancels the 3-second inactivity timeout
    void actionAlarmOn();      // TODO sound/vibration mechanism
    void actionAlarmOff();     // TODO sound/vibration mechanism
    void actionUpdateView();

    /**
     * Returns true if the remaining countdown time is zero.
     *
     * @return true when the timer has reached zero
     */
    boolean isTimeZero();

    // state-dependent UI updates
    void updateUIRuntime();
}
