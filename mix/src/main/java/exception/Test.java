package exception;

public class Test {
    public static void foo() {
        throw new ArgumentException("Ops");
    }

    public static void main(String[] args) {
        foo();
    }
}
