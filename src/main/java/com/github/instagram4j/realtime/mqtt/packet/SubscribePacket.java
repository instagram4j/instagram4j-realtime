package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;

public class SubscribePacket extends RequestPacket {
    public static final byte SUBSCRIBE_PACKET_TYPE = 8;
    private short packet_id;
    private Topic[] topics;
    
    public SubscribePacket(int packet_identifier, Topic... topics) {
        this.packet_id = (short) packet_identifier;
        this.topics = topics;
    }
    
    @Override
    protected FixedHeader getFixedHeader() {
        return new FixedHeader(SUBSCRIBE_PACKET_TYPE, (byte) 0x2);
    }

    @Override
    protected VariableHeader getVariableHeader() {
        VariableHeader variableHeader = new VariableHeader();
        
        variableHeader.writeByteArray(PacketUtil.to_msb_lsb(packet_id));
        
        return variableHeader;
    }

    @Override
    protected Payload getPayload() {
        Payload payload = new VariableHeader();
        
        for (Topic topic : topics) {
            payload.writeString(topic.getName());
            payload.writeByte(topic.getQoS());
        }
        
        return payload;
    }

}
