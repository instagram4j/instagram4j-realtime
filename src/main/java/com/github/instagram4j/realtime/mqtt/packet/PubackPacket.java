package com.github.instagram4j.realtime.mqtt.packet;

public class PubackPacket extends RequestPacket {
    public static byte PUBACK_PAKCET_TYPE = 4;
    private short packet_identifier;
    
    public PubackPacket(short packet_identifier) {
        this.packet_identifier = packet_identifier;
    }
    
    @Override
    protected FixedHeader getFixedHeader() {
        return new FixedHeader(PUBACK_PAKCET_TYPE, (byte) 0x2);
    }

    @Override
    protected VariableHeader getVariableHeader() {
        return new VariableHeader(this.packet_identifier);
    }

    @Override
    protected Payload getPayload() {
        return new Payload();
    }
    
}
