package com.cienet.equityposition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.cienet.equityposition.entity.SecurityCode;
import com.cienet.equityposition.entity.Trade;
import com.cienet.equityposition.entity.TradeAction;

/**
 * Better to mock some of these objects
 */
public class TransactionEngineTest {

    TransactionEngine engine;
    Trade trade;

    @Before
    public void before() {
        engine = new TransactionEngine();
        trade = new Trade(1, 1, SecurityCode.INF.name(), 0.0, TradeAction.INSERT, true);
    }

    @After
    public void after() {

    }

    @Test
    public void testStart() {
        // test
        boolean status = engine.start(new TradeManager());
        // verify
        assertTrue("Should start the engine ok", status);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartNoManager() {
        // test
        engine.start(null);
        // verify
        fail("Should not reach here with a null TradeManager");
    }

    @Test
    public void testStartThenStartAgain() {
        // test
        engine.start(new TradeManager());
        boolean status = engine.start(new TradeManager());
        // verify
        assertFalse("Should not start the engine twice", status);
    }

    @Test
    public void testStopBeforeStart() {
        // test
        boolean status = engine.stop();
        // verify
        assertFalse("Should not stop the engine before start", status);
    }

    @Test
    public void testStopAfterStart() {
        // test
        engine.start(new TradeManager());
        boolean status = engine.stop();
        // verify
        assertTrue("Should stop the engine ok", status);
    }

    @Test
    public void testAcceptNotStarted() {
        // test
        boolean status = engine.acceptTransaction(1, trade);
        // verify
        assertFalse("Can't accept a transaction if not started", status);
    }

    @Test
    public void testAcceptStarted() {
        // test
        engine.start(new TradeManager());
        boolean status = engine.acceptTransaction(1, trade);
        // verify
        assertTrue("Can accept a transaction after starting", status);
    }
}
