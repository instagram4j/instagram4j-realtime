package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
public class ConnectPacket extends RequestPacket {
    private static final byte CONNECT_PACKET_TYPE = 1;
    private String PROTOCOL_NAME = "MQTT";
    private byte PROTOCOL_LEVEL = 4;
    private byte USER_NAME = 0;
    private byte PASSWORD = 0;
    private byte WILL_RETAIN = 0;
    private byte WILL_QOS = 0;
    private byte WILL_FLAG = 0;
    private byte CLEAN_SESSION = 1;
    private byte RESERVED = 0;
    private short KEEP_ALIVE = 20;

    @Override
    protected FixedHeader getFixedHeader() {
        return new FixedHeader(CONNECT_PACKET_TYPE, (byte) 0x0);
    }

    @Override
    protected Payload getPayload() {
        Payload payload = new Payload();

        payload.writeString("mqttkkejmmfk");

        return payload;
    }

    @Override
    protected VariableHeader getVariableHeader() {
        VariableHeader variable_header = new VariableHeader();

        variable_header.writeString(PROTOCOL_NAME);
        variable_header.writeByte(PROTOCOL_LEVEL);
        variable_header.writeByte(getConnectFlags());
        variable_header.writeByteArray(PacketUtil.to_msb_lsb(KEEP_ALIVE));

        return variable_header;
    }

    protected byte getConnectFlags() {
        byte output = 0;
        output |= PacketUtil.shift(USER_NAME, 7);
        output |= PacketUtil.shift(PASSWORD, 6);
        output |= PacketUtil.shift(WILL_RETAIN, 5);
        output |= PacketUtil.shift(WILL_QOS, 3);
        output |= PacketUtil.shift(WILL_FLAG, 2);
        output |= PacketUtil.shift(CLEAN_SESSION, 1);
        // bit position 0 (RESERVED) set to 0 as of 3.1.1
        return output;
    }

}
