package com.cienet.equityposition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.cienet.equityposition.entity.SecurityCode;
import com.cienet.equityposition.entity.Trade;
import com.cienet.equityposition.entity.TradeAction;

/**
 * This class handles the logic of trades and their lifecycle (I/U/C)<br/>
 * The method handleTrade is the main workhorse, and is synchronized to allow multithreaded access.<br/>
 * Other possibilities could be a ConcurrentHashMap or other thread-safe datatype
 */
public class TradeManager {
    private final HashMap<Integer, Trade> trades;
    private final HashMap<String, Double> positions;
    // hold "old" positions for UPDATE/CANCEL cases
    // TODO: when is this value "synced" with the current positions?
    private final HashMap<String, Double> prevPositions;
    private final List<Trade> historyLog;

    public TradeManager() {
        trades = new HashMap<>();
        positions = new HashMap<>();
        prevPositions = new HashMap<>();
        historyLog = new ArrayList<>();
    }

    /**
     * Takes a trade and applies trade logic for the trade:
     * <ul>
     * <li>INSERT / UPDATE / CANCEL are actions on a Trade (with same trade id but different version)</li>
     * <li>INSERT will always be 1st version of a Trade, CANCEL will always be last version of Trade.</li>
     * <li>For UPDATE, SecurityCode or Quantity or Buy/Sell can change</li>
     * <li>For CANCEL, any changes in SecurityCode or Quantity or Buy/Sell may change and should be ignored</li>
     * </ul>
     * This method is synchronized, in the attempt to make sure multiple threads can only access this method one at a
     * time <br/>
     * Not necessarily the most performant, more analysis/design would be needed if more performance is needed...maybe
     * something like an Object lock/semaphore pattern
     * @return for now, always returns true...what makes it return false?
     */
    public synchronized boolean handleTrade(Trade trade) throws IllegalArgumentException {
        // TODO: debug log handling a trade
        validateTradeData(trade);

        historyLog.add(trade); // TODO: don't let this get too big!

        if (trades.containsKey(trade.getId())) {
            Trade oldTrade = trades.get(trade.getId());
            // newer trade, so handle it
            if (oldTrade.getVersion() < trade.getVersion()) {
                if (oldTrade.getAction() != TradeAction.CANCEL) {
                    // TODO: log newer trade being handled
                    trades.put(trade.getId(), trade);
                    refreshPosition(trade);
                } else {
                    // INSERT will always be 1st version of a Trade,
                    // CANCEL will always be last version of Trade.
                    // So we can't allow any INSERT/UPDATE after a CANCEL
                    // TODO: log an error? throw an exception ?
                }
            }
        } else {
            // TODO: log new trade being handled
            trades.put(trade.getId(), trade);
            refreshPosition(trade);
        }

        return true;
    }

    private void validateTradeData(Trade trade) {
        // TODO other validation as needed

        // Validate the received security code
        if (SecurityCode.findSecCode(trade.getSecCode()) == null) {
            // TODO: maybe want to save/log more data to understand why the data is wrong
            String wrongSecCode = String.format("Security code was wrong: %s", trade.getSecCode());
            System.out.println(wrongSecCode);
            throw new IllegalArgumentException(wrongSecCode);
        }
    }

    /**
     * Simple output of all received trades, in order they arrived...idempotent
     */
    public void showHistoryLog() {
        System.out.println("Historical trades");
        for (Trade trade : historyLog) {
            System.out.println(trade);
        }
    }

    /**
     * Simple function to list securities and the shares of the security. No changes are made to the positions.
     */
    public void listPositions() {
        for (Entry<String, Double> entry : positions.entrySet()) {
            System.out.println(String.format("%s : %f", entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Get the Position of a specific secCode
     * @param secCode can be null/empty, should be the 3-letter rep of the seccode
     * @return the seccode Position, or 0.0 if the secCode is not found
     */
    public Double getPosition(String secCode) {
        if (positions.containsKey(secCode)) {
            return positions.get(secCode);
        } else {
            // TODO: debug log that this was empty/not found?
            return 0.0;
        }
    }

    private void refreshPosition(Trade trade) {
        if (positions.containsKey(trade.getSecCode())) {
            if (trade.getAction() == TradeAction.CANCEL) {
                positions.put(trade.getSecCode(), prevPositions.get(trade.getSecCode()));
            } else if (trade.getAction() == TradeAction.UPDATE) {
                double quant = prevPositions.get(trade.getSecCode());
                quant += trade.isBuy() ? trade.getQuantity() : -trade.getQuantity();
                positions.put(trade.getSecCode(), quant);
            } else { // INSERT
                double quant = positions.get(trade.getSecCode());
                quant += trade.isBuy() ? trade.getQuantity() : -trade.getQuantity();
                positions.put(trade.getSecCode(), quant);
            }
        } else { // first position, so clean-slate
            prevPositions.put(trade.getSecCode(), 0.0);
            if (trade.getAction() != TradeAction.CANCEL) {
                positions.put(trade.getSecCode(), trade.isBuy() ? trade.getQuantity() : -trade.getQuantity());
            } else {
                positions.put(trade.getSecCode(), 0.0);
            }
        }
    }
}
