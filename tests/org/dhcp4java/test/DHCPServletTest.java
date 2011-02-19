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
import java.net.InetAddress;

import org.dhcp4java.DHCPBadPacketException;
import org.dhcp4java.DHCPPacket;
import org.dhcp4java.DHCPServlet;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

import static org.junit.Assert.*;
import static org.dhcp4java.DHCPConstants.*;

public class DHCPServletTest {
	
	private static DHCPServletTestServlet servlet = null;
	
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DHCPServletTest.class);
    }
    
    @BeforeClass
    public static void initServlet() {
    	servlet = new DHCPServletTestServlet();
    	servlet.init(null);		// not much to test here
    }
    
    @Test
    public void testServiceDatagram() {
    	assertNull(servlet.serviceDatagram(null));
    	
    	DHCPPacket pac = new DHCPPacket();
    	pac.setDhcp(false);		// BOOTP
    	assertNull(servicePacket(pac));		// reject BOOTP
    	
    	pac = new DHCPPacket();
    	assertNull(servicePacket(pac));		// reject if DHCP_MESSAGE_TYPE is empty
    	
    	pac = new DHCPPacket();
    	pac.setOp(BOOTREPLY);
    	pac.setDHCPMessageType(DHCPDISCOVER);
    	assertNull(servicePacket(pac));		// reject if BOOTREPLY
    	
    	pac = new DHCPPacket();
    	pac.setOp((byte)-1);
    	pac.setDHCPMessageType(DHCPDISCOVER);
    	assertNull(servicePacket(pac));		// reject if bad Op
    	
    	assertNull(servlet.getServer());
    	servlet.setServer(null);
    }
    // test all messages
    @Test
    public void testDoXXX() {
    	messageTypeTester(DHCPDISCOVER);
    	messageTypeTester(DHCPREQUEST);
    	messageTypeTester(DHCPINFORM);
    	messageTypeTester(DHCPDECLINE);
    	messageTypeTester(DHCPRELEASE);
    }
    
    @Test
    public void testInvalidMessageType() {
    	DHCPPacket pac = new DHCPPacket();
    	pac.setDHCPMessageType((byte)-2);
    	pac.setOp(BOOTREQUEST);
    	servlet.lastMessageType = -1;
    	assertNull(servicePacket(pac));
    	assertEquals((byte)-1, servlet.lastMessageType);
    }
    
    private static final DatagramPacket servicePacket(DHCPPacket pac) throws DHCPBadPacketException {
    	byte[] buf = pac.serialize();
    	DatagramPacket udp = new DatagramPacket(buf, buf.length);
    	return servlet.serviceDatagram(udp);
    }
    private static final void messageTypeTester(byte messageType) {
    	DHCPPacket pac = new DHCPPacket();
    	pac.setDHCPMessageType(messageType);
    	pac.setOp(BOOTREQUEST);
    	servlet.lastMessageType = -1;
    	assertNull(servicePacket(pac));
    	assertEquals(messageType, servlet.lastMessageType);
    }
    // test response addresses
    @Test
    public void testResponseAddresses() throws Exception {
    	DHCPServletTestServletWithGoodResponse servlet2 = new DHCPServletTestServletWithGoodResponse();
    	DHCPPacket pac = new DHCPPacket();
    	pac.setDHCPMessageType(DHCPDISCOVER);
    	pac.setOp(BOOTREQUEST);
    	servlet2.postProcessPassed = false;
    	byte[] buf = pac.serialize();
    	DatagramPacket udp = new DatagramPacket(buf, buf.length);
    	
    	servlet2.addressToReturn = null;
    	servlet2.portToReturn = 0;
    	assertNull(servlet2.serviceDatagram(udp));		// reject is address returned is null
    	
    	servlet2.postProcessPassed = false;
    	servlet2.addressToReturn = InetAddress.getByName("10.11.12.13");
    	servlet2.portToReturn = 67;
    	assertNotNull(servlet2.serviceDatagram(udp));
    	assertTrue(servlet2.postProcessPassed);
    }
}

class DHCPServletTestServlet extends DHCPServlet {
	public byte lastMessageType = -1;
	
	@Override
    protected DHCPPacket doDiscover(DHCPPacket request) {
    	lastMessageType = DHCPDISCOVER;
    	return super.doDiscover(request);
    }

	@Override
    protected DHCPPacket doRequest(DHCPPacket request) {
    	lastMessageType = DHCPREQUEST;
    	return super.doRequest(request);
    }

	@Override
    protected DHCPPacket doInform(DHCPPacket request) {
    	lastMessageType = DHCPINFORM;
    	return super.doInform(request);
    }

	@Override
    protected DHCPPacket doDecline(DHCPPacket request) {
    	lastMessageType = DHCPDECLINE;
    	return super.doDecline(request);
    }

	@Override
    protected DHCPPacket doRelease(DHCPPacket request) {
    	lastMessageType = DHCPRELEASE;
    	return super.doRelease(request);
    }
}

class DHCPServletTestServletWithGoodResponse extends DHCPServlet {

	public boolean postProcessPassed = false;
	public InetAddress addressToReturn = null;
	public int portToReturn = 0;
	
	@Override
    protected DHCPPacket doDiscover(DHCPPacket request) {
		DHCPPacket response = new DHCPPacket();
		response.setAddress(addressToReturn);
		response.setPort(portToReturn);
		return response;
    }

	@Override
	protected void postProcess(DatagramPacket requestDatagram, DatagramPacket responseDatagram) {
		super.postProcess(requestDatagram, responseDatagram);
		postProcessPassed = true;
	}
}