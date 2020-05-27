package com.cienet.equityposition;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.cienet.equityposition.entity.SecurityCode;
import com.cienet.equityposition.entity.Trade;
import com.cienet.equityposition.entity.TradeAction;

/**
 * Better to mock some of these objects like the Trade
 */
public class TradeManagerTest {

    TradeManager tradeManager;
    Trade insert;
    Trade update;
    Trade cancel;
    Trade bad;
    String secCode = SecurityCode.REL.name();

    @Before
    public void before() {
        tradeManager = new TradeManager();
        insert = new Trade(1, 1, secCode, 1.0, TradeAction.INSERT, true);
        update = new Trade(1, 2, secCode, 2.0, TradeAction.UPDATE, true);
        cancel = new Trade(1, 3, secCode, 3.0, TradeAction.CANCEL, false);
        bad = new Trade(1, 4, "WWF", 4.0, TradeAction.INSERT, false);
    }

    @After
    public void after() {

    }

    @Test
    public void testHandleGetEmptyPosition() {
        // test
        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Should be this empty position", output == 0.0);
    }

    @Test
    public void testHandleInsert() {
        // test
        tradeManager.handleTrade(insert);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins " + output, output == 1.0);
    }

    @Test
    public void testHandleUpdate() {
        // test
        tradeManager.handleTrade(update);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins " + output, output == 2.0);
    }

    @Test
    public void testHandleCancel() {
        // test
        tradeManager.handleTrade(cancel);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins " + output, output == 0.0);
    }

    @Test
    public void testHandleInsertThenUpdate() {
        // test
        tradeManager.handleTrade(insert);
        tradeManager.handleTrade(update);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins->up " + output, output == 2.0);
    }

    @Test
    public void testHandleInsertThenCancel() {
        // test
        tradeManager.handleTrade(insert);
        tradeManager.handleTrade(cancel);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins->can " + output, output == 0.0);
    }

    @Test
    public void testHandleUpdateThenInsert() {
        // test
        tradeManager.handleTrade(update);
        tradeManager.handleTrade(insert);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after up->ins", output == 2.0);
    }

    @Test
    public void testHandleCancelThenInsert() {
        // test
        tradeManager.handleTrade(cancel);
        tradeManager.handleTrade(insert);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after can->ins", output == 0.0);
    }

    @Test
    public void testHandleInsertThenUpdateThenCancel() {
        // test
        tradeManager.handleTrade(insert);
        tradeManager.handleTrade(update);
        tradeManager.handleTrade(cancel);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins->up->can " + output, output == 0.0);
    }

    @Test
    public void testHandleInsertThenCancelThenUpdate() {
        // test
        tradeManager.handleTrade(insert);
        tradeManager.handleTrade(update);
        tradeManager.handleTrade(cancel);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after ins->can->up " + output, output == 0.0);
    }

    @Test
    public void testHandleUpdateThenInsertThenCancel() {
        // test
        tradeManager.handleTrade(update);
        tradeManager.handleTrade(insert);
        tradeManager.handleTrade(cancel);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after up->ins->can " + output, output == 0.0);
    }

    @Test
    public void testHandleUpdateThenCancelThenInsert() {
        // test
        tradeManager.handleTrade(update);
        tradeManager.handleTrade(cancel);
        tradeManager.handleTrade(insert);

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Was this position after up->can->ins " + output, output == 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleIllegalSecCode() {
        // test
        tradeManager.handleTrade(bad);
        // verify
        fail("Should have hit an exception for the bad SecCode");
    }

    @Test(timeout = 20000)
    public void testHandleUpdateThenCancelThenInsertMultithreaded() throws InterruptedException {
        // setup
        List<ThreadedTrade> threads = new ArrayList<>();
        threads.add(new ThreadedTrade(insert));
        threads.add(new ThreadedTrade(update));
        threads.add(new ThreadedTrade(cancel));
        Collections.shuffle(threads);

        // test
        for (ThreadedTrade threadTrade : threads) {
            new Thread(threadTrade);
        }

        // verify
        double output = tradeManager.getPosition(secCode);
        assertTrue("Thread test had this output " + output, output == 0.0);
    }

    class ThreadedTrade implements Runnable {
        Trade trade;

        public ThreadedTrade(Trade input) {
            this.trade = input;
        }

        @Override
        public void run() {
            tradeManager.handleTrade(trade);
        }
    }
}
