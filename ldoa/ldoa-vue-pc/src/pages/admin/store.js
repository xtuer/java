import Vue from 'vue';
import Vuex from 'vuex';
import UserDao from '@/../public/static-p/js/dao/UserDao';
import AuditDao from '@/../public/static-p/js/dao/AuditDao';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        user: {}, // 登录用户
        messageCount: 0, // 消息数量
    },
    mutations: {
        // 设置登录用户
        setUser(state, user) {
            state.user = user;
        },
        // 设置消息数量
        setMessageCount(state, count) {
            state.messageCount = count;
        }
    },
    actions: {
        // 加载当前登陆用户
        loadCurrentUser({ commit }) {
            return UserDao.findCurrentUser().then(user => {
                commit('setUser', user);
            });
        },
        // 加载当前用户的消息数量
        loadCurrentUserMessageCount({ state, commit }) {
            const userId = state.user.userId; // 当前登陆用户的 ID
            AuditDao.countWaitingAuditStepsByUserId(userId).then(count => {
                commit('setMessageCount', count);
            });
        },
    },
    getters: {
        // 已登录返回 true，否则返回 false
        logined(state) {
            return state.user.userId;
        },
        // 当前登陆用户 ID
        currentUserId(state) {
            return state.user.userId;
        }
    }
});
