package yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bird implements Animal {
    private String name;

    @Override
    public void eat() {

    }
}
