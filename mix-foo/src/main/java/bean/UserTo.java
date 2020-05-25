package bean;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.sf.cglib.beans.BeanCopier;
import org.springframework.beans.BeanUtils;
// import org.springframework.cglib.beans.BeanCopier;

@Getter
@Setter
// @Accessors(chain = true)
public class UserTo {
    private String username;
    private String mobile;
    private int age;
    private int password;

    public static void main(String[] args) {
        UserFrom userFrom = new UserFrom();
        userFrom.setId(123L);
        userFrom.setUsername("Alice");
        userFrom.setPassword("Passw0rd");

        UserTo userTo = new UserTo(); //.setAge(23);

        // BeanCopier copier = BeanCopier.create(UserFrom.class, UserTo.class, false);
        // copier.copy(userFrom, userTo, null);
        BeanUtils.copyProperties(userFrom, userTo);

        System.out.println(JSON.toJSONString(userFrom));
        System.out.println(JSON.toJSONString(userTo));
    }
}
