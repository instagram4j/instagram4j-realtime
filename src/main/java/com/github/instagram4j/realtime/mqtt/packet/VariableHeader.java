package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.PacketUtil;

public class VariableHeader extends Payload {
    public final Short PACKET_IDENTIFIER;
    
    public VariableHeader() {
        this.PACKET_IDENTIFIER = null;
    }
    
    public VariableHeader(short identifier) {
        this.PACKET_IDENTIFIER = identifier;
        this.writeByteArray(PacketUtil.to_msb_lsb(identifier));
    }
}
