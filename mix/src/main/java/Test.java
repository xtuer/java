public class Test {
    public static void main(String[] args) {
        Test t = new Test();

        A a1 = new A<String>() {
            @Override
            public void set(String s) {
                System.out.println("A<String>: " + s);
            }
        };

        A a2 = (A<Integer>) n -> {
            System.out.println("A<Integer>: " + n);
        };

        t.m1(a1);
        t.m2(a2);
        t.m2(a1);
    }

    public void m1(A<String> a) {
        a.set("ABC");
    }

    public void m2(A<Integer> a) {
        a.set(12345);
    }
}

interface A<T>{
    void set(T t);
}
