package com.insaf.demo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void testHello() {
        assertEquals("Hello Insaf!", App.hello("Insaf"));
    }
}
