import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;

@Getter
@Setter
@Accessors(chain = true)
public class Test {
    private int id;
    private String name;

    public Test(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void main(String[] args) throws IOException {
    }
}
