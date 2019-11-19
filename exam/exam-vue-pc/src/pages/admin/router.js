import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home,
        },
        {
            // 试卷列表
            path: '/papers',
            name: 'papers',
            component: () => import(/* webpackChunkName: "paper" */ './views/Papers.vue'),
        },
        {
            path: '/paper-edit/:id',
            name: 'paper-edit',
            component: () => import(/* webpackChunkName: "paper" */ './views/PaperEdit.vue'),
        },
        {
            // 考试列表
            path: '/exams',
            name: 'exams',
            component: () => import(/* webpackChunkName: "paper" */ './views/Exams.vue'),
        },
        {
            // 用户的考试
            path: '/user/:userId/exam/:examId',
            name: 'user-exam',
            component: () => import(/* webpackChunkName: "paper" */ './views/UserExam.vue'),
        },
        {
            // 用户的考试记录 (考试)
            path: '/user/:userId/exam/:examId/record/:recordId',
            name: 'user-exam-record',
            component: () => import(/* webpackChunkName: "paper" */ './views/UserExamRecord.vue'),
        },
        {
            path: '/about',
            name: 'about',
            component: () => import(/* webpackChunkName: "common" */ './views/About.vue'),
        },
        {
            // 404 页面
            path: '*',
            component: () => import(/* webpackChunkName: "common" */ '../../components/404.vue'),
        }
    ],
});
