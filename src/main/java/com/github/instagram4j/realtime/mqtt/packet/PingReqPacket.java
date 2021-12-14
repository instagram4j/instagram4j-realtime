package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class PingReqPacket extends Packet {
    private static final byte PINGREQ_PACKET_TYPE = 12;
    private final byte fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(PINGREQ_PACKET_TYPE, (byte) 0);
    private final byte[] variableHeader = new byte[0];
    private final byte[] payload = new byte[0];
}
