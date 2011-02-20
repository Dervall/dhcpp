package com.dervall.subnet;

import org.dhcp4java.Util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * User: Per
 * Date: 2011-02-19
 * Time: 20:54
 */
public class Pool {
    private long base;
    private long end;

    private class Fragment {
        private long start;
        private long end;

        public Fragment(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public Pool(Inet4Address baseAddress, Inet4Address netmask) {
        // Add one fragment to the pool
        base = Util.inetAddress2Long(baseAddress);
        long mask = Util.inetAddress2Long(netmask);

        if ((base & mask) != base) {
            throw new IllegalArgumentException("Base AND Mask for a Ip pool does not constitute a valid subnet");
        }

        end = base | (~mask);
        fragments.add(new Fragment(base, end));
    }

    /**
     * Reserve an IP address so that it will not get assigned any other client.
     * If the IP is already leased out this will do nothing.
     * @param ip Ip to reserve
     */
    public void reserveIp(InetAddress ip) {

        long ipLong = Util.inetAddress2Long(ip);
        for (ListIterator<Fragment> iterator = fragments.listIterator(); iterator.hasNext();) {
            Fragment fragment = iterator.next();

            // If this fragment is big enough to contain this IP address
            if (fragment.start >= ipLong && fragment.end <= ipLong) {
                if (ipLong == fragment.start) {
                    // Shrink fragment head
                    ++fragment.start;
                } else if (ipLong == fragment.end) {
                    // Shrink fragment tail
                    --fragment.end;
                } else {
                    long oldEnd = fragment.end;
                    fragment.end = ipLong - 1;
                    iterator.add(new Fragment(ipLong + 1, oldEnd));
                }
            }
        }
    }

    public Inet4Address offerIp() {
        // Find an address which is not in use
        if (fragments.size() > 0) {
            Fragment fragment = fragments.get(0);

            long ip = fragment.start;
            ++fragment.start;

            // If the fragment has shrunk to nothing, remove it
            if (fragment.start > fragment.end) {
                fragments.remove(0);
            }

            return (Inet4Address) Util.long2InetAddress(ip);
        }

        // There are no free ips to offer from this pool.
        return null;
    }
}
