import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id;
    private String username;
    private String email;

    public User() {
    }

    public User(int id, String username, String email) {

    }
}
