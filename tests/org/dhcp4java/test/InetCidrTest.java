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
import java.util.ArrayList;
import java.util.List;

import org.dhcp4java.InetCidr;
import org.dhcp4java.Util;
import org.junit.Test;


import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import static junit.framework.Assert.*;

public class InetCidrTest {

	public static junit.framework.Test suite() {
       return new JUnit4TestAdapter(InetCidrTest.class);
    }
	
	@Test
	public void testConstructor() throws Exception {
		InetCidr cidr = new InetCidr(InetAddress.getByName("224.17.252.127"), 24);
		assertEquals(InetAddress.getByName("224.17.252.0"), cidr.getAddr());
		assertEquals(24, cidr.getMask());
		
	}
	@Test (expected=NullPointerException.class)
	public void testConstructorBadArgNull() {
		new InetCidr(null, 20);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorBadArgIPv6() throws Exception {
		new InetCidr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"), 20);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorBadArgMaskTooSmall() throws Exception {
		new InetCidr(InetAddress.getByName("16.17.18.19"), 0);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorBadArgMaskTooBig() throws Exception {
		new InetCidr(InetAddress.getByName("16.17.18.19"), 33);
	}
	
	@Test
	public void testConstructor2() throws Exception {
		InetCidr cidr0 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("10.11.12.0"), InetAddress.getByName("255.255.255.0"));
		assertEquals(cidr0, cidr1);
	}
	@Test (expected=NullPointerException.class)
	public void testConstructorBadArgNull2() {
		new InetCidr(null, null);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorBadArgIPv62() throws Exception {
		new InetCidr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"), InetAddress.getByName("255.255.255.0"));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorBadArgMask() throws Exception {
		new InetCidr(InetAddress.getByName("10.11.12.0"), InetAddress.getByName("255.255.255.12"));
	}

	@Test
	public void testAddrmask2CidrGood() {
		InetAddress ip = Util.int2InetAddress(0x12345678);
		InetCidr cidr1 = new InetCidr(ip, 30);
		InetCidr cidr2 = new InetCidr(ip, Util.int2InetAddress(0xFFFFFFFC));
		assertEquals(cidr1, cidr2);
	}
	
	@Test
	public void testToString() throws Exception {
		InetCidr cidr = new InetCidr(InetAddress.getByName("16.17.18.19"), 20);
		assertEquals("16.17.16.0/20", cidr.toString());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hash1 = (new InetCidr(InetAddress.getByName("224.17.252.127"), 24)).hashCode();
		int hash2 = (new InetCidr(InetAddress.getByName("224.17.252.127"), 20)).hashCode();
		assertTrue(hash1 != 0);
		assertTrue(hash2 != 0);
		assertTrue(hash1 != hash2);
	}
	
	@Test
	public void testEquals() throws Exception {
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("224.17.252.127"), 24);
		InetCidr cidr2 = new InetCidr(InetAddress.getByName("224.17.252.0"), 24);
		
		assertTrue(cidr1.equals(cidr1));
		assertTrue(cidr1.equals(cidr2));
		assertTrue(cidr2.equals(cidr1));
		assertFalse(cidr1.equals(null));
		assertFalse(cidr1.equals(new Integer(1)));
		assertFalse(cidr1.equals(new InetCidr(InetAddress.getByName("224.17.252.0"), 25)));
		assertFalse(cidr1.equals(new InetCidr(InetAddress.getByName("225.17.252.0"), 24)));
	}
	
	@Test (expected=NullPointerException.class)
	public void testAddrmask2CidrAddrNull() {
		new InetCidr(null, Util.int2InetAddress(0x12345678));
	}
	@Test (expected=NullPointerException.class)
	public void testAddrmask2CidrAddrMask() {
		new InetCidr(Util.int2InetAddress(0x12345678), null);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testAddrmask2CidrBadMask() {
		InetAddress ip = Util.int2InetAddress(0x12345678);
		new InetCidr(ip, ip);		// exception should be raised here
	}
	
	@Test
	public void testAddr2Cidr() {
		int ip = 0xFFFFFFFF;
		int mask = 32;
		InetCidr[] addrs = InetCidr.addr2Cidr(Util.int2InetAddress(ip));
		
		assertEquals(32, addrs.length);
		for (int i=0; i<32; i++) {
			InetCidr refValue =  new InetCidr(Util.int2InetAddress(ip), mask);
			assertEquals(addrs[i], refValue);
			assertEquals(addrs[i].getAddr(), Util.int2InetAddress(ip));
			assertEquals(addrs[i].getMask(), mask);
			ip = ip << 1;
			mask--;
		}
	}
	@Test (expected=IllegalArgumentException.class)
	public void testAddr2CidrNull() {
		InetCidr.addr2Cidr(null);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testAddr2CidrIPv6() throws Exception {
		InetCidr.addr2Cidr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
	}
	
	@Test
	public void testToLong() throws UnknownHostException {
		InetCidr cidr = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		assertEquals(0x180A0B0C00L, cidr.toLong());
	}
	
	@Test
	public void testFromLong() throws UnknownHostException {
		InetCidr cidr = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		assertEquals(cidr, InetCidr.fromLong(0x180A0B0C00L));
		assertEquals(cidr, InetCidr.fromLong(0x180A0B0CFFL));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testFromLongNegative() {
		InetCidr.fromLong(-1);
	}
	
	@Test
	public void testCompareTo() throws UnknownHostException {
		InetCidr cidr0 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		InetCidr cidr2 = new InetCidr(InetAddress.getByName("10.11.12.0"), 23);
		InetCidr cidr3 = new InetCidr(InetAddress.getByName("10.11.12.0"), 25);
		InetCidr cidr4 = new InetCidr(InetAddress.getByName("10.11.11.0"), 24);
		InetCidr cidr5 = new InetCidr(InetAddress.getByName("10.11.13.0"), 24);
		InetCidr cidr6 = new InetCidr(InetAddress.getByName("11.11.12.0"), 24);
		InetCidr cidr7 = new InetCidr(InetAddress.getByName("129.11.12.0"), 24);

		assertEquals(0, cidr0.compareTo(cidr0));
		assertEquals(0, cidr0.compareTo(cidr1));
		assertEquals(1, cidr1.compareTo(cidr2));
		assertEquals(-1, cidr1.compareTo(cidr3));
		assertEquals(1, cidr1.compareTo(cidr4));
		assertEquals(-1, cidr1.compareTo(cidr5));
		assertEquals(-1, cidr1.compareTo(cidr6));
		assertEquals(-1, cidr1.compareTo(cidr7));
	}
	
	@Test
	public void testIsInetCidrListSorted() throws Exception {
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
//		InetCidr cidr2 = new InetCidr(InetAddress.getByName("10.11.12.0"), 23);
		InetCidr cidr3 = new InetCidr(InetAddress.getByName("10.11.12.0"), 25);
//		InetCidr cidr4 = new InetCidr(InetAddress.getByName("10.11.11.0"), 24);
		InetCidr cidr5 = new InetCidr(InetAddress.getByName("10.11.13.0"), 24);
//		InetCidr cidr6 = new InetCidr(InetAddress.getByName("11.11.12.0"), 24);
		InetCidr cidr7 = new InetCidr(InetAddress.getByName("129.11.12.0"), 24);
		
		assertEquals(true, InetCidr.isSorted(null));
		List<InetCidr> list1 = new ArrayList<InetCidr>();
		list1.add(cidr1);
		list1.add(cidr3);
		list1.add(cidr5);
		list1.add(cidr7);
		assertEquals(true, InetCidr.isSorted(list1));
		list1 = new ArrayList<InetCidr>();
		list1.add(cidr1);
		list1.add(cidr5);
		list1.add(cidr3);
		list1.add(cidr7);
		assertEquals(false, InetCidr.isSorted(list1));
		list1 = new ArrayList<InetCidr>();
		list1.add(cidr1);
		list1.add(cidr3);
		list1.add(cidr3);
		list1.add(cidr5);
		list1.add(cidr7);
		assertEquals(false, InetCidr.isSorted(list1));
	}
	@Test (expected=NullPointerException.class)
	public void testIsInetCidrListSortedNullElement() throws Exception {
		List<InetCidr> list1 = new ArrayList<InetCidr>();
		list1.add(null);
		InetCidr.isSorted(list1);
	}
	
	@Test (expected=NullPointerException.class)
	public void testCompareToNull() throws UnknownHostException {
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		cidr1.compareTo(null);
	}
	
	@Test
	public void testHasNoOverlap() throws UnknownHostException {
		InetCidr cidr1 = new InetCidr(InetAddress.getByName("10.11.12.0"), 24);
		InetCidr cidr2 = new InetCidr(InetAddress.getByName("10.11.12.0"), 23);
		InetCidr cidr3 = new InetCidr(InetAddress.getByName("10.11.12.0"), 25);
//		InetCidr cidr4 = new InetCidr(InetAddress.getByName("10.11.11.0"), 24);
		InetCidr cidr5 = new InetCidr(InetAddress.getByName("10.11.13.0"), 24);
		InetCidr cidr6 = new InetCidr(InetAddress.getByName("11.11.12.0"), 24);
		InetCidr cidr7 = new InetCidr(InetAddress.getByName("129.11.12.0"), 24);
		List<InetCidr> list = new ArrayList<InetCidr>();
		list.add(cidr1);
		list.add(cidr5);
		list.add(cidr6);
		list.add(cidr7);
		InetCidr.checkNoOverlap(list);
		list.clear();
		list.add(cidr1);
		list.add(cidr2);
		list.add(cidr3);
		try {
			InetCidr.checkNoOverlap(list);
			Assert.fail();
		} catch (IllegalStateException e) {
			// good
		}
		list.clear();
		list.add(new InetCidr(InetAddress.getByName("10.11.0.0"), 16));
		list.add(cidr1);
		try {
			InetCidr.checkNoOverlap(list);
			Assert.fail();
		} catch (IllegalStateException e) {
			// good
		}
		InetCidr.checkNoOverlap(null);
	}
	@Test (expected=NullPointerException.class)
	public void testHasNoOverlapNullElement() {
		List<InetCidr> list = new ArrayList<InetCidr>();
		list.add(null);
		InetCidr.checkNoOverlap(list);
	}
	
}
