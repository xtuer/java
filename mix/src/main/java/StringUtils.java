import java.io.UnsupportedEncodingException;

public class StringUtils {
    protected static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2]; // 1 个 byte 整数用 2 个 16 进制字符表示

        for (int j = 0; j < bytes.length; j++) {
            byte v = bytes[j];
            hex[j * 2]     = HEX_CHARS[v >> 4 & 0x0F]; // 高 4 位
            hex[j * 2 + 1] = HEX_CHARS[v & 0x0F];      // 低 4 位
        }

        return new String(hex);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "数风流人物";
        System.out.println(bytesToHex(str.getBytes("UTF-8")));
        System.out.println(bytesToHex(str.getBytes("UTF16")));
    }
}
