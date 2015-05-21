package com.geeshenk.commons;

//see http://stackoverflow.com/a/4141360/923560
public class DumpUtil {
    
    private static final String HEX_DIGITS = "0123456789abcdef";
    
    public static String toHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i != data.length; i++) {
            int v = data[i] & 0xff;
            
            buf.append(HEX_DIGITS.charAt(v >> 4));
            buf.append(HEX_DIGITS.charAt(v & 0xf));
            
        }
        
        return buf.toString();
    }

}
