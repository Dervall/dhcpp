/*
 *	This file is part of dhcp4java, a DHCP API for the Java language.
 *	(c) 2006 Stephan Hadinger
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.dhcp4java.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dhcp4java.DHCPPacket;
import org.dhcp4java.DHCPCoreServer;
import org.dhcp4java.DHCPServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

import static org.dhcp4java.DHCPConstants.*;

/**
 * @author Stephan Hadinger
 */
public class DHCPEndToEndTest {

    private static final String SERVER_ADDR = "127.0.0.1";
    private static final int    SERVER_PORT = 6767;
    private static final int    CLIENT_PORT = 6768;
    
    private static DHCPCoreServer server = null;
    private static DatagramSocket socket = null;
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DHCPEndToEndTest.class);
    }

    @BeforeClass
    public static void setLoggingAll() {
    	Logger.getLogger("org.dhcp4java").setLevel(Level.ALL);
    }
    /**
     * Start Server.
     *
     */
    @BeforeClass
    public static void startServer() throws Exception {
        Properties localProperties = new Properties();

        localProperties.put(DHCPCoreServer.SERVER_ADDRESS, SERVER_ADDR + ':' + SERVER_PORT);
        localProperties.put(DHCPCoreServer.SERVER_THREADS, "1");

        server = DHCPCoreServer.initServer(new DHCPEndToEndTestServlet(), localProperties);

        new Thread(server).start();
        
        socket = new DatagramSocket(CLIENT_PORT);
    }

    @Test (timeout=1000)
    public void testDiscover() throws Exception {
    	byte[] buf;
    	DatagramPacket udp;
    	DHCPPacket pac = new DHCPPacket();
    	pac.setOp(BOOTREQUEST);
    	buf = pac.serialize();
    	udp = new DatagramPacket(buf, buf.length);
    	udp.setAddress(InetAddress.getByName(SERVER_ADDR));
    	udp.setPort(SERVER_PORT);
    	socket.send(udp);
    	udp = new DatagramPacket(new byte[1500], 1500);
    	// TODO
    	// socket.receive(udp);
    }

    @AfterClass
    public static void shutdownServer() {
    	if (socket != null) {
    		socket.close();
    		socket = null;
    	}
    	if (server != null) {		// do some cleanup
    		server.stopServer();
    		server = null;
    	}
    }
}

class DHCPEndToEndTestServlet extends DHCPServlet {
	// to be completed
	
	
}