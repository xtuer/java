package lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaTest {
    public static void foo(int... ns) {
        for (int n : ns) {
            System.out.println(n);
        }
    }
    public static void main(String[] args) {
        List<String> t1 = Arrays.asList("Huang Biao", "Hill Man")
                .stream()
                .map(e -> Arrays.asList(e.split(" ")))
                .flatMap(e -> e.stream())
                .collect(Collectors.toList()); // [Huang, Biao, Hill, Man]

        List<String> t2 = Arrays.asList("Huang Biao", "Hill Man")
                .stream()
                .map(line -> line.split(" "))
                .flatMap(Stream::of)
                .collect(Collectors.toList()); // [Huang, Biao, Hill, Man]

        System.out.println(t1);
        System.out.println(t2);
    }

    public static List<User> data() {
        List<User> users = Arrays.asList(
                new User(1, "张三", 1),
                new User(2, "李四", 1),
                new User(3, "王五", 2),
                new User(4, "悟空", 2),
                new User(5, "悟净", 2),
                new User(4, "大圣", 2),  // id 与大圣的重复了，认为是同一个对象
                new User(1, "Alice", 1) // id 与张三的重复了，认为是同一个对象
        );

        return users;
    }
}
