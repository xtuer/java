import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    String name;
    int age;

    public static void main(String[] args) throws IOException {
        Files.lines(Paths.get("/Users/Biao/Documents/workspace/Java/mix/src/main/java/BTree.java")).limit(10).forEach(System.out::println);
    }
}
