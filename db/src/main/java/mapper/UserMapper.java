package mapper;

import bean.User;

public interface UserMapper {
    void insertUser(User user);

    User findUserById(long id);
}
