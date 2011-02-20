package com.dervall.servlet;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;

import java.io.IOException;

/**
 * User: Per
 * Date: 2011-02-19
 * Time: 12:33
 */
public class TestStandardServlet {
    public static junit.framework.Test suite() {
       return new JUnit4TestAdapter(TestStandardServlet.class);
    }

    @Test
    public void testService() throws IOException {
        StandardServlet standardServlet = new StandardServlet();
        standardServlet.service(null);
    }
}
