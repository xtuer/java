import bean.User;
import com.alibaba.fastjson.JSON;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class Test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:config/application.xml");
        UserService userService = context.getBean(UserService.class);

//        User user = userService.newUser().setId(1);
//        // userService.insertUser(user);
//        user = userService.findUserById(1);
//        System.out.println(JSON.toJSONString(user, true));
//
//        user = JSON.parseObject(JSON.toJSONString(user, true), User.class);
//        System.out.println(user);

        System.out.println(User.Gender.class.getEnumConstants()[1]);
        System.out.println(User.Gender.FEMALE.ordinal());
        System.out.println(User.Gender.FEMALE.name());
        User.Gender g = Enum.valueOf(User.Gender.class, "FEMALE");
        System.out.println(g);
    }
}
