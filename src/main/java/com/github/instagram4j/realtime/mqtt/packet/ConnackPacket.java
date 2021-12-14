package com.github.instagram4j.realtime.mqtt.packet;

import java.util.Arrays;
import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class ConnackPacket extends Packet {
    public static final byte CONNACK_PACKET_TYPE = 2;
    private final byte fixedHeaderParameter;
    private final byte[] variableHeader;
    private final byte[] payload;
    
    public ConnackPacket(byte[] data) {
        this.fixedHeaderParameter = data[0];
        this.variableHeader = Arrays.copyOfRange(data, 1, 3);
        this.payload = Arrays.copyOfRange(data, 3, data.length);
        if (PacketUtil.getControlType(this.fixedHeaderParameter) != ConnackPacket.CONNACK_PACKET_TYPE)
            throw new IllegalStateException("Expected CONNACK but received type " + PacketUtil.getControlType(this.fixedHeaderParameter));
    }
    
    public byte getReturnCode() {
        return this.variableHeader[1];
    }
}
