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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.dhcp4java.DHCPConstants;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

import static org.junit.Assert.*;

/**
 * Test of DHCPConstants internals.
 * 
 * @author Stephan Hadinger
 *
 */
public class DHCPConstantsTest {
	
	public static junit.framework.Test suite() {
       return new JUnit4TestAdapter(DHCPConstantsTest.class);
    }

	@Test
	public void testConstants() throws UnknownHostException {
		assertEquals(InetAddress.getByName("0.0.0.0"), DHCPConstants.INADDR_ANY);
		assertEquals(InetAddress.getByName("255.255.255.255"), DHCPConstants.INADDR_BROADCAST);
	}
	
	@Test
	public void testGetBootNamesMap() {
		Map<Byte, String> map = DHCPConstants.getBootNamesMap();
		assertNotNull(map);
		assertEquals("BOOTREQUEST", map.get(DHCPConstants.BOOTREQUEST));
		assertEquals("BOOTREPLY", map.get(DHCPConstants.BOOTREPLY));
		assertNull(map.get(3));
	}
	
	@Test
	public void testGetHtypesMap() {
		Map<Byte, String> map = DHCPConstants.getHtypesMap();
		assertNotNull(map);
		assertEquals("HTYPE_ETHER", map.get(DHCPConstants.HTYPE_ETHER));
		assertNull(map.get(64));
	}
	
	@Test
	public void testGetDhcpCodesMap() {
		Map<Byte, String> map = DHCPConstants.getDhcpCodesMap();
		assertNotNull(map);
		assertEquals("DHCPDISCOVER", map.get(DHCPConstants.DHCPDISCOVER));
		assertNull(map.get(127));
	}
	
	@Test
	public void testGetDhoNamesMap() {
		Map<Byte, String> map = DHCPConstants.getDhoNamesMap();
		assertNotNull(map);
		assertEquals("DHO_SUBNET_MASK", map.get(DHCPConstants.DHO_SUBNET_MASK));
		assertEquals("DHO_DHCP_LEASE_TIME", map.get(DHCPConstants.DHO_DHCP_LEASE_TIME));
		assertNull(map.get(145));
	}
	
	@Test
	public void testGetDhoNamesReverseMap() {
		Map<String, Byte> map = DHCPConstants.getDhoNamesReverseMap();
		assertNotNull(map);
		assertEquals(DHCPConstants.DHO_SUBNET_MASK, (byte)map.get("DHO_SUBNET_MASK"));
		assertEquals(DHCPConstants.DHO_DHCP_LEASE_TIME, (byte)map.get("DHO_DHCP_LEASE_TIME"));
		assertNull(map.get(""));
	}
	
	@Test
	public void testGetDhoNamesReverse() {
		assertEquals(DHCPConstants.DHO_SUBNET_MASK, (byte)DHCPConstants.getDhoNamesReverse("DHO_SUBNET_MASK"));
		assertEquals(DHCPConstants.DHO_DHCP_LEASE_TIME, (byte)DHCPConstants.getDhoNamesReverse("DHO_DHCP_LEASE_TIME"));
		assertNull(DHCPConstants.getDhoNamesReverse(""));
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetDhoNamesReverseNull() {
		DHCPConstants.getDhoNamesReverse(null);
	}
	
	@Test
	public void testGetDhoName() {
		assertEquals("DHO_SUBNET_MASK", DHCPConstants.getDhoName(DHCPConstants.DHO_SUBNET_MASK));
		assertEquals("DHO_DHCP_LEASE_TIME", DHCPConstants.getDhoName(DHCPConstants.DHO_DHCP_LEASE_TIME));
		assertNull(DHCPConstants.getDhoName((byte)145));
	}
}
