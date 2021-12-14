package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class PubackPacket extends Packet {
    public static byte PUBACK_PAKCET_TYPE = 4;
    private final short packetIdentifier;
    private final byte fixedHeaderParameter;
    private final byte[] variableHeader;
    private final byte[] payload = new byte[0];
    
    public PubackPacket(final short packetIdentifier) {
        this.packetIdentifier = packetIdentifier;
        this.fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(PUBACK_PAKCET_TYPE, (byte) 0x2);
        this.variableHeader = new Payload().writeByteArray(PacketUtil.toMsbLsb(this.packetIdentifier)).toByteArray();
    }
}
