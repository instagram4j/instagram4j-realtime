package com.github.instagram4j.realtime.mqtt.packet;
public class PingReqPacket extends RequestPacket {
    private static final byte PINGREQ_PACKET_TYPE = 12;

    @Override
    protected FixedHeader getFixedHeader() {
        return new FixedHeader(PINGREQ_PACKET_TYPE, (byte) 0x0);
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
