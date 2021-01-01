package lambda;

public interface Bar {
    void fahren(int value);

    default void gehen() {
        System.out.println("gehen()");
        fahren(33);
    }

    default void fliegen() {
        System.out.println("fliegen()");
    }
}
