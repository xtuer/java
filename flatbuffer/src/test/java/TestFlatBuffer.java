import com.google.flatbuffers.FlatBufferBuilder;
import com.xtuer.bean.Person;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class TestFlatBuffer {
    private static final String FILE_NAME = "/Users/Biao/Desktop/person.data";

    @Test
    public void testWrite() throws Exception {
        // [1] 创建 Person 对象
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int root = Person.createPerson(builder, builder.createString("道格拉斯·狗"), 30);
        builder.finish(root);

        // [2] 序列化 Person 对象为字节码
        byte[] buffer = builder.sizedByteArray();

        // [3] 保存字节码到文件，也可以进行网络传输
        FileOutputStream out = new FileOutputStream(FILE_NAME);
        out.write(buffer);
        out.close();
    }

    @Test
    public void testRead() throws Exception {
        // [1] 从文件读取字节码，也可以是使用 Socket 传输得到的
        byte[] buf = new byte[1024];
        FileInputStream in = new FileInputStream(FILE_NAME);
        in.read(buf);
        in.close();

        // [2] 把字节码反序列化为 Person 对象
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        Person person = Person.getRootAsPerson(buffer);
        System.out.println("Name: " + person.name() + ", Age: " + person.age());
    }
}
