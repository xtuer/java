import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class User {
    private int id;
    private String username;
    private String email;
    private boolean isLast;

    public User() {
    }

    @Builder
    public User(int id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public static void main(String[] args) {
        User user = User.builder().id(6).username("Alice").email("alice@gmail.com").build();
        System.out.println(JSON.toJSONString(user));
    }
}
