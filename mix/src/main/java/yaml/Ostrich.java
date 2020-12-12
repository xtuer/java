package yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ostrich implements Animal {
    private String nickname;

    @Override
    public void eat() {

    }
}
