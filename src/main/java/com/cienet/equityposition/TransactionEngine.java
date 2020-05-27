package com.cienet.equityposition;

import com.cienet.equityposition.entity.Trade;

/**
 * This class handles accepting incoming trade transactions and passing them to the trade manager. Probably should be
 * threadable but for now it's not thread-safe.
 */
public class TransactionEngine {
    private boolean started = false;
    private TradeManager tradeManager;

    public static void main(String[] args) {
        TransactionEngine engine = new TransactionEngine();
        TradeManager manager = new TradeManager();
        engine.start(manager);
    }

    /**
     * TODO: maybe make the manager into an interface to allow different logical trade managers...
     * @param manager checks if this for null
     * @return if started ok, true; else false
     * @throws IllegalArgumentException if the manager param is null
     */
    public boolean start(TradeManager manager) throws IllegalArgumentException {
        if (manager == null) {
            throw new IllegalArgumentException("TradeManager was null in start()");
        }

        if (!started) {
            started = true;
            tradeManager = manager;
            // TODO: log that the engine has started
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stop the engine: don't accept transactions anymore
     * @return true if stopped ok; else false
     */
    public boolean stop() {
        if (started) {
            started = false;
            // TODO: log that the engine has stopped
            return true;
        } else {
            return false;
        }
    }

    /**
     * Immediately deal with trade transactions as they arrive...<br/>
     * "The Positions should be updated after each transaction"
     * @param transactionId not sure if we need this
     * @param trade
     * @return true if the transaction was accepted ok; otherwise false
     */
    public boolean acceptTransaction(int transactionId, Trade trade) {
        if (started) {
            return tradeManager.handleTrade(trade);
        } else {
            // log an error? throw an exception?
            return false;
        }
    }
}
