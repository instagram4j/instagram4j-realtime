package com.github.instagram4j.realtime.utils;
import java.nio.charset.StandardCharsets;

public class PacketUtil {
    public static byte[] encoded_UTF8(String s) {
        byte[] msb_lsb = to_msb_lsb((short) s.length());
        byte[] chars = s.getBytes(StandardCharsets.UTF_8);
        byte[] output = new byte[s.length() + 2];
        
        output[0] = msb_lsb[0];
        output[1] = msb_lsb[1];
        
        for(int i = 0; i < s.length(); ++i) {
            byte b = chars[i];
            output[i + 2] = b;
        }
        
        return output;
    }
    
    public static byte[] to_msb_lsb(short length) {
        byte MSB = (byte) (length >> 8);
        byte LSB = (byte) length;
        
        return new byte[] { MSB, LSB };
    }
    
    public static short from_msb_lsb(byte msb, byte lsb) {
        return (short) ((msb << 8) & 0xFF | (lsb & 0xFF));
    }
    
    public static byte shift(byte b, int pos) {
        return (byte) (b << pos);
    }
    
    public static String hex_stringify(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(String.format("%02x ", b));
        }
        
        return sb.toString();
    }
    
    public static String stringify(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }
}
