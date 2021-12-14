package com.github.instagram4j.realtime.mqtt.packet;

public class MQTToTConnectPacket extends ConnectPacket {
    
    public MQTToTConnectPacket(byte[] payload) {
        super("MQTToT", (byte) 3, (byte) 1, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 20, payload);
    }
}
