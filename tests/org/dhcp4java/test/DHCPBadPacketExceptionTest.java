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

import org.dhcp4java.DHCPBadPacketException;
import org.junit.Test;


import junit.framework.JUnit4TestAdapter;

/**
 * These are complementary test (not essential ones) designed to increase the test
 * coverage.
 * 
 * @author Stephan Hadinger
 *
 */
public class DHCPBadPacketExceptionTest {
	
	public static junit.framework.Test suite() {
	       return new JUnit4TestAdapter(DHCPBadPacketExceptionTest.class);
	    }
		
	@Test (expected=DHCPBadPacketException.class)
	public void testDHCPBadPacketExceptionVoid() {
		throw new DHCPBadPacketException();
	}
	
	@Test (expected=DHCPBadPacketException.class)
	public void testDHCPBadPacketExceptionString() {
		throw new DHCPBadPacketException("foobar");
	}
	
	@Test (expected=DHCPBadPacketException.class)
	public void testDHCPBadPacketExceptionThrowableString() {
		throw new DHCPBadPacketException("foobar", new RuntimeException());
	}
	
	@Test (expected=DHCPBadPacketException.class)
	public void testDHCPBadPacketExceptionThrowable() {
		throw new DHCPBadPacketException(new RuntimeException());
	}

}
