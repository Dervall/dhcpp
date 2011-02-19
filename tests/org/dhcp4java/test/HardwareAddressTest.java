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

import java.util.Arrays;

import org.dhcp4java.DHCPConstants;
import org.dhcp4java.HardwareAddress;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

import static junit.framework.Assert.*;

public class HardwareAddressTest {

	public static junit.framework.Test suite() {
       return new JUnit4TestAdapter(HardwareAddressTest.class);
    }
	
	private static HardwareAddress ha = new HardwareAddress(DHCPConstants.HTYPE_ETHER, "001122334455");
	
	@Test
	public void testConstructor() {
		assertEquals(DHCPConstants.HTYPE_ETHER, ha.getHardwareType());
		assertEquals("001122334455", ha.getHardwareAddressHex());
		assertTrue(Arrays.equals(HexUtils.hexToBytes("001122334455"), ha.getHardwareAddress()));
		
		HardwareAddress ha2 = new HardwareAddress(DHCPConstants.HTYPE_ETHER, HexUtils.hexToBytes("001122334455"));
		assertEquals(ha, ha2);
		HardwareAddress ha3 = new HardwareAddress(HexUtils.hexToBytes("001122334455"));
		assertEquals(ha, ha3);
		HardwareAddress ha4 = new HardwareAddress("001122334455");
		assertEquals(ha, ha4);
		HardwareAddress ha5 = new HardwareAddress(DHCPConstants.HTYPE_FDDI, HexUtils.hexToBytes("001122334455"));
		assertFalse(ha.equals(ha5));
		
		assertTrue(ha.hashCode() != 0);
		assertEquals(ha.hashCode(), ha2.hashCode());
		assertEquals(ha.hashCode(), ha3.hashCode());
		assertEquals(ha.hashCode(), ha4.hashCode());
		assertTrue(ha.hashCode() != ha5.hashCode());
		
		assertFalse(ha.equals(null));
		assertFalse(ha.equals(new Object()));
	}
	
	@Test
	public void testToString() {
		assertEquals("00:11:22:33:44:55", ha.toString());
		HardwareAddress ha2 = new HardwareAddress(DHCPConstants.HTYPE_FDDI, "0011045508");
		assertEquals("8/00:11:04:55:08", ha2.toString());
	}
	
	@Test
	public void testGetHardwareAddressByString() {
		HardwareAddress ha2 = new HardwareAddress("0011045508FF");
		HardwareAddress ha3 = HardwareAddress.getHardwareAddressByString("0:11:4:55:8:Ff");
		assertEquals(ha2, ha3);
	}
	@Test (expected=NullPointerException.class)
	public void testGetHardwareAddressByStringNull() {
		HardwareAddress.getHardwareAddressByString(null);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetHardwareAddressByStringEmpty() {
		HardwareAddress.getHardwareAddressByString("");
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetHardwareAddressByStringMax() {
		HardwareAddress.getHardwareAddressByString("0:11:4:55:8:1Ff");
	}
}
