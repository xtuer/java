import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter

public class User {
    private int id;
    private String username;
    private String email;
    private boolean isLast;
    private List<String> roles = new LinkedList();

    public User() {
    }

    @Builder
    public User(int id, String username, String email, String... roles) {
        this.id = id;
        this.email = email;
        this.username = username;

        for (String role : roles) {
            this.roles.add(role);
        }
    }

    public static void main(String[] args) {
        User user = User.builder().id(6).username("Alice").email("alice@gmail.com").build();
        System.out.println(JSON.toJSONString(user));
    }
}
