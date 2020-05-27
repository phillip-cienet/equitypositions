package com.cienet.equityposition.entity;

/**
 * This POJO class represents one trade and the associated information for a trade.<br/>
 * No unit tests for simple POJOs with no real b/l. When b/l is added we should add unit tests... Not thread-safe
 */
public class Trade {
    final int id;
    final int version;
    final String secCode;
    final double quantity;
    final TradeAction action;
    final boolean buy; // true is buy, false is sell

    public Trade(int newId, int newVersion, String newSecCode, double newQuantity, TradeAction newAction,
                 boolean newBuy) {
        id = newId;
        version = newVersion;
        secCode = newSecCode;
        quantity = newQuantity;
        action = newAction;
        buy = newBuy;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getSecCode() {
        return secCode;
    }

    public double getQuantity() {
        return quantity;
    }

    public TradeAction getAction() {
        return action;
    }

    public boolean isBuy() {
        return buy;
    }

    @Override
    public String toString() {
        return "Trade [id=" + id + ", version=" + version + ", secCode=" + secCode + ", quantity=" + quantity
            + ", action=" + action + ", buy=" + buy + "]";
    }
}
