import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Test {
    public static void main(String[] args) throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);

        long add = unsafe.allocateMemory(4);
        unsafe.putInt(add, 12);
        System.out.println(unsafe.getInt(add));
        unsafe.freeMemory(add);

        Test t = (Test) unsafe.allocateInstance(Test.class);
        t.print();
    }

    public Test(int i) {

    }

    public void print() {
        System.out.println("Test.print()");
    }
}
