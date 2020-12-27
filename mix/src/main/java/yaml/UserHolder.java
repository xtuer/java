package yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserHolder {
    private Map<String, User> users;
    private User john;

    private List<User> members;
}
