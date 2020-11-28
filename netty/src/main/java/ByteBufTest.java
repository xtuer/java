import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * ByteBuf 测试: duplicate (wrap) 和 copy 的区别
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf o = Unpooled.buffer();
        o.writeBytes("Alice".getBytes());

        ByteBuf d = o.duplicate(); // 底层 origin 使用同一个数组，但各自维护自己的读写 index，不过共享了引用计数
        ByteBuf c = o.copy();      // 底层使用新的数组

        o.writeByte('X');  // 影响 o，不影响 d，修改了 writerIndex
        d.setByte(0, 'G'); // 影响 o 和 d，因为修改的是底层 byte 数组
        dump(o); // 输出: GliceX
        dump(d); // 输出: Glice
        dump(c); // 输出: Alice

        // 引用计数测试
        System.out.println(o.refCnt()); // 输出: 1
        System.out.println(d.refCnt()); // 输出: 1
        System.out.println(c.refCnt()); // 输出: 1

        ReferenceCountUtil.release(d);
        ReferenceCountUtil.release(c);

        System.out.println(o.refCnt()); // 输出: 0
        System.out.println(d.refCnt()); // 输出: 0
        System.out.println(c.refCnt()); // 输出: 0
    }

    public static void dump(ByteBuf buf) {
        System.out.println(buf);
        System.out.println(ByteBufUtil.hexDump(buf));
        System.out.println(buf.toString(CharsetUtil.UTF_8));
    }
}
