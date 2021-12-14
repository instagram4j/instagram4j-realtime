package com.github.instagram4j.realtime.mqtt.packet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import com.github.instagram4j.realtime.utils.PacketUtil;
import com.github.instagram4j.realtime.utils.ZipUtil;

public class Payload {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public Payload writeString(final String s) {
        this.writeByteArray(PacketUtil.encodeUTF8(s));
        
        return this;
    }
    
    public Payload writeShort(final short s) {
        this.writeByteArray(PacketUtil.toMsbLsb(s));
        
        return this;
    }
    
    public Payload writeByte(final byte b) {
        out.write(b);
        
        return this;
    }
    
    public Payload writeByteArray(final byte[] b) {
        try {
            out.write(b);
            
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public Payload compress() {
        final byte[] compressed = ZipUtil.zip(out.toByteArray());
        try {
            out.reset();
            out.write(compressed);
            
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    public byte[] toByteArray() {
        return out.toByteArray();
    }

}
