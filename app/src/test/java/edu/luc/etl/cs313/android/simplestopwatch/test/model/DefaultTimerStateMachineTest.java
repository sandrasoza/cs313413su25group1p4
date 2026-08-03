package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import org.junit.After;
import org.junit.Before;

import edu.luc.etl.cs313.android.simplestopwatch.model.state.DefaultTimerStateMachine;

/**
 * Concrete testcase subclass for the default stopwatch state machine
 * implementation.
 *
 * @author laufer
 * @see http://xunitpatterns.com/Testcase%20Superclass.html
 */
public class DefaultTimerStateMachineTest extends AbstractStopwatchStateMachineTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setModel(new DefaultTimerStateMachine(getDependency(), getDependency()));
    }

    @After
    public void tearDown() {
        setModel(null);
        super.tearDown();
    }
}