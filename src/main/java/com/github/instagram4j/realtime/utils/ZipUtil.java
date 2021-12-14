package com.github.instagram4j.realtime.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil {
    public static byte[] zip(byte[] in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length);
        Deflater compressor = new Deflater(9);
        
        compressor.setInput(in);
        compressor.finish();
        
        byte[] buffer = new byte[1024];
        while (!compressor.finished()) {
            int len = compressor.deflate(buffer);
            baos.write(buffer, 0, len);
        }
        
        return baos.toByteArray();
    }
    
    public static byte[] unzip(byte[] in) throws DataFormatException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length);
        Inflater decompressor = new Inflater();
        
        decompressor.setInput(in);

        byte[] buffer = new byte[1024];
        while (!decompressor.finished()) {
            final int len = decompressor.inflate(buffer);
            baos.write(buffer, 0, len);
        }
        
        return baos.toByteArray();
    }
}
