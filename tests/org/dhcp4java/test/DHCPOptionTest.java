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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.dhcp4java.DHCPBadPacketException;
import org.dhcp4java.DHCPConstants;
import org.dhcp4java.DHCPOption;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

import static org.dhcp4java.DHCPConstants.*;
import static junit.framework.Assert.*;

public class DHCPOptionTest {
	
	private static final String testString = "foobar";
	private static final byte[] buf0 = testString.getBytes();

	public static junit.framework.Test suite() {
	       return new JUnit4TestAdapter(DHCPOptionTest.class);
	    }
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorFailPad(){
		new DHCPOption(DHO_PAD, null);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testConstructorFailEnd(){
		new DHCPOption(DHO_END, null);
	}

	@Test
	public void testConstructor() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_MESSAGE, buf0);
		
		assertEquals(opt.getCode(), DHO_DHCP_MESSAGE);
		assertFalse(opt.isMirror());
		assertTrue(Arrays.equals(opt.getValue(), buf0));
		assertTrue(opt.getValue() != buf0);		// value should be cloned
	}
	
	@Test
	public void testConstructorNull() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_MESSAGE, null);
		
		assertEquals(opt.getCode(), DHO_DHCP_MESSAGE);
		assertFalse(opt.isMirror());
		assertEquals(opt.getValue(), null);
	}
	
	@Test
	public void testEquals() {
		DHCPOption opt1 = new DHCPOption(DHO_BOOTFILE, buf0);
		DHCPOption opt2 = new DHCPOption(DHO_BOOTFILE, buf0.clone());
		
		assertTrue(opt1.equals(opt1));
		assertTrue(opt1.equals(opt2));
		assertTrue(opt2.equals(opt1));
		assertFalse(opt1.equals(null));
		assertFalse(opt1.equals(new Integer(1)));
		assertFalse(opt1.equals(new DHCPOption(DHO_BOOTFILE, null)));
	}
	@Test
	public void testEqualsNull() {
		DHCPOption opt1 = new DHCPOption(DHO_BOOTFILE, null);
		DHCPOption opt2 = new DHCPOption(DHO_BOOTFILE, null);
		
		assertTrue(opt1.equals(opt1));
		assertTrue(opt1.equals(opt2));
		assertTrue(opt2.equals(opt1));
		assertFalse(opt1.equals(null));
		assertFalse(opt1.equals(new Integer(1)));
		assertFalse(opt1.equals(new DHCPOption(DHO_BOOTFILE, buf0)));
	}
	@Test
	public void testEqualsMirror() {
		DHCPOption opt1 = new DHCPOption(DHO_BOOTFILE, null);
		DHCPOption opt2 = new DHCPOption(DHO_BOOTFILE, null, false);
		DHCPOption opt3 = new DHCPOption(DHO_BOOTFILE, null, true);
		DHCPOption opt4 = new DHCPOption(DHO_BOOTFILE, null, true);
		
		assertTrue(opt1.equals(opt1));
		assertTrue(opt1.equals(opt2));
		assertTrue(opt2.equals(opt1));
		
		assertTrue(opt3.equals(opt3));
		assertTrue(opt3.equals(opt4));
		assertTrue(opt4.equals(opt3));
		
		assertFalse(opt1.equals(opt3));
		assertFalse(opt3.equals(opt1));
	}
	
	@Test
	public void testHashCode() {
		DHCPOption opt1 = new DHCPOption(DHO_BOOTFILE, buf0);
		DHCPOption opt2 = new DHCPOption(DHO_DHCP_MESSAGE, buf0);
		assertTrue(opt1.hashCode() != 0);
		assertTrue(opt1.hashCode() != opt2.hashCode());
	}
	
	@Test
	public void testToString() {
		DHCPOption opt1 = new DHCPOption(DHO_BOOTFILE, buf0);
		assertEquals(opt1.toString(), "DHO_BOOTFILE(67)=\"foobar\"");
	}
	
	@Test
	public void runMain() throws Exception {
		// there is no real test here, this is just to avoid noise in code coverage tools
		DHCPOption.main(null);
	}
	
	@Test
	public void testUserClassToX() {
		assertNull(DHCPOption.userClassToString(null));
		byte[] userClassBuf1 = "\03foo\06foobar".getBytes();
		assertEquals(DHCPOption.userClassToString(userClassBuf1), "\"foo\",\"foobar\"");
		byte[] userClassBuf2 = "\03foo".getBytes();
		assertEquals(DHCPOption.userClassToString(userClassBuf2), "\"foo\"");
		byte[] userClassBuf3 = "\07foo".getBytes();
		assertEquals(DHCPOption.userClassToString(userClassBuf3), "\"foo\"");
		assertEquals(DHCPOption.userClassToString(new byte[0]), "");
		assertEquals(DHCPOption.userClassToString(new byte[1]), "\"\"");
		assertEquals(DHCPOption.userClassToString(new byte[2]), "\"\",\"\"");
		assertNotNull(DHCPOption.userClassToString(new byte[255]));
		
		// userClassToList are only tested through their string representation
		// last test for null only
		assertNull(DHCPOption.userClassToList(null));
	}
	@Test
	public void testStringListToUserClass() {
		assertNull(DHCPOption.stringListToUserClass(null));
		LinkedList<String> list = new LinkedList<String>();
		list.add("foo");
		list.add("foobar");
		assertTrue(Arrays.equals("\03foo\06foobar".getBytes(), DHCPOption.stringListToUserClass(list)));
		
	}
	// ----------------------------------------------------------------------
	// testing type conversion
	@Test
	public void testByte2Bytes() {
		assertTrue(Arrays.equals(DHCPOption.byte2Bytes((byte)0), new byte[1]));
		byte[] buf1 = { (byte) 0xff };
		assertTrue(Arrays.equals(DHCPOption.byte2Bytes((byte)-1), buf1));
	}
	@Test
	public void testShort2Bytes() {
		assertTrue(Arrays.equals(DHCPOption.short2Bytes((short)0), new byte[2]));
		byte[] buf1 = { (byte) 0xff, (byte) 0xff };
		assertTrue(Arrays.equals(DHCPOption.short2Bytes((short)-1), buf1));
		byte[] buf2 = { (byte) 0x11, (byte) 0x22 };
		assertTrue(Arrays.equals(DHCPOption.short2Bytes((short)0x1122), buf2));
	}
	@Test
	public void testIntBytes() {
		assertTrue(Arrays.equals(DHCPOption.int2Bytes(0), new byte[4]));
		byte[] buf1 = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		assertTrue(Arrays.equals(DHCPOption.int2Bytes(-1), buf1));
		byte[] buf2 = { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44 };
		assertTrue(Arrays.equals(DHCPOption.int2Bytes(0x11223344), buf2));
	}
	
	@Test
	public void testInetAddress2Bytes() throws Exception {
		assertNull(DHCPOption.inetAddress2Bytes(null));
		byte[] buf = { (byte) 10, (byte) 11, (byte) 12, (byte) 13 };
		InetAddress adr = InetAddress.getByName("10.11.12.13");
		assertTrue(Arrays.equals(DHCPOption.inetAddress2Bytes(adr), buf));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testInetAddress2BytesFailNonIpv4() throws Exception {
		DHCPOption.inetAddress2Bytes(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
	}
	
	@Test
	public void testInetAddresses2Bytes() throws Exception {
		assertNull(DHCPOption.inetAddresses2Bytes(null));
		assertTrue(Arrays.equals(DHCPOption.inetAddresses2Bytes(new InetAddress[0]), new byte[0]));
		
		byte[] buf = HexUtils.hexToBytes("10111213FFFFFFFF0000000011223344");
		InetAddress[] iadrs = new InetAddress[4];
		iadrs[0] = InetAddress.getByName("16.17.18.19");
		iadrs[1] = InetAddress.getByName("255.255.255.255");
		iadrs[2] = InetAddress.getByName("0.0.0.0");
		iadrs[3] = InetAddress.getByName("17.34.51.68");
		
		assertTrue(Arrays.equals(DHCPOption.inetAddresses2Bytes(iadrs), buf));
		
		// maximum size allowed
		InetAddress[] maxAdrs = new InetAddress[63];
		Arrays.fill(maxAdrs, InetAddress.getByName("16.17.18.19"));
		assertNotNull(DHCPOption.inetAddresses2Bytes(maxAdrs));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testInetAddresses2BytesFailNonIpv4() throws Exception {
		InetAddress[] iadrs = new InetAddress[4];
		iadrs[0] = InetAddress.getByName("16.17.18.19");
		iadrs[1] = InetAddress.getByName("1080:0:0:0:8:800:200C:417A");
		iadrs[2] = InetAddress.getByName("0.0.0.0");
		iadrs[3] = InetAddress.getByName("17.34.51.68");
		DHCPOption.inetAddresses2Bytes(iadrs);
	}
	
	// agentOptionsToString
	@Test
	public void testAgentOptionsToString() {
		assertNull(DHCPOption.agentOptionsToString(null));
		byte[] buf = "\01\03foo\02\06barbaz\377\00".getBytes();
		assertEquals(DHCPOption.agentOptionsToString(buf), "{1}\"foo\",{2}\"barbaz\",{255}\"\"");
		buf = "\01\377foo".getBytes();
		assertEquals(DHCPOption.agentOptionsToString(buf), "{1}\"foo\"");
		buf = "\01\00".getBytes();
		assertEquals(DHCPOption.agentOptionsToString(buf), "{1}\"\"");
		buf = "\01".getBytes();
		assertEquals(DHCPOption.agentOptionsToString(buf), "");
		assertEquals(DHCPOption.agentOptionsToString(new byte[0]), "");
		
		// agentOptionsToMap is not tested directly, only for null
		assertNull(DHCPOption.agentOptionsToMap(null));
	}
	
	// agentOptionToRaw
	@Test
	public void testAgentOptionToRaw() {
		assertNull(DHCPOption.agentOptionToRaw(null));
		LinkedHashMap<Byte, String> map = new LinkedHashMap<Byte, String>();
		map.put((byte)1, "foo");
		map.put((byte)2, "bar");
		byte[] buf = "\01\03foo\02\03bar".getBytes();
		assertTrue(Arrays.equals(DHCPOption.agentOptionToRaw(map), buf));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testAgentOptionToRawTooBig() {
		LinkedHashMap<Byte, String> map = new LinkedHashMap<Byte, String>();
		map.put((byte) -1, String.valueOf(new char[256]));
		byte[] buf = new byte[257];
		buf[0] = (byte) 255;	// sub-option
		buf[1] = (byte) 255;	// length
		assertTrue(Arrays.equals(DHCPOption.agentOptionToRaw(map), buf));
	}
	// ----------------------------------------------------------------------
	// high level static constructors
	
	// Byte
	@Test
	public void testNewOptionAsByteGetValueAsByte() {
		DHCPOption opt = DHCPOption.newOptionAsByte(DHO_IP_FORWARDING, (byte)1);
		assertEquals(opt.getCode(), DHO_IP_FORWARDING);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("01")));
		
		assertEquals(opt.getValueAsByte(), (byte) 1);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsByteBad() {
		DHCPOption.newOptionAsByte(DHO_DHCP_LEASE_TIME, (byte) 0);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsByteBad() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[0]);
		opt.getValueAsByte();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsByteIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_IP_FORWARDING, null);
		opt.getValueAsByte();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsByteBadSize2() {
		DHCPOption opt = new DHCPOption(DHO_IP_FORWARDING, new byte[2]);
		opt.getValueAsByte();
	}
	
	// Short
	@Test
	public void testNewOptionAsShortGetValueAsShort() {
		DHCPOption opt = DHCPOption.newOptionAsShort(DHO_INTERFACE_MTU, (short)1500);
		assertEquals(opt.getCode(), DHO_INTERFACE_MTU);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("05DC")));
		
		assertEquals(opt.getValueAsShort(), (short) 1500);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsShortBad() {
		DHCPOption.newOptionAsShort(DHO_DHCP_LEASE_TIME, (short) 0);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsShortBad() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[0]);
		opt.getValueAsShort();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsShortIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_INTERFACE_MTU, null);
		opt.getValueAsShort();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsShortBadSize3() {
		DHCPOption opt = new DHCPOption(DHO_INTERFACE_MTU, new byte[3]);
		opt.getValueAsShort();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsShortBadSize1() {
		DHCPOption opt = new DHCPOption(DHO_INTERFACE_MTU, new byte[1]);
		opt.getValueAsShort();
	}
	// Shorts
	@Test
	public void testGetValueAsShorts() {
		DHCPOption opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, HexUtils.hexToBytes("05DC0000FFFF"));
		short[] shorts = new short[3];
		shorts[0] = (short) 1500;
		shorts[1] = (short) 0;
		shorts[2] = (short) -1;
		assertEquals(DHO_PATH_MTU_PLATEAU_TABLE, opt.getCode());
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("05DC0000FFFF")));
		assertTrue(Arrays.equals(shorts, opt.getValueAsShorts()));
	}
//	@Test (expected=IllegalArgumentException.class)
//	public void testNewOptionAsShortsBad() {
//		DHCPOption.newOptionAsShort(DHO_DHCP_LEASE_TIME, (short) 0);
//	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsShortsBad() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[0]);
		opt.getValueAsShorts();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsShortsIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, null);
		opt.getValueAsShorts();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsShortsBadSize1() {
		DHCPOption opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, new byte[1]);
		opt.getValueAsShorts();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsShortsBadSize5() {
		DHCPOption opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, new byte[5]);
		opt.getValueAsShorts();
	}
	
	@Test
	public void testNewOptionAsShorts() {
		short[] shorts = new short[3];
		shorts[0] = (short) 1500;
		shorts[1] = (short) 0;
		shorts[2] = (short) -1;
		DHCPOption opt = DHCPOption.newOptionAsShorts(DHO_PATH_MTU_PLATEAU_TABLE, shorts);
		DHCPOption opt2 = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, HexUtils.hexToBytes("05DC0000FFFF"));
		assertEquals(opt2, opt);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("05DC0000FFFF")));
		assertTrue(Arrays.equals(shorts, opt.getValueAsShorts()));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsShortsIllegal() {
		DHCPOption.newOptionAsShorts(DHO_DHCP_LEASE_TIME, null);
	}
	
	
	// Int
	@Test
	public void testNewOptionAsIntGetValueAsInt() {
		DHCPOption opt = DHCPOption.newOptionAsInt(DHO_DHCP_LEASE_TIME, 0x01FE02FC);
		assertEquals(opt.getCode(), DHO_DHCP_LEASE_TIME);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("01FE02FC")));
		
		assertEquals(opt.getValueAsInt(), 0x01FE02FC);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsIntBad() {
		DHCPOption.newOptionAsInt(DHO_SUBNET_MASK, 0);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsIntBad() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, new byte[0]);
		opt.getValueAsInt();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsIntIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, null);
		opt.getValueAsInt();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsIntBadSize3() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[3]);
		opt.getValueAsInt();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsIntBadSize5() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[5]);
		opt.getValueAsInt();
	}
	
	// Num
	@Test
	public void testNewOptionAsIntGetValueAsNum() {
		DHCPOption opt;
		opt = DHCPOption.newOptionAsInt(DHO_DHCP_LEASE_TIME, 0x01FE02FC);
		assertEquals(Integer.valueOf(0x01FE02FC), opt.getValueAsNum());
		opt = DHCPOption.newOptionAsShort(DHO_INTERFACE_MTU, (short)1500);
		assertEquals(Integer.valueOf(1500), opt.getValueAsNum());
		opt = DHCPOption.newOptionAsByte(DHO_IP_FORWARDING, (byte)1);
		assertEquals(Integer.valueOf(1), opt.getValueAsNum());
		opt = DHCPOption.newOptionAsString(DHO_TFTP_SERVER, "foobar");
		assertNull(opt.getValueAsNum());
		opt = DHCPOption.newOptionAsString(DHO_TFTP_SERVER, null);
		assertNull(opt.getValueAsNum());
	}
	
	// InetAddress
	@Test
	public void testNewOptionAsInetAddressGetValueAsInetAddress() throws Exception {
		DHCPOption opt = DHCPOption.newOptionAsInetAddress(DHO_SUBNET_MASK,
											InetAddress.getByName("252.10.224.3"));
		assertEquals(opt.getCode(), DHO_SUBNET_MASK);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("FC0AE003")));
		
		assertEquals(opt.getValueAsInetAddr(), InetAddress.getByName("252.10.224.3"));
	}
	@Test
	public void testNewOptionAsInetAddressGetValueAsInetAddressSingle() throws Exception {
		DHCPOption opt = DHCPOption.newOptionAsInetAddress(DHO_WWW_SERVER,
											InetAddress.getByName("252.10.224.3"));
		assertEquals(opt.getCode(), DHO_WWW_SERVER);
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("FC0AE003")));
		
		InetAddress[] iadrs = opt.getValueAsInetAddrs();
		assertNotNull(iadrs);
		assertEquals(1, iadrs.length);
		assertEquals(InetAddress.getByName("252.10.224.3"), iadrs[0]);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsInetAddressBad() throws Exception {
		DHCPOption.newOptionAsInetAddress(DHO_DHCP_LEASE_TIME, InetAddress.getByName("252.10.224.3"));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsInetAddressBad() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[0]);
		opt.getValueAsInetAddr();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsInetAddressIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, null);
		opt.getValueAsInetAddr();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsInetAddressBadSize3() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, new byte[3]);
		opt.getValueAsInetAddr();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsInetAddressBadSize5() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, new byte[5]);
		opt.getValueAsInetAddr();
	}
	// InetAddresses
	@Test
	public void testNewOptionAsInetAddressesGetValueAsInetAddresses() throws Exception {
		InetAddress[] iadrs = new InetAddress[3];
		iadrs[0] = InetAddress.getByName("0.0.0.0");
		iadrs[1] = InetAddress.getByName("252.10.224.3");
		iadrs[2] = InetAddress.getByName("255.255.255.255");
		DHCPOption opt = DHCPOption.newOptionAsInetAddresses(DHO_WWW_SERVER, iadrs);
		assertEquals(DHO_WWW_SERVER, opt.getCode());
		assertTrue(Arrays.equals(HexUtils.hexToBytes("00000000FC0AE003FFFFFFFF"), opt.getValue()));
		
		assertTrue(Arrays.equals(iadrs, opt.getValueAsInetAddrs()));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsInetAddressesNullAdress() {
		DHCPOption.newOptionAsInetAddresses(DHO_WWW_SERVER, new InetAddress[1]);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsInetAddressesIPv6() throws Exception {
		InetAddress[] iadrs = new InetAddress[1];
		iadrs[0] = InetAddress.getByName("1080:0:0:0:8:800:200C:417A");
		DHCPOption.newOptionAsInetAddresses(DHO_WWW_SERVER, iadrs);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsInetAddressesBad() throws Exception {
		InetAddress[] iadrs = new InetAddress[1];
		iadrs[0] = InetAddress.getByName("0.0.0.0");
		DHCPOption.newOptionAsInetAddresses(DHO_DHCP_LEASE_TIME, iadrs);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsInetAddressesBad() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_LEASE_TIME, new byte[0]);
		opt.getValueAsInetAddrs();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsInetAddressesIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_WWW_SERVER, null);
		opt.getValueAsInetAddrs();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsInetAddressesBadSize3() {
		DHCPOption opt = new DHCPOption(DHO_WWW_SERVER, new byte[3]);
		opt.getValueAsInetAddrs();
	}
	@Test (expected=DHCPBadPacketException.class)
	public void testGetValueAsInetAddressesBadSize9() {
		DHCPOption opt = new DHCPOption(DHO_WWW_SERVER, new byte[9]);
		opt.getValueAsInetAddrs();
	}

	// String
	@Test
	public void testNewOptionAsStringGetValueAsString() {
		DHCPOption opt = DHCPOption.newOptionAsString(DHO_TFTP_SERVER, "foobar");
		assertEquals(DHO_TFTP_SERVER, opt.getCode());
		assertTrue(Arrays.equals("foobar".getBytes(), opt.getValue()));
		
		assertEquals("foobar", opt.getValueAsString());
		
		opt = new DHCPOption(DHO_TFTP_SERVER, new byte[0]);
		assertEquals("", opt.getValueAsString());
		opt = new DHCPOption(DHO_TFTP_SERVER, new byte[1]);
		assertEquals("", opt.getValueAsString());
		opt = new DHCPOption(DHO_TFTP_SERVER, new byte[2]);
		assertEquals("", opt.getValueAsString());
	}
	@Test (expected=IllegalArgumentException.class)
	public void testNewOptionAsStringBad() {
		DHCPOption.newOptionAsString(DHO_SUBNET_MASK, "foo");
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsStringBad() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, "foobar".getBytes());
		opt.getValueAsString();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsStringIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_TFTP_SERVER, null);
		opt.getValueAsString();
	}

	// Bytes
	@Test
	public void testGetValueAsBytes() {
		byte[] bb = HexUtils.hexToBytes("00010000FC0AE003FFFFFFFF");
		DHCPOption opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, bb);
		assertEquals(DHO_DHCP_PARAMETER_REQUEST_LIST, opt.getCode());
		assertTrue(Arrays.equals(HexUtils.hexToBytes("00010000FC0AE003FFFFFFFF"), opt.getValue()));
		assertTrue(Arrays.equals(HexUtils.hexToBytes("00010000FC0AE003FFFFFFFF"), opt.getValueAsBytes()));
		
		opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, new byte[0]);
		assertTrue(Arrays.equals(new byte[0], opt.getValueAsBytes()));
		opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, new byte[1]);
		assertTrue(Arrays.equals(HexUtils.hexToBytes("00"), opt.getValueAsBytes()));
		opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, new byte[2]);
		assertTrue(Arrays.equals(HexUtils.hexToBytes("0000"), opt.getValueAsBytes()));
	}
	@Test (expected=IllegalArgumentException.class)
	public void testGetValueAsBytesBad() {
		DHCPOption opt = new DHCPOption(DHO_SUBNET_MASK, new byte[1]);
		opt.getValueAsBytes();
	}
	@Test (expected=IllegalStateException.class)
	public void testGetValueAsBytesIllegalState() {
		DHCPOption opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, null);
		opt.getValueAsBytes();
	}

	// ----------------------------------------------------------------------
	// append
	
	@Test
	public void testAppendNullValue() {
		StringBuilder buf;
		DHCPOption opt;

		buf = new StringBuilder();
		opt = new DHCPOption((byte) -2, null);
		opt.append(buf);
		assertEquals("(254)=<null>", buf.toString());

		buf = new StringBuilder();
		opt = new DHCPOption(DHO_DHCP_MESSAGE_TYPE, null);
		opt.append(buf);
		assertEquals("DHO_DHCP_MESSAGE_TYPE(53)=<null>", buf.toString());
		
		buf = new StringBuilder();
		opt = new DHCPOption(DHO_DHCP_LEASE_TIME, null);
		opt.append(buf);
		assertEquals("DHO_DHCP_LEASE_TIME(51)=<null>", buf.toString());
	}
	@Test
	public void testAppend() throws Exception {
		StringBuilder buf;
		DHCPOption opt;

		buf = new StringBuilder();
		opt = new DHCPOption((byte) -2, HexUtils.hexToBytes("00010000FC0AE003FFFF"));
		opt.append(buf);
		assertEquals("(254)=0x00010000FC0AE003FFFF", buf.toString());

		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsByte(DHO_DHCP_MESSAGE_TYPE, DHCPOFFER);
		opt.append(buf);
		assertEquals("DHO_DHCP_MESSAGE_TYPE(53)=DHCPOFFER", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsByte(DHO_DHCP_MESSAGE_TYPE, (byte) -1);
		opt.append(buf);
		assertEquals("DHO_DHCP_MESSAGE_TYPE(53)=-1", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsByte(DHO_IP_FORWARDING, (byte) -10);
		opt.append(buf);
		assertEquals("DHO_IP_FORWARDING(19)=-10", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsShort(DHO_INTERFACE_MTU, (short) 1500);
		opt.append(buf);
		assertEquals("DHO_INTERFACE_MTU(26)=1500", buf.toString());
		
		buf = new StringBuilder();
		opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, HexUtils.hexToBytes("00010000FC0A"));
		opt.append(buf);
		assertEquals("DHO_PATH_MTU_PLATEAU_TABLE(25)=1 0 -1014 ", buf.toString());
		
		buf = new StringBuilder();
		opt = new DHCPOption(DHO_PATH_MTU_PLATEAU_TABLE, new byte[0]);
		opt.append(buf);
		assertEquals("DHO_PATH_MTU_PLATEAU_TABLE(25)=", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsInt(DHO_DHCP_LEASE_TIME, 0x01234567);
		opt.append(buf);
		assertEquals("DHO_DHCP_LEASE_TIME(51)=19088743", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsInetAddress(DHO_SUBNET_MASK,
				InetAddress.getByName("252.10.224.3"));
		opt.append(buf);
		assertEquals("DHO_SUBNET_MASK(1)=252.10.224.3", buf.toString());
		
		buf = new StringBuilder();
		InetAddress[] iadrs = new InetAddress[3];
		iadrs[0] = InetAddress.getByName("0.0.0.0");
		iadrs[1] = InetAddress.getByName("252.10.224.3");
		iadrs[2] = InetAddress.getByName("255.255.255.255");
		opt = DHCPOption.newOptionAsInetAddresses(DHO_WWW_SERVER, iadrs);
		opt.append(buf);
		assertEquals("DHO_WWW_SERVER(72)=0.0.0.0 252.10.224.3 255.255.255.255 ", buf.toString());
		
		buf = new StringBuilder();
		opt = DHCPOption.newOptionAsString(DHO_TFTP_SERVER, "foobar");
		opt.append(buf);
		assertEquals("DHO_TFTP_SERVER(66)=\"foobar\"", buf.toString());

		buf = new StringBuilder();
		opt = new DHCPOption(DHO_DHCP_PARAMETER_REQUEST_LIST, HexUtils.hexToBytes("0001FC0AE003FF"));
		opt.append(buf);
		assertEquals("DHO_DHCP_PARAMETER_REQUEST_LIST(55)=0 1 252 10 224 3 255 ", buf.toString());

		buf = new StringBuilder();
		opt = new DHCPOption(DHO_USER_CLASS, "\03foo\06foobar".getBytes());
		opt.append(buf);
		assertEquals("DHO_USER_CLASS(77)=\"foo\",\"foobar\"", buf.toString());

		buf = new StringBuilder();
		opt = new DHCPOption(DHO_DHCP_AGENT_OPTIONS, "\01\03foo\02\06barbaz\377\00".getBytes());
		opt.append(buf);
		assertEquals("DHO_DHCP_AGENT_OPTIONS(82)={1}\"foo\",{2}\"barbaz\",{255}\"\"", buf.toString());


	}
	
	@Test
	public void testGetOptionFormat() {
		assertEquals(InetAddress.class, DHCPOption.getOptionFormat(DHCPConstants.DHO_SUBNET_MASK));
		assertEquals(InetAddress[].class, DHCPOption.getOptionFormat(DHCPConstants.DHO_ROUTERS));
		assertEquals(int.class, DHCPOption.getOptionFormat(DHCPConstants.DHO_TIME_OFFSET));
		assertEquals(short.class, DHCPOption.getOptionFormat(DHCPConstants.DHO_BOOT_SIZE));
		assertEquals(short[].class, DHCPOption.getOptionFormat(DHCPConstants.DHO_PATH_MTU_PLATEAU_TABLE));
		assertEquals(byte.class, DHCPOption.getOptionFormat(DHCPConstants.DHO_IP_FORWARDING));
		assertEquals(byte[].class, DHCPOption.getOptionFormat(DHCPConstants.DHO_DHCP_PARAMETER_REQUEST_LIST));
		assertEquals(String.class, DHCPOption.getOptionFormat(DHCPConstants.DHO_DOMAIN_NAME));
		assertNull(DHCPOption.getOptionFormat((byte)0));
	}
	
	@Test
	public void testString2Class() {
		assertEquals(InetAddress.class, DHCPOption.string2Class("InetAddress"));
		assertEquals(InetAddress.class, DHCPOption.string2Class("inet"));
		assertEquals(InetAddress[].class, DHCPOption.string2Class("InetAddress[]"));
		assertEquals(InetAddress[].class, DHCPOption.string2Class("inets"));
		assertEquals(int.class, DHCPOption.string2Class("int"));
		assertEquals(short.class, DHCPOption.string2Class("short"));
		assertEquals(short[].class, DHCPOption.string2Class("short[]"));
		assertEquals(short[].class, DHCPOption.string2Class("shorts"));
		assertEquals(byte.class, DHCPOption.string2Class("byte"));
		assertEquals(byte[].class, DHCPOption.string2Class("byte[]"));
		assertEquals(byte[].class, DHCPOption.string2Class("bytes"));
		assertEquals(String.class, DHCPOption.string2Class("String"));
		assertEquals(String.class, DHCPOption.string2Class("string"));
		assertNull(DHCPOption.string2Class("foobar"));
		assertNull(DHCPOption.string2Class(""));
		assertNull(DHCPOption.string2Class(null));
	}
	
	@Test
	public void testParseNewOption() throws Exception {
		DHCPOption opt;
		
		opt = DHCPOption.parseNewOption(DHO_DHCP_LEASE_TIME, int.class, "33424124");
		assertEquals(DHO_DHCP_LEASE_TIME, opt.getCode());
		assertEquals(0x01FE02FC, opt.getValueAsInt());
		opt = DHCPOption.parseNewOption(DHO_INTERFACE_MTU, short.class, "1500");
		assertEquals(DHO_INTERFACE_MTU, opt.getCode());
		assertEquals((short)1500, opt.getValueAsShort());
		opt = DHCPOption.parseNewOption(DHO_IP_FORWARDING, byte.class, "1");
		assertEquals(DHO_IP_FORWARDING, opt.getCode());
		assertEquals((byte)1, opt.getValueAsByte());
		opt = DHCPOption.parseNewOption(DHO_TFTP_SERVER, String.class, "foobar");
		assertEquals(DHO_TFTP_SERVER, opt.getCode());
		assertEquals("foobar", opt.getValueAsString());

		short[] shorts = new short[3];
		shorts[0] = (short) 1500;
		shorts[1] = (short) 0;
		shorts[2] = (short) -1;
		opt = DHCPOption.parseNewOption(DHO_PATH_MTU_PLATEAU_TABLE, short[].class, "1500 0 -1");
		assertEquals(DHO_PATH_MTU_PLATEAU_TABLE, opt.getCode());
		assertTrue(Arrays.equals(opt.getValue(), HexUtils.hexToBytes("05DC0000FFFF")));
		assertTrue(Arrays.equals(shorts, opt.getValueAsShorts()));
		
		opt = DHCPOption.parseNewOption(DHO_DHCP_PARAMETER_REQUEST_LIST, byte[].class, "16 32 64");
		byte[] bytes = HexUtils.hexToBytes("102040");
		assertEquals(DHO_DHCP_PARAMETER_REQUEST_LIST, opt.getCode());
		assertTrue(Arrays.equals(bytes, opt.getValue()));

		opt = DHCPOption.parseNewOption(DHO_WWW_SERVER, byte[].class, "0.0.0.0 252.10.224.3 255.255.255.255");
		assertEquals(DHO_WWW_SERVER, opt.getCode());
		assertTrue(Arrays.equals(HexUtils.hexToBytes("00000000FC0AE003FFFFFFFF"), opt.getValue()));

		opt = DHCPOption.parseNewOption(DHO_WWW_SERVER, InetAddress[].class, "0.0.0.0 252.10.224.3 255.255.255.255");
		InetAddress[] iadrs = new InetAddress[3];
		iadrs[0] = InetAddress.getByName("0.0.0.0");
		iadrs[1] = InetAddress.getByName("252.10.224.3");
		iadrs[2] = InetAddress.getByName("255.255.255.255");
		assertEquals(DHO_WWW_SERVER, opt.getCode());
		assertTrue(Arrays.equals(iadrs, opt.getValueAsInetAddrs()));
		
		opt = DHCPOption.parseNewOption(DHO_WWW_SERVER, InetAddress.class, "252.10.224.3");
		assertEquals(DHO_WWW_SERVER, opt.getCode());
		assertTrue(Arrays.equals(HexUtils.hexToBytes("FC0AE003"), opt.getValue()));
		
		assertNull(DHCPOption.parseNewOption(DHO_WWW_SERVER, InetAddress.class, "www.foo.bar"));
		assertNull(DHCPOption.parseNewOption(DHO_WWW_SERVER, InetAddress[].class, "10.0.0.1 www.foo.bar"));
		
		assertNull(DHCPOption.parseNewOption((byte) 150, Object.class, ""));
	}
	@Test (expected=NullPointerException.class)
	public void testParseNewOptionNull1() {
		DHCPOption.parseNewOption((byte) 10, int.class, null);
	}
	@Test (expected=NullPointerException.class)
	public void testParseNewOptionNull2() {
		DHCPOption.parseNewOption((byte) 10, null, "fobbar");
	}
	@Test (expected=IllegalArgumentException.class)
	public void testParseNewOptionIllegal1() {
		DHCPOption.parseNewOption(DHO_IP_FORWARDING, InetAddress.class, "252.10.224.3");
	}
}
