package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;

/**
 * Testcase superclass for the time model abstraction.
 * This is a simple unit test of an object without dependencies.
 *
 * @author laufer
 */
 //@see http://xunitpatterns.com/Testcase%20Superclass.html
public abstract class AbstractTimeModelTest {

    private TimeModel model;

    /**
     * Setter for dependency injection. Usually invoked by concrete testcase
     * subclass.
     *
     * @param model
     */
    protected void setModel(final TimeModel model) {
        this.model = model;
    }

    /**
     * Verifies that time is initially 0.
     */
    @Test
    public void testPreconditions() {
        assertEquals(0, model.getTime());
    }

    /**
     * Verifies that time is incremented correctly.
     */
    @Test
    public void testIncTimeOne() {
        final var time = model.getTime();
        model.incTime();
        assertEquals(time + 1, model.getTime());
    }

    /**
     * Verifies that time respects upper bound.
     */
    @Test
    public void testIncTimeAtMax() {
        model.setTime(99);
        model.incTime();
        assertEquals(99, model.getTime());
    }

    /**
     * Verifies that time is decremented correctly.
     */
    @Test
    public void testDecTimeOne() {
        model.setTime(5);
        model.decTime();
        assertEquals(4, model.getTime());
    }

    /**
     * Verifies that time respects lower bound.
     */
    @Test
    public void testDecTimeAtMin() {
        model.setTime(0);
        model.decTime();
        assertEquals(0, model.getTime());
    }

    /**
     * Verifies that time can be set within bounds.
     */
    @Test
    public void testSetTimeValid() {
        model.setTime(50);
        assertEquals(50, model.getTime());
    }

    /**
     * Verifies that time is bounded on set.
     */
    @Test
    public void testSetTimeOutOfBounds() {
        model.setTime(100);
        assertEquals(0, model.getTime());
        model.setTime(-1);
        assertEquals(0, model.getTime());
    }

    /**
     * Verifies that reset() sets time back to 0.
     */
    @Test
    public void testResetTime() {
        model.setTime(50);
        model.resetTime();
        assertEquals(0, model.getTime());
    }

    /**
     * Verifies that isZero() returns true when time is 0.
     */
    @Test
    public void testIsZeroTrue() {
        model.setTime(0);
        assertTrue(model.isZero());
    }

    /**
     * Verifies that isZero() returns false when time is not 0.
     */
    @Test
    public void testIsZeroFalse() {
        model.setTime(50);
        assertFalse(model.isZero());
    }

    /**
     * Verifies that isMax() returns true when time is 99.
     */
    @Test
    public void testIsMaxTrue() {
        model.setTime(99);
        assertTrue(model.isMax());
    }

    /**
     * Verifies that isMax() returns false when time is not 99.
     */
    @Test
    public void testIsMaxFalse() {
        model.setTime(50);
        assertFalse(model.isMax());
    }
}

