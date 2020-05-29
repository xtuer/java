package bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class User {
    private long  id;
    private String username;
    private String password;
    private String nickname;
    private Role   role;


    public enum Gender {
        MALE(1), FEMALE(2);
        private final int value;

        Gender(int value) {
            this.value = value;
        }
    }
    public enum Role {
        ADMIN, STUDENT, TEACHER
    }
}
