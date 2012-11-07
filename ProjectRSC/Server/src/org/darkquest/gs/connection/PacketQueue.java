package org.darkquest.gs.connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronized packet queue
 */
public class PacketQueue<T extends Packet> {
    /**
     * The list of packets in the queue
     */
    private ArrayList<T> packets = new ArrayList<T>();

    private final Object lock = new Object();

    /**
     * Adds a packet to the queue
     */
    public void add(T p) {
        synchronized (lock) {
            packets.add(p);
        }
    }

    /**
     * Returns the packets currently in the list and removes them from the
     * backing store
     */
    public List<T> getPackets() {
        synchronized (lock) {
            List<T> temp = this.packets;
            this.packets = new ArrayList<T>(temp.size());

            return temp;
        }
    }

    /**
     * Returns if there is packets to process
     */
    public boolean hasPackets() {
        synchronized (lock) {
            return !packets.isEmpty();
        }
    }
}
