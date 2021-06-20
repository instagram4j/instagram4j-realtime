package com.github.instagram4j.realtime.mqtt.packet;

import java.util.Arrays;
import com.github.instagram4j.realtime.utils.ZipUtil;

public class PublishResponsePacket extends ResponsePacket {
    private String topic;
    private byte[] payload;
    
    public PublishResponsePacket(byte[] data) {
        super(data);
    }
    
    public String getTopic() {
        // TODO: decode utf8 encoded string from packet data
        return "undefined";
    }
    
    public short getIdentifier() {
        // fixed pos of identifier at byte 6 and 7 for now
        // TODO: Get correct identifier dynamically
        int pos = 6;
        return (short) (((this.data[pos] & 0xFF) << 8) | (this.data[pos+1] & 0xFF)); 
    }
    
    public String getJSONPayload() {
        if (payload == null) {
            if (this.data[0] == 0x30)
                payload = ZipUtil.unzip(Arrays.copyOfRange(this.data, 6, this.data.length));
            else if (this.data[0] == 0x32)
                payload = ZipUtil.unzip(Arrays.copyOfRange(this.data, 8, this.data.length));
        }
        
        return new String(payload);
    }

}
