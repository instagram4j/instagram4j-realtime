package com.github.instagram4j.realtime.mqtt.packet;
public class Topic {
    private final String name;
    private final byte QoS;
    
    public Topic(String name, int i) {
        this.name = name;
        this.QoS = (byte) i;
    }
    
    public String getName() {
        return this.name;
    }
    
    public byte getQoS() {
        return this.QoS;
    }
}
