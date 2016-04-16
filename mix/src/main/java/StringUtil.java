import java.io.UnsupportedEncodingException;

public class StringUtil {
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4]; // 高 4 位无符号右移 4 位
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // 低 4 位
        }

        return new String(hexChars);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "黄彪";
        System.out.println(bytesToHex(str.getBytes("UTF-8")));
        System.out.println(bytesToHex(str.getBytes("UTF16")));
    }
}
