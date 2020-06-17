import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import(/* webpackChunkName: "common" */ './views/Home.vue'),
            redirect: { name: 'papers' },

            children: [
                {
                    // 试卷管理
                    path: '/papers',
                    name: 'papers',
                    component: () => import(/* webpackChunkName: "exam" */ './views/Papers.vue'),
                },
                {
                    // 考试管理
                    path: '/exams',
                    name: 'exams',
                    component: () => import(/* webpackChunkName: "exam" */ './views/Exams.vue'),
                },
                {
                    // 整卷批改
                    path: '/correct-papers',
                    name: 'correct-papers',
                    component: () => import(/* webpackChunkName: "exam" */ './views/CorrectPapers.vue'),
                },
                {
                    // 逐题批改
                    path: '/correct-questions',
                    name: 'correct-questions',
                    component: () => import(/* webpackChunkName: "exam" */ './views/CorrectQuestions.vue'),
                },
                {
                    // 试卷编辑
                    path: '/papers/:paperId/edit',
                    name: 'paper-edit',
                    component: () => import(/* webpackChunkName: "exam" */ './views/PaperEdit.vue'),
                },
                {
                    path: '/about',
                    name: 'about',
                    component: () => import(/* webpackChunkName: "common" */ './views/About.vue'),
                },
                {
                    path: '/scroll',
                    name: 'scroll',
                    component: () => import(/* webpackChunkName: "common" */ './views/Scroll.vue'),
                },
            ]
        },
        {
            // 404 页面
            path: '*',
            component: () => import(/* webpackChunkName: "common" */ '../../components/404.vue'),
        }
    ],
});
