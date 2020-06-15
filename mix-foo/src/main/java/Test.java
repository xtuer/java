import java.util.concurrent.CopyOnWriteArrayList;

public class Test {
    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> ns = new CopyOnWriteArrayList<>();
        ns.add(1);
        ns.add(2);
        ns.add(3);
        ns.add(4);

        CopyOnWriteArrayList<Integer> ns2 = ns;
        ns2.add(5);

        System.out.println(ns);
        System.out.println(ns2);
    }
}
