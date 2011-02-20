package com.dervall;

import com.dervall.servlet.StandardServlet;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.dhcp4java.DHCPCoreServer;
import org.dhcp4java.DHCPServerInitException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * User: Per
 * Date: 2011-02-19
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName().toLowerCase());

    public static void main(String[] args) {
        // Fix the properties
        logger.info("Starting DHCP server");

        try {
            DHCPCoreServer server = DHCPCoreServer.initServer(new StandardServlet(), null);
            new Thread(server).start();
        } catch (DHCPServerInitException e) {
            logger.log(Level.SEVERE, "Server init", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Servlet init", e);
        }

        logger.finest("Running");
    }
}
