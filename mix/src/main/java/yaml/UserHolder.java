package yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserHolder {
    private Map<String, User> users;
    private User john;
}
