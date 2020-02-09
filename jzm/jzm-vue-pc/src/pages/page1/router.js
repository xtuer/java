import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import(/* webpackChunkName: "common" */ './views/Home.vue'),
            redirect: { name: 'spares' },

            children: [
                {
                    path: '/about',
                    name: 'about',
                    component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
                },
                {
                    path: '/orders',
                    name: 'orders',
                    component: () => import(/* webpackChunkName: "about" */ './views/Orders.vue'),
                },
                {
                    path: '/spares',
                    name: 'spares',
                    component: () => import(/* webpackChunkName: "about" */ './views/Spares.vue'),
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
