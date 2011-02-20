package com.dervall.subnet;

import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.dhcp4java.DHCPPacket;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Per
 * Date: 2011-02-19
 * Time: 15:08
 */
public class Subnet {
    private List<Pool> pools = new ArrayList<Pool>();

    public Subnet() {
    }

    public void addPool(Pool pool) {
        pools.add(pool);
    }

    public boolean matchesRequest(DHCPPacket request) {
        return true;
    }

    public void reserveAddress(InetAddress ip) {
        for (Pool pool : pools) {
            pool.reserveIp(ip);
        }
    }

    public InetAddress offerAddress() {
        for (Pool pool : pools) {
            Inet4Address offer = pool.offerIp();
            if (offer != null ) {
                return offer;
            }
        }
        return null;
    }

    public boolean ackAddress() {
        return false;
    }
}
