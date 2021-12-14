package com.github.instagram4j.realtime.mqtt.packet;

import java.util.stream.Stream;
import com.github.instagram4j.realtime.utils.PacketUtil;
import lombok.Getter;

@Getter
public class SubscribePacket extends Packet {
    public static final byte SUBSCRIBE_PACKET_TYPE = 8;
    private short packetIdentifier;
    private Topic[] topics;
    private byte fixedHeaderParameter;
    private byte[] variableHeader;
    private byte[] payload;
    
    public SubscribePacket(short packetIdentifier, Topic... topics) {
        this.packetIdentifier = packetIdentifier;
        this.topics = topics;
        this.fixedHeaderParameter = PacketUtil.toFixedHeaderParameter(SUBSCRIBE_PACKET_TYPE, (byte) 2);
        this.variableHeader = new Payload().writeByteArray(PacketUtil.toMsbLsb(packetIdentifier)).toByteArray();
        this.payload = Stream.of(topics).reduce(new Payload(), (payload, topic) -> payload.writeString(topic.getName()).writeByte(topic.getQoS()), (x,y) -> x).toByteArray();
    }
}
