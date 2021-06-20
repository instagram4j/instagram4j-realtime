package com.github.instagram4j.realtime.mqtt.packet;

public class ConnackPacket extends ResponsePacket {
    public static final byte CONNACK_PACKET_TYPE = 2;

    public ConnackPacket(byte[] data) {
        super(data);
    }
    
    public byte getReturnCode() {
        return this.data[2];
    }
}
