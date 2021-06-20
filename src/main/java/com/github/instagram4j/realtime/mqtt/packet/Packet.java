package com.github.instagram4j.realtime.mqtt.packet;
import java.io.IOException;

public interface Packet {
    byte[] toByteArray() throws IOException;
}
