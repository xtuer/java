import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;

public class Test {
    public static void main(String[] args) throws Exception {
        Foo foo = new Foo();
        Field field = Foo.class.getDeclaredField("name");
        field.setAccessible(true);
        field.set(foo, "Biao");

        System.out.println(foo);
    }
}

class Foo {
    private String name;

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                '}';
    }
}
