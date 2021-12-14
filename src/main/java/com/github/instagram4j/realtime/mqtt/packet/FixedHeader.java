package com.github.instagram4j.realtime.mqtt.packet;

import java.io.ByteArrayOutputStream;

public class FixedHeader extends Payload {
    private final byte PACKET_PARAMETERS;
    private final int REMAINING_LENGTH;
    
    public FixedHeader(byte PACKET_PARAMETERS, int REMAINING_LENGTH) {
        this.PACKET_PARAMETERS = PACKET_PARAMETERS;
        this.REMAINING_LENGTH = REMAINING_LENGTH;
    }
    
    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(this.PACKET_PARAMETERS);
        
        int X = this.REMAINING_LENGTH;
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
