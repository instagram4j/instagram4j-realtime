package com.github.instagram4j.realtime.mqtt.packet;

public class DisconnectPacket extends RequestPacket {
    public static final byte DISCONNECT_PACKET_TYPE = 14;
    
    @Override
    protected FixedHeader getFixedHeader() {
        return new FixedHeader(DISCONNECT_PACKET_TYPE, (byte) 0);
    }

    @Override
    protected VariableHeader getVariableHeader() {
        return new VariableHeader();
    }

    @Override
    protected Payload getPayload() {
        return new Payload();
    }

}
