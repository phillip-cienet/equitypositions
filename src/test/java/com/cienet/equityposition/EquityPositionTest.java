package com.cienet.equityposition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.cienet.equityposition.entity.Trade;
import com.cienet.equityposition.entity.TradeAction;

/**
 * TODO: to mock some of these objects<br/>
 * TODO: automate the iteration of this test, do 100+ times or more?
 */
public class EquityPositionTest {

    TransactionEngine engine;
    TradeManager manager = new TradeManager();
    Trade trans1, trans2, trans3, trans4, trans5, trans6;
    List<Trade> trades;

    String rel = "REL";
    String itc = "ITC";
    String inf = "INF";

    double wantedREL = 60.0;
    double wantedITC = 0.0;
    double wantedINF = 50.0;

    @Before
    public void before() {
        engine = new TransactionEngine();
        engine.start(manager);

        trans1 = new Trade(1, 1, rel, 50.0, TradeAction.INSERT, true);
        trans2 = new Trade(2, 1, itc, 40.0, TradeAction.INSERT, false);
        trans3 = new Trade(3, 1, inf, 70.0, TradeAction.INSERT, true);
        trans4 = new Trade(1, 2, rel, 60.0, TradeAction.UPDATE, true);
        trans5 = new Trade(2, 2, itc, 30.0, TradeAction.CANCEL, true);
        trans6 = new Trade(4, 1, inf, 20.0, TradeAction.INSERT, false);

        trades = new ArrayList<Trade>();
        trades.add(trans1);
        trades.add(trans2);
        trades.add(trans3);
        trades.add(trans4);
        trades.add(trans5);
        trades.add(trans6);

        Collections.shuffle(trades); // randomize the order the transactions hit the engine
    }

    @After
    public void after() {
        engine.stop();
    }

    @Test
    public void testHandleTradesFailure() {
        // setup
        trades.remove(trans5);

        // test
        int transId = 0;
        for (Trade trade : trades) {
            engine.acceptTransaction(transId, trade);
            transId++;
        }
        manager.listPositions();

        // verify
        boolean finished = checkCalcs();
        assertFalse("Failed failure test :(", finished);
    }

    @Test
    public void testHandleTradesSuccess() {
        // TODO: still not sure if transaction ID is really needed
        // test
        int transId = 1;
        for (Trade trade : trades) {
            engine.acceptTransaction(transId, trade);
            transId++;
        }
        manager.listPositions();

        // verify
        boolean finished = checkCalcs();
        assertTrue("Failed success test :(", finished);
    }

    private boolean checkCalcs() {
        boolean relOK = manager.getPosition(rel) == wantedREL;
        if (!relOK) {
            System.out.println("Wrong REL:" + manager.getPosition(rel));
        }
        boolean infOK = manager.getPosition(inf) == wantedINF;
        if (!infOK) {
            System.out.println("Wrong INF:" + manager.getPosition(inf));
        }
        boolean itcOK = manager.getPosition(itc) == wantedITC;
        if (!itcOK) {
            System.out.println("Wrong ITC:" + manager.getPosition(itc));
        }
        return relOK && infOK && itcOK;
    }
}
