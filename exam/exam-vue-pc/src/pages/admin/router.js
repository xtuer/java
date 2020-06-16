import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import(/* webpackChunkName: "common" */ './views/Home.vue'),
            redirect: { name: 'about' },

            children: [
                {
                    path: '/papers',
                    name: 'papers',
                    component: () => import(/* webpackChunkName: "exam" */ './views/Papers.vue'),
                },
                {
                    path: '/exams',
                    name: 'exams',
                    component: () => import(/* webpackChunkName: "exam" */ './views/Exams.vue'),
                },
                {
                    path: '/correct-papers',
                    name: 'correct-papers',
                    component: () => import(/* webpackChunkName: "exam" */ './views/CorrectPapers.vue'),
                },
                {
                    path: '/correct-questions',
                    name: 'correct-questions',
                    component: () => import(/* webpackChunkName: "exam" */ './views/CorrectQuestions.vue'),
                },
                {
                    path: '/exams',
                    name: 'exams',
                    component: () => import(/* webpackChunkName: "exam" */ './views/Exams.vue'),
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
