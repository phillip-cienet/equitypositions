package com.cienet.equityposition.entity;

/**
 * Represents a trade action done in a transaction:<br/>
 * INSERT / UPDATE / CANCEL are actions on a Trade (with same trade id but different version)<br/>
 * INSERT will always be 1st version of a Trade, <br/>
 * CANCEL will always be last version of Trade. <br/>
 * No unit tests for simple enums with no real b/l. When b/l is added we should add unit tests...
 */
public enum TradeAction {
                         INSERT, UPDATE, CANCEL;
}
