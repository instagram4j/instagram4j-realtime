package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;

public class PublishPacket extends RequestPacket {
    public static final byte PUBLISH_PACKET_TYPE = 3;
    private String topic_name;
    private short packet_id;
    private Payload payload;
    
    public PublishPacket(String topic_name, int packet_identifier, Payload payload) {
        this.topic_name = topic_name;
        this.packet_id = (short) packet_identifier;
        this.payload = payload;
    }
    
    @Override
    protected FixedHeader getFixedHeader() {
        // control flags (0x2) 0010
        // dup: 0 qos: 01 retain: 0
        return new FixedHeader(PUBLISH_PACKET_TYPE, (byte) 0x2);
    }

    @Override
    protected VariableHeader getVariableHeader() {
        VariableHeader variableHeader = new VariableHeader();
        
        variableHeader.writeString(this.topic_name);
        variableHeader.writeByteArray(PacketUtil.to_msb_lsb(this.packet_id));
        
        return variableHeader;
    }

    @Override
    protected Payload getPayload() {
        return this.payload;
    }

}
