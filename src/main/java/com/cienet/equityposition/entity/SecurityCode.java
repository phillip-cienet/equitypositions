package com.cienet.equityposition.entity;

/**
 * Allowed values for Security Codes
 */
public enum SecurityCode {
                          REL("Relative Dynamics"), ITC("International Trade Commission"), INF("Infinity and Beyond!");

    private final String fullName;

    private SecurityCode(String name) {
        this.fullName = name;
    }

    /**
     * Find the enum based on a string input
     *
     * @return null if not found or the input param is empty/null
     */
    public static SecurityCode findSecCode(String secCode) {
        if (secCode == null || secCode.isEmpty()) {
            return null;
        }

        for (SecurityCode sc : values()) {
            if (secCode.equalsIgnoreCase(sc.name())) {
                return sc;
            }
        }
        return null;
    }
}
