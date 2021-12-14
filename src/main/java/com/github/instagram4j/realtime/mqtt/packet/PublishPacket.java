package com.github.instagram4j.realtime.mqtt.packet;

import java.util.Arrays;
import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class PublishPacket extends Packet {
    public static final byte PUBLISH_PACKET_TYPE = 3;
    private final boolean dupFlag;
    private final byte QoS;
    private final boolean retain;
    private final String topicName;
    private final short packetIdentifier;
    private final byte fixedHeaderParameter;
    private final byte[] variableHeader;
    private final byte[] payload;

    public PublishPacket(
            final boolean dupFlag,
            final byte Qos,
            final boolean retain,
            final String topicName,
            final short packetIdentifier,
            final byte[] payload) {
        this.dupFlag = dupFlag;
        this.QoS = Qos;
        this.retain = retain;
        this.topicName = topicName;
        this.packetIdentifier = packetIdentifier;
        this.fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(PUBLISH_PACKET_TYPE, this.getPublishParameters());
        this.variableHeader = new Payload()
                .writeString(this.topicName)
                .writeByteArray(QoS != 0 ? PacketUtil.toMsbLsb(this.packetIdentifier) : new byte[0])
                .toByteArray();
        this.payload = payload;
    }
    
    public PublishPacket(byte[] data) {
        this.fixedHeaderParameter = data[0];
        this.retain = (this.fixedHeaderParameter & 0x1) == 1 ? true : false;
        this.QoS = (byte) ((this.fixedHeaderParameter >>> 1) & 0x3);
        this.dupFlag = ((this.fixedHeaderParameter >>> 3) & 0x1) == 1 ? true : false;
        final int topicNameLength = PacketUtil.fromMsbLsb(data[1], data[2]);
        this.topicName = PacketUtil.stringify(Arrays.copyOfRange(data, 3, 3 + topicNameLength));
        final int packetIdentifierPos = 3 + topicNameLength;
        this.packetIdentifier = this.QoS != 0 ? PacketUtil.fromMsbLsb(data[packetIdentifierPos], data[packetIdentifierPos + 1]) : 0;
        final int variableHeaderLength = topicNameLength + (this.QoS != 0 ? 4 : 2);
        this.variableHeader = Arrays.copyOfRange(data, 1, 1 + variableHeaderLength);
        this.payload = Arrays.copyOfRange(data, 1 + variableHeaderLength, data.length);
    }
    
    private byte getPublishParameters() {
        int parameters = 0;
        
        parameters |= (this.dupFlag ? 1 : 0) << 3;
        parameters |= this.QoS << 1;
        parameters |= this.retain ? 1 : 0;
        
        return (byte) parameters;
    }
}
