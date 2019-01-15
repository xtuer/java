import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BooleanTest {
    private int count = 10;
    private boolean visible;
    private boolean persistent = true;

    public BooleanTest() {}

    public BooleanTest(int count, boolean visible) {
        this.count = count;
        this.visible = visible;
    }
}
