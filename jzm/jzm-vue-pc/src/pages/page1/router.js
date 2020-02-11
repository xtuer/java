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
                    path: '/orders',
                    name: 'orders',
                    component: () => import(/* webpackChunkName: "order" */ './views/Orders.vue'),
                },
                {
                    path: '/spares',
                    name: 'spares',
                    component: () => import(/* webpackChunkName: "spare" */ './views/Spares.vue'),
                },
                {
                    path: '/warehousing-logs',
                    name: 'warehousingLogs',
                    component: () => import(/* webpackChunkName: "spare" */ './views/WarehousingLogs.vue'),
                },
                {
                    path: '/work-orders',
                    name: 'workOrders',
                    component: () => import(/* webpackChunkName: "spare" */ './views/WorkOrders.vue'),
                },
                {
                    path: '/scroll',
                    name: 'scroll',
                    component: () => import(/* webpackChunkName: "about" */ './views/Scroll.vue'),
                },
                {
                    path: '/about',
                    name: 'about',
                    component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
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
