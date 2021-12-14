package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class ConnectPacket extends Packet {
    private static final byte CONNECT_PACKET_TYPE = 1;
    private final String protocolName;
    private final byte protocolLevel;
    private final byte username;
    private final byte password;
    private final byte willRetain;
    private final byte willQoS;
    private final byte willFlag;
    private final byte cleanSession;
    private final byte reserved;
    private final short keepAlive;
    private final byte fixedHeaderParameter;
    private final byte[] variableHeader;
    private final byte[] payload;
    
    // private final String PROTOCOL_NAME = "MQTT";
    // private final byte PROTOCOL_LEVEL = 4;
    // private final byte USER_NAME = 0;
    // private final byte PASSWORD = 0;
    // private final byte WILL_RETAIN = 0;
    // private final byte WILL_QOS = 0;
    // private final byte WILL_FLAG = 0;
    // private final byte CLEAN_SESSION = 1;
    // private final byte RESERVED = 0;
    // private final short KEEP_ALIVE = 20;

    public ConnectPacket(
            final String protocolName,
            final byte protocolLevel,
            final byte username,
            final byte password,
            final byte willRetain,
            final byte willQoS,
            final byte willFlag,
            final byte cleanSession,
            final byte reserved,
            final short keepAlive,
            final byte[] payload) {
        this.protocolName = protocolName;
        this.protocolLevel = protocolLevel;
        this.username = username;
        this.password = password;
        this.willRetain = willRetain;
        this.willQoS = willQoS;
        this.willFlag = willFlag;
        this.cleanSession = cleanSession;
        this.reserved = reserved;
        this.keepAlive = keepAlive;
        this.fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(CONNECT_PACKET_TYPE, (byte) 0x0);
        this.variableHeader = new Payload()
                .writeString(protocolName)
                .writeByte(protocolLevel)
                .writeByte(this.getConnectFlags())
                .writeByteArray(PacketUtil.toMsbLsb(keepAlive))
                .toByteArray();
        this.payload = payload;
    }

    protected byte getConnectFlags() {
        int output = 0;
        output |= this.username << 7;
        output |= this.password << 6;
        output |= this.willRetain << 5;
        output |= this.willQoS << 3;
        output |= this.willFlag << 2;
        output |= this.cleanSession << 1;

        // bit position 0 (RESERVED) set to 0 as of 3.1.1
        return (byte) output;
    }
}
