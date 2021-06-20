package com.github.instagram4j.realtime.mqtt.packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import com.github.instagram4j.realtime.utils.PacketUtil;
import com.github.instagram4j.realtime.utils.ZipUtil;

public class Payload implements Packet {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public void writeString(String s) {
        this.writeByteArray(PacketUtil.encoded_UTF8(s));
    }
    
    public void writeByte(byte b) {
        out.write(b);
    }
    
    public void writeByteArray(byte[] b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public void compress() {
        byte[] compressed = ZipUtil.zip(out.toByteArray());
        try {
            out.reset();
            out.write(compressed);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    public byte[] toByteArray() {
        return out.toByteArray();
    }

}
