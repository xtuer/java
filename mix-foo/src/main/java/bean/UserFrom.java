package bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
// @Accessors(chain = true)
public class UserFrom {
    private long id;
    private String username;
    private String password;
    private String mobile;
}
