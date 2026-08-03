package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import org.junit.After;
import org.junit.Before;

import edu.luc.etl.cs313.android.simplestopwatch.model.state.DefaultTimerStateMachine;

/**
 * Concrete testcase subclass for the default timer state machine
 * implementation.
 *
 * @author laufer
 * @see http://xunitpatterns.com/Testcase%20Superclass.html
 */
public class DefaultTimerStateMachineTest extends AbstractTimerStateMachineTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setModel(new DefaultTimerStateMachine(getDependency(), getDependency(), getTimeoutDependency()));
    }

    @After
    public void tearDown() {
        setModel(null);
        super.tearDown();
    }
}