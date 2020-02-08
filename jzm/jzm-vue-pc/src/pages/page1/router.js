import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import(/* webpackChunkName: "common" */ './views/Home.vue'),
            redirect: { name: 'order' },

            children: [
                {
                    path: '/about',
                    name: 'about',
                    component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
                },
                {
                    path: '/order',
                    name: 'order',
                    component: () => import(/* webpackChunkName: "about" */ './views/Order.vue'),
                },
                {
                    path: '/scroll',
                    name: 'scroll',
                    component: () => import(/* webpackChunkName: "about" */ './views/Scroll.vue'),
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
