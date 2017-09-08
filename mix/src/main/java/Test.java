import lombok.Data;

import java.util.UUID;

@Data
public class Test {
    private int id;

    public static void main(String[] args) throws Exception {
        System.out.println(UUID.randomUUID().toString().toUpperCase());
    }
}
