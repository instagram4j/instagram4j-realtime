package com.github.instagram4j.realtime.mqtt.packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Packet {
    protected abstract byte getFixedHeaderParameter();
    protected abstract byte[] getVariableHeader();
    protected abstract byte[] getPayload();
    
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] fixedHeader = new FixedHeader(this.getFixedHeaderParameter(), this.getVariableHeader().length + this.getPayload().length).toByteArray();
        
        out.write(fixedHeader);
        out.write(getVariableHeader());
        out.write(getPayload());
        
        return out.toByteArray(); 
    }
}
