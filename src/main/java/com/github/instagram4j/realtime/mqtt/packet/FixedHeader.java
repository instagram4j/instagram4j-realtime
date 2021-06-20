package com.github.instagram4j.realtime.mqtt.packet;

import java.io.ByteArrayOutputStream;

public class FixedHeader implements Packet {
    private final byte PACKET_AND_RESERVED;
    private int REMAINING_LENGTH;
    
    public FixedHeader(byte CONTROL_PACKET_TYPE, byte RESERVED) {
        this.PACKET_AND_RESERVED = (byte) ((CONTROL_PACKET_TYPE << 4) | RESERVED);
    }
    
    public FixedHeader(byte CONTROL_PACKET_TYPE, byte RESERVED, int REMAINING_LENGTH) {
        this.PACKET_AND_RESERVED = (byte) ((CONTROL_PACKET_TYPE << 4) | RESERVED);
        this.REMAINING_LENGTH = REMAINING_LENGTH;
    }
    
    public FixedHeader setRemainingLength(int len) {
        this.REMAINING_LENGTH = len;
        return this;
    }
    
    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(PACKET_AND_RESERVED);
        
        int X = REMAINING_LENGTH;
        do {
            byte encodedByte = (byte) (X % 128);
            X /= 128;
            if (X > 0) {
                encodedByte = (byte) (encodedByte | 128);
            }
            baos.write(encodedByte);
        } while (X > 0);
        
        return baos.toByteArray();
    }
    
}
