package com.github.instagram4j.realtime.mqtt.packet;

import java.io.IOException;

public abstract class ResponsePacket implements Packet {
    protected byte[] data;
    
    protected ResponsePacket(byte[] data) {
        // data is of format
        // [ControlByte VariableHeader Payload]
        // The remaining length is not encoded here!
        // To get the remaining length it is data.length - 1
        this.data = data;
    }
    
    public byte getControlType() {
        return (byte) (data[0] >>> 4);
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        return data;
    }

}
