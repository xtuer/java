import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
@Accessors(chain = true)
public class Test {
    public static void main(String[] args) throws Exception {
        printBinary(lastNBits1(3));
        printBinary(lastNBits1(8));
        printBinary(lastNBits1(20));
        printBinary(lastNBits1(31));
    }

    public static int lastNBits1(int n) {
        return -1 ^ (-1 << n);
    }

    public static void printBinary(int n) {
        String bs = Integer.toBinaryString(n);
        System.out.println(StringUtils.leftPad(bs, 32, '0'));
    }
}
