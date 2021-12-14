package com.github.instagram4j.realtime.utils;
import java.nio.charset.StandardCharsets;

public final class PacketUtil {
    
    private PacketUtil() {
    }
    
    /**
     * Encode a UTF-8 String according to MQTT 3.1.1 Spec
     * 
     * @param s Input string to encode
     * @return encoded string byte array 
     */
    public static byte[] encodeUTF8(String s) {
        byte[] msb_lsb = toMsbLsb((short) s.length());
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
    
    /**
     * Encode a length into Most Significant Byte (MSB) and Least Significant Byte (LSB)
     * 
     * @param length Input length
     * @return byte array containing the MSB and LSB of the length
     */
    public static byte[] toMsbLsb(short length) {
        byte MSB = (byte) (length >>> 8);
        byte LSB = (byte) length;
        
        return new byte[] { MSB, LSB };
    }
    
    /**
     * From Most Significant Byte (MSB) and Least Significant Byte (LSB) construct the length
     * 
     * @param msb Most Significant Byte
     * @param lsb Least Significant Byte
     * @return short type from MSB and LSB
     */
    public static short fromMsbLsb(byte msb, byte lsb) {
        return (short) ((msb << 8) | (lsb & 0xFF));
    }
    
    /**
     * Return hex representation of a byte array
     * 
     * @param arr Input byte array
     * @return Space separated hex representation of byte array
     */
    public static String hexStringify(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(String.format("%02x ", b));
        }
        
        return sb.toString();
    }
    
    /**
     * Return a UTF-8 string representation of a byte array
     * 
     * @param arr UTF-8 encoded byte array
     * @return String representation
     */
    public static String stringify(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }
    
    /**
     * Return the packet control type from a fixed header parameter byte
     * 
     * @param data fixed header parameter byte which is the first byte of the fixed header
     * @return The packet control type
     */
    public static byte getControlType(byte data) {
        return (byte) (data >>> 4);
    }
    
    /**
     * Given a packet control type and packet parameters construct the fixed header parameter byte
     * 
     * @param packetControlType four bits representing the packet control type
     * @param packetParameters four bits representing the packet parameters
     * @return Fixed header parameter byte
     */
    public static byte toFixedHeaderParameter(byte packetControlType, byte packetParameters) {
        return (byte) ((packetControlType << 4) | packetParameters);
    }
}
