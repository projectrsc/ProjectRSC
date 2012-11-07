package org.rscangel.client.stream;

public final class Buffer {
	
    public final byte data[];
    
    public int offset;
    
    public Buffer(byte data[]) {
        this.data = data;
        offset = 0;
    }

    public void addByte(int value) {
        data[offset++] = (byte) value;
    }

    public void addInt(int value) {
        data[offset++] = (byte) (value >> 24);
        data[offset++] = (byte) (value >> 16);
        data[offset++] = (byte) (value >> 8);
        data[offset++] = (byte) value;
    }

    @SuppressWarnings("deprecation")
    public void addString(String value) {
        value.getBytes(0, value.length(), data, offset);
        offset += value.length();
        data[offset++] = 10;
    }

    public int getByte() {
        return data[offset++] & 0xff;
    }

    public int getShort() {
        offset += 2;
        return ((data[offset - 2] & 0xff) << 8) + (data[offset - 1] & 0xff);
    }

    public int getInt() {
        offset += 4;
        return ((data[offset - 4] & 0xff) << 24) + ((data[offset - 3] & 0xff) << 16) + ((data[offset - 2] & 0xff) << 8) + (data[offset - 1] & 0xff);
    }

    public void getBytes(byte dest[], int off, int len) {
        for (int i = off; i < off + len; i++) {
            dest[i] = data[offset++];
        }
    }
}