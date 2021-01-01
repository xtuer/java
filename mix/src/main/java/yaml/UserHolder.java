package yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserHolder {
    private User john;
    private List<User> users;
    private Map<String, User> userMap;
}
