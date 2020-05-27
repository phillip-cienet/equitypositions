package com.cienet.equityposition.entity;

import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SecurityCodeTest {

    @Before
    public void before() {

    }

    @After
    public void after() {

    }

    @Test
    public void testFindSecCodes() {
        // test and verify
        for (SecurityCode testSecCode : SecurityCode.values()) {
            SecurityCode secCode = SecurityCode.findSecCode(testSecCode.name());
            assertTrue("Should be found: " + testSecCode, secCode == testSecCode);
        }
    }

    @Test
    public void testNotFoundSecCode() {
        // test
        SecurityCode secCode = SecurityCode.findSecCode("WWF");
        // verify
        assertTrue("Should be null in notfound", secCode == null);
    }

    @Test
    public void testEmptySecCode() {
        // test
        SecurityCode secCode = SecurityCode.findSecCode("");
        // verify
        assertTrue("Should be null in empty", secCode == null);
    }

    @Test
    public void testNullSecCode() {
        // test
        SecurityCode secCode = SecurityCode.findSecCode(null);
        // verify
        assertTrue("Should be null in null", secCode == null);
    }
}
