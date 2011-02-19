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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.dhcp4java.DHCPBadPacketException;
import org.dhcp4java.DHCPOption;
import org.dhcp4java.DHCPPacket;
import org.dhcp4java.HardwareAddress;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import static org.dhcp4java.DHCPConstants.*;
import static junit.framework.Assert.*;
import static org.dhcp4java.test.HexUtils.hexToBytes;

/**
 */
public class DHCPPacketTest {

	private static DHCPPacket refPacketFromHex;
	private static DHCPPacket refPacketFromSratch;
	private DHCPPacket pac0;
	
	public static junit.framework.Test suite() {
       return new JUnit4TestAdapter(DHCPPacketTest.class);
    }
	
    /*
     * @see TestCase#setUp()
     */
    @BeforeClass
    public static void setUpOnce() throws Exception {
    	byte[] refBuf = HexUtils.hexToBytes(REF_PACKET);

        refPacketFromHex = DHCPPacket.getPacket(refBuf, 0, refBuf.length, true);
        refPacketFromHex.setComment("foobar");
        refPacketFromHex.setAddress(InetAddress.getByName("10.11.12.13"));
        refPacketFromHex.setPort(6767);
    	
    	DHCPPacket packet = new DHCPPacket();
    	packet.setComment("foobar");
    	packet.setOp(BOOTREQUEST);
    	packet.setHtype(HTYPE_ETHER);
    	packet.setHlen((byte) 6);
    	packet.setHops((byte) 0);
    	packet.setSecs((short)0);
    	packet.setXid(0x11223344);
    	packet.setFlags((short) 0x8000);
    	packet.setCiaddr("10.0.0.1");
    	packet.setYiaddr("10.0.0.2");
    	packet.setSiaddr("10.0.0.3");
    	packet.setGiaddr("10.0.0.4");
    	packet.setChaddr(HexUtils.hexToBytes("00112233445566778899AABBCCDDEEFF"));
    	packet.setSname(STR200.substring(0,  64));
    	packet.setFile (STR200.substring(0, 128));
    	packet.setDHCPMessageType(DHCPDISCOVER);
    	packet.setOptionAsInetAddress(DHO_DHCP_SERVER_IDENTIFIER, "12.34.56.68");
    	packet.setOptionAsInt(DHO_DHCP_LEASE_TIME, 86400);
    	packet.setOptionAsInetAddress(DHO_SUBNET_MASK, "255.255.255.0");
    	packet.setOptionAsInetAddress(DHO_ROUTERS, "10.0.0.254");
    	InetAddress[] staticRoutes = new InetAddress[2];
    	staticRoutes[0] = InetAddress.getByName("22.33.44.55");
    	staticRoutes[1] = InetAddress.getByName("10.0.0.254");
    	packet.setOptionAsInetAddresses(DHO_STATIC_ROUTES, staticRoutes);
    	packet.setOptionAsInetAddress(DHO_NTP_SERVERS, "10.0.0.5");
    	packet.setOptionAsInetAddress(DHO_WWW_SERVER, "10.0.0.6");
    	packet.setPaddingWithZeroes(256);
    	packet.setAddress(InetAddress.getByName("10.11.12.13"));
    	packet.setPort(6767);

        refPacketFromSratch = packet;
    }
    
    @Before
    public void setUp() {
    	pac0 = new DHCPPacket();
    }


    @Test
    public void testConstrucor() throws Exception {
    	DHCPPacket pac = new DHCPPacket();
    	
    	assertEquals("", pac.getComment());
    	assertEquals(BOOTREPLY, pac.getOp());
    	assertEquals(HTYPE_ETHER, pac.getHtype());
    	assertEquals(6, pac.getHlen());
    	assertEquals((short) 0, pac.getSecs());
    	assertEquals(InetAddress.getByName("0.0.0.0"), pac.getCiaddr());
    	assertEquals(InetAddress.getByName("0.0.0.0"), pac.getYiaddr());
    	assertEquals(InetAddress.getByName("0.0.0.0"), pac.getSiaddr());
    	assertEquals(InetAddress.getByName("0.0.0.0"), pac.getGiaddr());
    	assertTrue(Arrays.equals(new byte [16], pac.getChaddr()));
    	assertEquals("", pac.getSname());
    	assertEquals("", pac.getFile());
    	assertTrue(Arrays.equals(new byte[0], pac.getPadding()));
    	assertEquals(true, pac.isDhcp());
    	assertEquals(false, pac.isTruncated());
    	DHCPOption[] opts = pac.getOptionsArray();
    	assertNotNull(opts);
    	assertEquals(0, opts.length);
    	
    	assertNull(pac.getAddress());
    	assertEquals(0, pac.getPort());

    }
    
    @Test
    public void testGetPacket() throws Exception {
    	byte[] buf = hexToBytes(REF_PACKET);
    	DatagramPacket udp = new DatagramPacket(buf, buf.length);
    	udp.setAddress(InetAddress.getByName("10.11.12.13"));
    	udp.setPort(6767);
    	DHCPPacket pac = DHCPPacket.getPacket(udp);
    	pac.setComment("foobar");

    	assertEquals(refPacketFromHex, pac);
    	
    }
    @Test (expected=IllegalArgumentException.class)
    public void testGetPacketNull() throws Exception {
    	DHCPPacket.getPacket(null);
    }
    // marshall
    @Test (expected=IllegalArgumentException.class)
    public void testMarshallNull() {
    	DHCPPacket.getPacket(null, 0, 256, true);
    }
    @Test (expected=IndexOutOfBoundsException.class)
    public void testMarshallNegativeOffset() {
    	DHCPPacket.getPacket(new byte[256], -1, 256, true);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testMarshallNegativeLength() {
    	DHCPPacket.getPacket(new byte[256], 0, -1, true);
    }
    @Test (expected=IndexOutOfBoundsException.class)
    public void testMarshallLentghTooLong() {
    	DHCPPacket.getPacket(new byte[256], 1, 256, true);
    }
    @Test
    public void testMarshallUnderLimits() {
    	DHCPPacket.getPacket(new byte[300], 0, 300, true);
    	DHCPPacket.getPacket(new byte[1500], 0, 1500, true);
    }
    @Test (expected=DHCPBadPacketException.class)
    public void testMarshallLentghPacketTooSmall() {
    	DHCPPacket.getPacket(new byte[235], 0, 235, true);
    }
    @Test (expected=DHCPBadPacketException.class)
    public void testMarshallLentghPacketTooBig() {
    	DHCPPacket.getPacket(new byte[1501], 0, 1501, true);
    }
    // serialize
    @Test
    public void testSerializeLimits() {
    	byte[] buf;
    	DHCPPacket pac = new DHCPPacket();
    	buf = pac.serialize();
    	assertEquals(300, buf.length);
    	
    	pac.setOptionRaw(DHO_HOST_NAME, new byte[255]);
    	buf = pac.serialize();
    	assertEquals(498, buf.length);
    	
    	buf = pac.serialize(1500, 1500);
    	assertEquals(1500, buf.length);
    }
    @Test (expected=DHCPBadPacketException.class)
    public void testSerializeOptionOver256() {
    	DHCPPacket pac = new DHCPPacket();
    	pac.setOptionRaw(DHO_HOST_NAME, new byte[256]);
    	pac.serialize();
    }
    @Test (expected=DHCPBadPacketException.class)
    public void testSerializePacketTooBig() {
    	DHCPPacket pac = new DHCPPacket();
    	pac.setOptionRaw((byte)11, new byte[255]);
    	pac.setOptionRaw((byte)12, new byte[255]);
    	pac.setOptionRaw((byte)13, new byte[255]);
    	pac.setOptionRaw((byte)14, new byte[255]);
    	pac.setOptionRaw((byte)15, new byte[255]);
    	pac.serialize();
    }
    
    // Limit tests for setters/getter
    @Test
    public void testSetCHAddrUnderLimit() {
    	String bufs = "FFEEDDCCBBAA9988776655443322110F";
    	DHCPPacket pac = new DHCPPacket();
    	pac.setHlen((byte)16);
    	pac.setChaddr(hexToBytes(bufs));
    	assertEquals(bufs, pac.getChaddrAsHex());
    	pac.setChaddr(null);
    	assertEquals("00000000000000000000000000000000", pac.getChaddrAsHex());
    	pac.setHlen((byte) 255);
    	assertEquals("00000000000000000000000000000000", pac.getChaddrAsHex());
    	pac.setChaddrHex(bufs);
    	assertTrue(Arrays.equals(hexToBytes(bufs), pac.getChaddr()));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetCHAddrTooLong() {
    	DHCPPacket pac = new DHCPPacket();
    	pac.setHlen((byte)16);
    	pac.setChaddr(hexToBytes("FFEEDDCCBBAA9988776655443322110F00"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetCHAddrHexNotEven() {
    	pac0.setChaddrHex("0");
    }
    // ----
    // bad addresses
    @Test (expected=IllegalArgumentException.class)
    public void testSetCIAddrIPv6() throws Exception {
    	pac0.setCiaddr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetCIAddrRaw3() throws Exception {
    	pac0.setCiaddrRaw(new byte[3]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetCIAddrRaw5() throws Exception {
    	pac0.setCiaddrRaw(new byte[5]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetSIAddrIPv6() throws Exception {
    	pac0.setSiaddr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetSIAddrRaw3() throws Exception {
    	pac0.setSiaddrRaw(new byte[3]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetSIAddrRaw5() throws Exception {
    	pac0.setSiaddrRaw(new byte[5]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetYIAddrIPv6() throws Exception {
    	pac0.setYiaddr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetYIAddrRaw3() throws Exception {
    	pac0.setYiaddrRaw(new byte[3]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetYIAddrRaw5() throws Exception {
    	pac0.setYiaddrRaw(new byte[5]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetGIAddrIPv6() throws Exception {
    	pac0.setGiaddr(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetGIAddrRaw3() throws Exception {
    	pac0.setGiaddrRaw(new byte[3]);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetGIAddrRaw5() throws Exception {
    	pac0.setGiaddrRaw(new byte[5]);
    }
    // SName
    @Test
    public void testSetSnameRaw() {
    	pac0.setSnameRaw(new byte[64]);		// maximum size
    	assertEquals("", pac0.getSname());
    	pac0.setSnameRaw(null);
    	assertEquals("", pac0.getSname());
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetSnameRawTooLong() {
    	pac0.setSnameRaw(new byte[65]);
    }
    // File
    @Test
    public void testSetFileRaw() {
    	pac0.setFileRaw(new byte[128]);		// maximum size
    	assertEquals("", pac0.getFile());
    	pac0.setFileRaw(null);
    	assertEquals("", pac0.getFile());
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetFileRawTooLong() {
    	pac0.setFileRaw(new byte[129]);
    }
    
    @Test
    public void testGetHardwareAddress() {
        HardwareAddress ha = new HardwareAddress(HTYPE_ETHER, "001122334455");
        assertEquals(ha, refPacketFromSratch.getHardwareAddress());
        DHCPPacket pac2 = refPacketFromSratch.clone();
        pac2.setHlen((byte)17);
        HardwareAddress ha2 = new HardwareAddress(HTYPE_ETHER, "00112233445566778899AABBCCDDEEFF");
        assertEquals(ha2, pac2.getHardwareAddress());
    }
    @Test
    public void testGetOptionAsNum() {
    	assertEquals(Integer.valueOf(86400), refPacketFromSratch.getOptionAsNum(DHO_DHCP_LEASE_TIME));
    	assertNull(refPacketFromSratch.getOptionAsNum(DHO_USER_CLASS));
    	assertNull(refPacketFromSratch.getOptionAsNum(DHO_STATIC_ROUTES));
    	assertEquals(Integer.valueOf(167772414), refPacketFromSratch.getOptionAsNum(DHO_ROUTERS));
    }

    @Test
    public void testEqualsTrivial() {
    	assertTrue(refPacketFromHex.equals(refPacketFromHex));
    	assertFalse(refPacketFromHex.equals(new Integer(1)));
    	DHCPPacket pac = refPacketFromHex.clone();
    	assertTrue(refPacketFromHex.equals(pac));
    	pac.setHops((byte)-1);
    	assertFalse(refPacketFromHex.equals(pac));
    }
    @Test
    public void compareRefScratch() {
    	// compare packet serialized from packet built from scratch
    	// compare DHCPPacket objects
    	Assert.assertEquals(refPacketFromHex, refPacketFromSratch);
    	// compare byte[] datagrams
    	Assert.assertTrue(Arrays.equals(HexUtils.hexToBytes(REF_PACKET),
                                 refPacketFromSratch.serialize()));
    }
    
    @Test
    public void testMarshall() throws UnknownHostException {
    	// test if serialized packet has the right parameters
    	DHCPPacket packet = refPacketFromHex;
    	
    	assertEquals("foobar", packet.getComment());
    	assertEquals(BOOTREQUEST, packet.getOp());
    	assertEquals(HTYPE_ETHER, packet.getHtype());
    	assertEquals((byte) 6, packet.getHlen());
    	assertEquals((byte)0, packet.getHops());
    	assertEquals(0x11223344, packet.getXid());
    	assertEquals((short) 0x8000, packet.getFlags());
    	assertEquals((short) 0, packet.getSecs());
    	assertEquals(InetAddress.getByName("10.0.0.1"), packet.getCiaddr());
    	assertTrue(Arrays.equals(InetAddress.getByName("10.0.0.1").getAddress(), packet.getCiaddrRaw()));
    	assertEquals(InetAddress.getByName("10.0.0.2"), packet.getYiaddr());
    	assertTrue(Arrays.equals(InetAddress.getByName("10.0.0.2").getAddress(), packet.getYiaddrRaw()));
    	assertEquals(InetAddress.getByName("10.0.0.3"), packet.getSiaddr());
    	assertTrue(Arrays.equals(InetAddress.getByName("10.0.0.3").getAddress(), packet.getSiaddrRaw()));
    	assertEquals(InetAddress.getByName("10.0.0.4"), packet.getGiaddr());
    	assertTrue(Arrays.equals(InetAddress.getByName("10.0.0.4").getAddress(), packet.getGiaddrRaw()));
    	
    	assertEquals("00112233445566778899aabbccddeeff".substring(0, 2*packet.getHlen()), packet.getChaddrAsHex());
    	assertTrue(Arrays.equals(HexUtils.hexToBytes("00112233445566778899AABBCCDDEEFF"), packet.getChaddr()));
    	
    	assertEquals(STR200.substring(0,  64), packet.getSname());
    	assertEquals(STR200.substring(0, 128), packet.getFile());
    	assertTrue(Arrays.equals(new byte[256], packet.getPadding()));
    	assertTrue(packet.isDhcp());
    	assertFalse(packet.isTruncated());
    	
    	assertEquals(DHCPDISCOVER, packet.getDHCPMessageType().byteValue());
    	assertEquals("12.34.56.68", packet.getOptionAsInetAddr(DHO_DHCP_SERVER_IDENTIFIER).getHostAddress());
    	assertEquals(86400, packet.getOptionAsInteger(DHO_DHCP_LEASE_TIME).intValue());
    	assertEquals("255.255.255.0", packet.getOptionAsInetAddr(DHO_SUBNET_MASK).getHostAddress());
    	assertEquals(1, packet.getOptionAsInetAddrs(DHO_ROUTERS).length);
    	assertEquals("10.0.0.254", packet.getOptionAsInetAddrs(DHO_ROUTERS)[0].getHostAddress());
    	assertEquals(2, packet.getOptionAsInetAddrs(DHO_STATIC_ROUTES).length);
    	assertEquals("22.33.44.55", packet.getOptionAsInetAddrs(DHO_STATIC_ROUTES)[0].getHostAddress());
    	assertEquals("10.0.0.254", packet.getOptionAsInetAddrs(DHO_STATIC_ROUTES)[1].getHostAddress());
    	assertEquals(1, packet.getOptionAsInetAddrs(DHO_WWW_SERVER).length);
    	assertEquals("10.0.0.6", packet.getOptionAsInetAddrs(DHO_WWW_SERVER)[0].getHostAddress());
    	assertEquals(null, packet.getOptionAsInetAddrs(DHO_IRC_SERVER));
    	
    	assertFalse(packet.containsOption(DHO_BOOTFILE));
    	assertFalse(packet.containsOption(DHO_PAD));
    	assertFalse(packet.containsOption(DHO_END));
    	assertTrue(packet.containsOption(DHO_WWW_SERVER));
    }
    
    @Test
    public void testRemoveAllOptions() {
    	DHCPPacket packet = refPacketFromHex.clone();
    	assertTrue(packet.containsOption(DHO_WWW_SERVER));
    	packet.removeAllOptions();
    	assertFalse(packet.containsOption(DHO_WWW_SERVER));
    	assertEquals(0, packet.getOptionsArray().length);
    }
    
    
    @Test
    public void testBootP() {
    	pac0.setDhcp(false);
    	assertEquals(REF_BOOTP_STRING, pac0.toString());

    	assertEquals(hexToBytes(EMPTY_BOOTP).length, pac0.serialize().length);
    	assertTrue(Arrays.equals(hexToBytes(EMPTY_BOOTP), pac0.serialize()));
    }
    
    @Test (expected=DHCPBadPacketException.class)
    public void testMarshallInNonStrictMode() {
    	byte[] buf = hexToBytes(REF_PACKET_WITHOUT_DHO_END);
    	DHCPPacket.getPacket(buf, 0, buf.length, true);
    }
    @Test
    public void testMarshallInStrictMode() {
    	byte[] buf = hexToBytes(REF_PACKET_WITHOUT_DHO_END);
    	DHCPPacket.getPacket(buf, 0, buf.length, false);
    }
    
    // padding
    @Test
    public void testSetPaddingWithZeroes() {
    	pac0.setPaddingWithZeroes(-1);
    	assertTrue(Arrays.equals(new byte[0], pac0.getPadding()));
    	pac0.setPaddingWithZeroes(1500);
    	assertTrue(Arrays.equals(new byte[1500], pac0.getPadding()));
    	pac0.setPadding(hexToBytes("FF0011AA"));
    	assertTrue(Arrays.equals(hexToBytes("FF0011AA"), pac0.getPadding()));
    	pac0.setPaddingWithZeroes(4);
    	assertTrue(Arrays.equals(new byte[4], pac0.getPadding()));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetPaddingWithZeroesTooBig() {
    	pac0.setPaddingWithZeroes(1501);
    }
    //
    // option getter and setters
    @Test
    public void testOptionAsByte() {
    	assertNull(pac0.getOptionAsByte(DHO_IP_FORWARDING));
    	pac0.setOptionAsByte(DHO_IP_FORWARDING, (byte)0xf0);
    	assertEquals((byte) 0xf0, pac0.getOptionAsByte(DHO_IP_FORWARDING).byteValue());
    }
    @Test
    public void testOptionAsShort() {
    	assertNull(pac0.getOptionAsShort(DHO_INTERFACE_MTU));
    	pac0.setOptionAsShort(DHO_INTERFACE_MTU, (short) 1500);
    	assertEquals((short) 1500, pac0.getOptionAsShort(DHO_INTERFACE_MTU).shortValue());
    }
    @Test
    public void testOptionAsShorts() {
    	// TODO missing setOptionAsShorts
    	assertNull(pac0.getOptionAsShorts(DHO_PATH_MTU_PLATEAU_TABLE));
    	pac0.setOptionRaw(DHO_PATH_MTU_PLATEAU_TABLE, hexToBytes("FFFF00000010"));
    	short[] shorts = new short[3];
    	shorts[0] = (short)-1;
    	shorts[1] = (short)0;
    	shorts[2] = (short)16;
    	assertTrue(Arrays.equals(shorts, pac0.getOptionAsShorts(DHO_PATH_MTU_PLATEAU_TABLE)));
    }
    @Test
    public void testOptionAsBytes() {
    	assertNull(pac0.getOptionAsShorts(DHO_DHCP_PARAMETER_REQUEST_LIST));
    	pac0.setOptionRaw(DHO_DHCP_PARAMETER_REQUEST_LIST, hexToBytes("FF0180"));
    	assertTrue(Arrays.equals(hexToBytes("FF0180"), pac0.getOptionAsBytes(DHO_DHCP_PARAMETER_REQUEST_LIST)));
    }
    @Test
    public void testOptionAsInetAddress() throws Exception {
    	assertNull(pac0.getOptionAsInetAddr(DHO_SUBNET_MASK));
    	pac0.setOptionAsInetAddress(DHO_SUBNET_MASK, InetAddress.getByName("10.12.14.16"));
    	assertEquals(InetAddress.getByName("10.12.14.16"), pac0.getOptionAsInetAddr(DHO_SUBNET_MASK));
    }
    
    @Test
    public void testOptionAsInteger() {
    	assertNull(pac0.getOptionAsInteger(DHO_DHCP_LEASE_TIME));
    	pac0.setOptionAsInt(DHO_DHCP_LEASE_TIME, -1);
    	assertEquals(-1, pac0.getOptionAsInteger(DHO_DHCP_LEASE_TIME).intValue());
    }
    @Test
    public void testOptionAsString() {
    	assertNull(pac0.getOptionAsString(DHO_BOOTFILE));
    	pac0.setOptionAsString(DHO_BOOTFILE, "foobar");
    	assertEquals("foobar", pac0.getOptionAsString(DHO_BOOTFILE));
    }
    @Test
    public void testOptionRaw() {
    	assertNull(pac0.getOptionRaw(DHO_BOOTFILE));
    	pac0.setOptionRaw(DHO_BOOTFILE, hexToBytes("FF005498"));
    	assertTrue(Arrays.equals(hexToBytes("FF005498"), pac0.getOptionRaw(DHO_BOOTFILE)));
    	pac0.setOptionRaw(DHO_BOOTFILE, null);		// equivalent to removeOption
    	assertNull(pac0.getOptionRaw(DHO_BOOTFILE));
    }
    @Test
    public void testSetOptionNull() {
    	pac0.setOptionRaw(DHO_BOOTFILE, hexToBytes("FF005498"));
    	assertNotNull(pac0.getOptionRaw(DHO_BOOTFILE));
    	pac0.setOption(new DHCPOption(DHO_BOOTFILE, null));
    	assertFalse(pac0.containsOption(DHO_BOOTFILE));
    	assertNull(pac0.getOptionRaw(DHO_BOOTFILE));
    }
    @Test
    public void testSetOptions() throws Exception {
    	DHCPOption[] opts = new DHCPOption[4];
    	opts[0] = DHCPOption.newOptionAsShort(DHO_INTERFACE_MTU, (short)1500);
    	opts[1] = DHCPOption.newOptionAsInt(DHO_DHCP_LEASE_TIME, 0x01FE02FC);
    	opts[2] = null;
    	opts[3] = DHCPOption.newOptionAsInetAddress(DHO_SUBNET_MASK, InetAddress.getByName("252.10.224.3"));
    	pac0.setOptions(opts);
    	DHCPOption[] pacOpts = pac0.getOptionsArray();
    	assertEquals(3, pacOpts.length);
    	assertEquals(opts[0], pacOpts[0]);
    	assertEquals(opts[1], pacOpts[1]);
    	assertEquals(opts[3], pacOpts[2]);
    	// verifying that null setter does not modify packet
    	DHCPPacket pac2 = pac0.clone();
    	pac2.setOptions((DHCPOption[])null);
    	assertEquals(pac0, pac2);
    }
    @Test
    public void testSetOptionsCollection() throws Exception {
    	LinkedList<DHCPOption> list = new LinkedList<DHCPOption>();
    	list.add(DHCPOption.newOptionAsShort(DHO_INTERFACE_MTU, (short)1500));
    	list.add(DHCPOption.newOptionAsInt(DHO_DHCP_LEASE_TIME, 0x01FE02FC));
    	list.add(DHCPOption.newOptionAsInetAddress(DHO_SUBNET_MASK, InetAddress.getByName("252.10.224.3")));
    	pac0.setOptions(list);
    	DHCPOption[] pacOpts = pac0.getOptionsArray();
    	assertEquals(3, pacOpts.length);
    	assertEquals(list.get(0), pacOpts[0]);
    	assertEquals(list.get(1), pacOpts[1]);
    	assertEquals(list.get(2), pacOpts[2]);
    	// verifying that null setter does not modify packet
    	DHCPPacket pac2 = pac0.clone();
    	pac2.setOptions((Collection<DHCPOption>)null);
    	assertEquals(pac0, pac2);
    }
    
    // address/port
    @Test
    public void testSetAddress() throws Exception {
    	pac0.setAddress(InetAddress.getByName("10.255.12.254"));
    	assertEquals(InetAddress.getByName("10.255.12.254"), pac0.getAddress());
    	pac0.setAddress(null);
    	assertNull(pac0.getAddress());
    }
    @Test
    public void testAddrPort() throws Exception {
    	pac0.setAddrPort(new InetSocketAddress(InetAddress.getByName("10.255.12.254"), 6868));
    	assertEquals(InetAddress.getByName("10.255.12.254"), pac0.getAddress());
    	assertEquals(6868, pac0.getPort());
    	pac0.setAddress(InetAddress.getByName("255.255.255.255"));
    	pac0.setPort(0);
    	assertEquals(new InetSocketAddress(InetAddress.getByName("255.255.255.255"), 0), pac0.getAddrPort());
    	pac0.setAddrPort(null);
    	assertNull(pac0.getAddress());
    	assertEquals(0, pac0.getPort());
    	assertEquals(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0), pac0.getAddrPort());
    }
    @Test (expected=IllegalArgumentException.class)
    public void testSetAddressIPv6() throws Exception {
    	pac0.setAddress(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testAddrPortIPv6() throws Exception {
    	pac0.setAddrPort(new InetSocketAddress(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"), 0));
    }
    
    @Test
    public void testToString() {
    	assertEquals(REF_PACKET_TO_STRING, refPacketFromHex.toString());
    	DHCPPacket pac = refPacketFromHex.clone();
    	pac.setOp((byte) 129);
    	pac.setHtype((byte) -2);
    	assertEquals(REF_PACKET_MOD_TO_STRING, pac.toString());
    }

    @Test
    public void testSerialize() throws Exception {
        //TODO Implement serialize().
        //throw new Exception("toto");
    }

    @Test (expected=IndexOutOfBoundsException.class)
    public void testSerializeBadValues() {
        testPacket(   0, -1,   10);    // bad values
    }
    
    @Test (expected=DHCPBadPacketException.class)
    public void testSerializeTooSmall() {
        testPacket(  47,  0,   47);    // packet too small
    }

    @Test (expected=DHCPBadPacketException.class)
    public void testSerializeTooBig() {
        testPacket(4700,  0, 4700);    // packet too big
    }
    
    private static void testPacket(int size, int offset, int length) {
    	DHCPPacket.getPacket(new byte[size], offset, length, true);
    }
    
    // utility functions
    @Test
    public void testGetHostAddress() throws Exception {
    	InetAddress adr;
    	adr = InetAddress.getByName("0.0.0.0");
    	assertEquals(adr.getHostAddress(), DHCPPacket.getHostAddress(adr));
    	adr = InetAddress.getByName("255.255.255.255");
    	assertEquals(adr.getHostAddress(), DHCPPacket.getHostAddress(adr));
    	adr = InetAddress.getByName("10.254.11.252");
    	assertEquals(adr.getHostAddress(), DHCPPacket.getHostAddress(adr));
    }
    @Test (expected=IllegalArgumentException.class)
    public void testGetHostAddressNull() {
    	DHCPPacket.getHostAddress(null);
    }
    @Test (expected=IllegalArgumentException.class)
    public void testGetHostAddressIPv6() throws Exception {
    	DHCPPacket.getHostAddress(InetAddress.getByName("1080:0:0:0:8:800:200C:417A"));
    }
    
    // Hashcode
    @Test
    public void testHashCode() throws Exception {
    	DHCPPacket pac1 = new DHCPPacket();
    	DHCPPacket pac2 = new DHCPPacket();
    	pac2.setYiaddr("10.0.0.1");
    	assertTrue(pac1.hashCode() != 0);
    	assertTrue(pac2.hashCode() != 0);
    	assertTrue(pac1.hashCode() != pac2.hashCode());
    	
    	DHCPPacket pac3 = pac1.clone();
    	assertTrue(pac1.equals(pac3));
    	assertEquals(pac1.hashCode(), pac3.hashCode());
    }

    private static final String REF_PACKET =
        "0101060011223344000080000a0000010a0000020a0000030a00000400112233" +
        "445566778899aabbccddeeff3132333435363738393031323334353637383930" +
        "3132333435363738393031323334353637383930313233343536373839303132" +
        "3334353637383930313233343132333435363738393031323334353637383930" +
        "3132333435363738393031323334353637383930313233343536373839303132" +
        "3334353637383930313233343536373839303132333435363738393031323334" +
        "3536373839303132333435363738393031323334353637383930313233343536" +
        "3738393031323334353637386382536335010136040c22384433040001518001" +
        "04ffffff0003040a0000fe210816212c370a0000fe2a040a00000548040a0000" +
        "06ff000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000";
    private static final String REF_PACKET_WITHOUT_DHO_END =
        "0101060011223344000080000a0000010a0000020a0000030a00000400112233" +
        "445566778899aabbccddeeff3132333435363738393031323334353637383930" +
        "3132333435363738393031323334353637383930313233343536373839303132" +
        "3334353637383930313233343132333435363738393031323334353637383930" +
        "3132333435363738393031323334353637383930313233343536373839303132" +
        "3334353637383930313233343536373839303132333435363738393031323334" +
        "3536373839303132333435363738393031323334353637383930313233343536" +
        "3738393031323334353637386382536335010136040c22384433040001518001" +
        "04ffffff0003040a0000fe210816212c370a0000fe2a040a00000548040a0000" +
        "0600000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000000000000000000000000000000000000000000000000000000000000000" +
        "0000";
    private static final String EMPTY_BOOTP =
    	"0201060000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"0000000000000000000000000000000000000000000000000000000000000000" +
    	"000000000000000000000000";
    private static final String REF_PACKET_TO_STRING =
    	"DHCP Packet\n"+
    	"comment=foobar\n"+
    	"address=10.11.12.13(6767)\n"+
    	"op=BOOTREQUEST(1)\n"+
    	"htype=HTYPE_ETHER(1)\n"+
    	"hlen=6\n"+
    	"hops=0\n"+
    	"xid=0x11223344\n"+
    	"secs=0\n"+
    	"flags=0xffff8000\n"+
    	"ciaddr=10.0.0.1\n"+
    	"yiaddr=10.0.0.2\n"+
    	"siaddr=10.0.0.3\n"+
    	"giaddr=10.0.0.4\n"+
    	"chaddr=0x001122334455\n"+
    	"sname=1234567890123456789012345678901234567890123456789012345678901234\n"+
    	"file=12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678\n"+
    	"Options follows:\n"+
    	"DHO_DHCP_MESSAGE_TYPE(53)=DHCPDISCOVER\n"+
    	"DHO_DHCP_SERVER_IDENTIFIER(54)=12.34.56.68\n"+
    	"DHO_DHCP_LEASE_TIME(51)=86400\n"+
    	"DHO_SUBNET_MASK(1)=255.255.255.0\n"+
    	"DHO_ROUTERS(3)=10.0.0.254 \n"+ 
    	"DHO_STATIC_ROUTES(33)=22.33.44.55 10.0.0.254 \n"+ 
    	"DHO_NTP_SERVERS(42)=10.0.0.5 \n"+ 
    	"DHO_WWW_SERVER(72)=10.0.0.6 \n"+ 
    	"padding[256]=00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private static final String REF_PACKET_MOD_TO_STRING =
    	"DHCP Packet\n"+
    	"comment=foobar\n"+
    	"address=10.11.12.13(6767)\n"+
    	"op=-127\n"+
    	"htype=-2\n"+
    	"hlen=6\n"+
    	"hops=0\n"+
    	"xid=0x11223344\n"+
    	"secs=0\n"+
    	"flags=0xffff8000\n"+
    	"ciaddr=10.0.0.1\n"+
    	"yiaddr=10.0.0.2\n"+
    	"siaddr=10.0.0.3\n"+
    	"giaddr=10.0.0.4\n"+
    	"chaddr=0x001122334455\n"+
    	"sname=1234567890123456789012345678901234567890123456789012345678901234\n"+
    	"file=12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678\n"+
    	"Options follows:\n"+
    	"DHO_DHCP_MESSAGE_TYPE(53)=DHCPDISCOVER\n"+
    	"DHO_DHCP_SERVER_IDENTIFIER(54)=12.34.56.68\n"+
    	"DHO_DHCP_LEASE_TIME(51)=86400\n"+
    	"DHO_SUBNET_MASK(1)=255.255.255.0\n"+
    	"DHO_ROUTERS(3)=10.0.0.254 \n"+ 
    	"DHO_STATIC_ROUTES(33)=22.33.44.55 10.0.0.254 \n"+ 
    	"DHO_NTP_SERVERS(42)=10.0.0.5 \n"+ 
    	"DHO_WWW_SERVER(72)=10.0.0.6 \n"+ 
    	"padding[256]=00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private static final String REF_BOOTP_STRING =
    	"BOOTP Packet\n"+
    	"comment=\n"+
    	"address=(0)\n"+
    	"op=BOOTREPLY(2)\n"+
    	"htype=HTYPE_ETHER(1)\n"+
    	"hlen=6\n"+
    	"hops=0\n"+
    	"xid=0x00000000\n"+
    	"secs=0\n"+
    	"flags=0x0\n"+
    	"ciaddr=0.0.0.0\n"+
    	"yiaddr=0.0.0.0\n"+
    	"siaddr=0.0.0.0\n"+
    	"giaddr=0.0.0.0\n"+
    	"chaddr=0x000000000000\n"+
    	"sname=\n"+
    	"file=\n"+
    	"padding[0]=";
    private static final String STR200 = 
    	"12345678901234567890123456789012345678901234567890" + //50
    	"12345678901234567890123456789012345678901234567890" + //50
    	"12345678901234567890123456789012345678901234567890" + //50
    	"12345678901234567890123456789012345678901234567890";  //50
}
