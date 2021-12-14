package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class DisconnectPacket extends Packet {
    public static final byte DISCONNECT_PACKET_TYPE = 14;
    private byte fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(DISCONNECT_PACKET_TYPE, (byte) 0);
    private byte[] variableHeader = new byte[0];
    private byte[] payload = new byte[0];
}
