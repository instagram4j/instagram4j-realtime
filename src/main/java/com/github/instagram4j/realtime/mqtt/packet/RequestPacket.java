package com.github.instagram4j.realtime.mqtt.packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class RequestPacket implements Packet {
    protected abstract FixedHeader getFixedHeader();
    protected abstract VariableHeader getVariableHeader();
    protected abstract Payload getPayload();
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] payload = getPayload().toByteArray();
        byte[] variable_header = getVariableHeader().toByteArray();
        byte[] fixed_header = getFixedHeader().setRemainingLength(payload.length + variable_header.length).toByteArray();
        
        out.write(fixed_header);
        out.write(variable_header);
        out.write(payload);
        
        return out.toByteArray(); 
    }
}
