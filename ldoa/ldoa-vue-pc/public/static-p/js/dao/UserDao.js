import { userSetter } from 'core-js/fn/symbol';

/**
 * 访问用户数据的 Dao
 */
export default class UserDao {
    /**
     * 获取当前页面登录的用户
     *
     * 网址: http://localhost:8080/api/login/users/current
     * 参数: 无
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为用户对象，reject 的参数为错误信息
     */
    static findCurrentUser() {
        return Rest.get(Urls.API_USERS_CURRENT).then(({ data: user, success, message }) => {
            return Utils.response(user, success, message);
        });
    }

    /**
     * 查询 ID 为传入的 userId 的用户
     *
     * 网址: http://localhost:8080/api/users/{userId}
     * 参数: 无
     *
     * @param  {String}  userId 用户 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为用户对象，reject 的参数为错误消息
     */
    static findUserById(userId) {
        return Rest.get(Urls.API_USERS_BY_ID, { params: { userId } }).then(({ data: user, success, message }) => {
            return Utils.response(user, success, message);
        });
    }

    /**
     * 查询用户，nickname 不为空时使用 LIKE 匹配，为空时返回所有用户
     *
     * 网址: http://localhost:8080/api/users
     * 参数:
     *      nickname   [可选]: 姓名 (使用 LIKE 匹配)
     *      pageNumber [可选]: 页码
     *      pageSize   [可选]: 页码
     *
     * @param {JSON} filter 过滤条件
     * @return {Promise} 返回 Promise 对象，resolve 的参数为用户数组，reject 的参数为错误信息
     */
    static findUsers(filter) {
        return Rest.get(Urls.API_USERS, { data: filter }).then(({ data: users, success, message }) => {
            return Utils.response(users, success, message);
        });
    }

    /**
     * 创建用户
     *
     * 网址: http://localhost:8080/api/users
     * 参数: 无
     * 请求体: 用户的 JSON 字符串
     *      username (必要): 账号
     *      nickname (必要): 姓名
     *      password (必要): 密码
     *      roles    [可选]: 角色数组
     *
     * @param user 用户
     * @return {Promise} 返回 Promise 对象，resolve 的参数为用户，reject 的参数为错误信息
     */
    static createUser(user) {
        return Rest.create(Urls.API_USERS, { data: user, json: true }).then(({ data: newUser, success, message }) => {
            return Utils.response(newUser, success, message);
        });
    }

    /**
     * 更新用户的昵称、头像、手机、性别、密码。
     * 注意: 一次只能更新一个属性。
     *
     * 网址: http://localhost:8080/api/users/{userId}
     * 参数:
     *      nickname      [可选]: 昵称
     *      avatar        [可选]: 头像
     *      mobile        [可选]: 手机
     *      gender        [可选]: 性别 (0, 1, 2)
     *      oldPassword   [可选]: 旧密码
     *      newPassword   [可选]: 新密码
     *      renewPassword [可选]: 确认的密码
     *      role          [可选]: 角色
     *
     * 案例: UserDao.patchUser({ userId: 1, nickname: 'Bob' })
     *
     * 1. 更新头像成功时，data 为头像的正式 URL
     * 2. 更新其他属性成功时 data 为空，message 为对应属性更新成功提示
     *
     * @return {Promise} 返回 Promise 对象，resolve 的参数为对应更新操作的结果，reject 的参数为错误信息
     */
    static patchUser(user) {
        return Rest.patch(Urls.API_USERS_BY_ID, { params: { userId: user.userId }, data: user }).then(({ data, success, message }) => {
            return Utils.response(data, success, message, true);
        });
    }

    /**
     * 重置用户的密码
     * 网址: http://localhost:8080/api/users/{userId}/passwords/reset
     * 参数: 无
     *
     * @param userId 用户的 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static resetPassword(userId) {
        return Rest.update(Urls.API_USER_PASSWORDS_RESET, { params: { userId } }).then((success, message) => {
            return Utils.response(null, success, message);
        });
    }

    /**
     * 删除用户
     *
     * 网址: http://localhost:8080/api/users/{userId}
     * 参数: 无
     *
     * @param userId 用户 ID
     * @return {Promise} 返回 Promise 对象，resolve 的参数为无，reject 的参数为错误信息
     */
    static deleteUser(userId) {
        return Rest.del(Urls.API_USERS_BY_ID, { params: { userId } }).then(({ success, message }) => {
            return Utils.response(null, success, message);
        });
    }
}
