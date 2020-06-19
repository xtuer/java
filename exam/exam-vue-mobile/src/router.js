import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        name: 'home',
        redirect: { name: 'exams' },
    },
    {
        path: '/login',
        name: 'login',
        component: () => import(/* webpackChunkName: "about" */ './views/Login.vue'),
    },
    {
        path: '/exams',
        name: 'exams',
        component: () => import(/* webpackChunkName: "about" */ './views/Exams.vue'),
    },
    {
        path: '/exams/:examId',
        name: 'exam',
        component: () => import(/* webpackChunkName: "about" */ './views/Exam.vue'),
    },
    {
        path: '/about',
        name: 'about',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
    },
];

const router = new VueRouter({
    routes,
});

export default router;
