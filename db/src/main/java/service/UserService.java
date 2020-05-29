package service;

import bean.User;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserMapper userMapper;

    public void greet() {
        System.out.println("greet: " + idWorker.nextId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertUsers(List<User> users) {
        for (User user : users) {
            userMapper.insertUser(user);
        }
    }

    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    public User findUserById(long id) {
        return userMapper.findUserById(id);
    }

    public User newUser() {
        long id = idWorker.nextId();

        User user = new User()
                .setId(id)
                .setUsername("TempUser:" + id)
                .setNickname("TempUser")
                .setPassword("----")
                .setRole(User.Role.ADMIN);

        return user;
    }
}
