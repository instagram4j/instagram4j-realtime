package com.github.instagram4j.realtime.mqtt.packet;

import com.github.instagram4j.realtime.utils.ZipUtil;

public class MQTToTConnectPacket extends ConnectPacket {
    private byte[] payload;
    
    public MQTToTConnectPacket(byte[] payload) {
        this.payload = payload;
        this.setPROTOCOL_NAME("MQTToT");
        this.setPROTOCOL_LEVEL((byte) 3);
        this.setUSER_NAME((byte) 1);
        this.setPASSWORD((byte) 1);
    }
    
    @Override
    public Payload getPayload() {
        Payload payload = new Payload();
        
        payload.writeByteArray(ZipUtil.zip(this.payload));
        
        return payload;
    }
    
}
