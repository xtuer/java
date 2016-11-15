package com.xtuer.formatter;

import com.xtuer.bean.User;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

// Formatter 是全局的: 字符串和对象互相转换
public class UserFormatter implements Formatter<User> {
    @Override
    public User parse(String text, Locale locale) throws ParseException {
        String[] components = text.split(",");
        User user = new User();
        user.setUsername(components[0].trim());
        user.setAge(Integer.parseInt(components[1].trim()));

        return user;
    }

    @Override
    public String print(User object, Locale locale) {
        return null;
    }
}
