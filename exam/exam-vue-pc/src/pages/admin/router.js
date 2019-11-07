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
            path: '/papers',
            name: 'papers',
            component: () => import(/* webpackChunkName: "paper" */ './views/PaperList.vue'),
        },
        {
            path: '/paper-edit/:id',
            name: 'paper-edit',
            component: () => import(/* webpackChunkName: "paper" */ './views/PaperEdit.vue'),
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
