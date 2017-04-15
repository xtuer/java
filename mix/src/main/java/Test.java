import java.nio.CharBuffer;
import java.util.List;

public class Test {
    public static void main(String[] argv) throws Exception {
        CharBuffer buffer = CharBuffer.allocate(11);
        fillBuffer(buffer,"hello world");
        buffer.position(6);
        buffer.compact();
        buffer.put("j");
        buffer.put("a");
        buffer.put("v");
        buffer.put("a");
        buffer.flip();

        while (buffer.hasRemaining()) {
            System.out.print(buffer.get());
        }
    }
    private static void fillBuffer(CharBuffer buffer, String string) {
        for (int i = 0; i < string.length(); i++) {
            buffer.put(string.charAt(i));
        }
    }
}
