import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import(/* webpackChunkName: "common" */ './views/Home.vue'),
            redirect: { name: 'product-items' },

            children: [
                // 产品项管理
                {
                    path: '/product-items',
                    name: 'product-items',
                    component: () => import(/* webpackChunkName: "product" */ './views/ProductItemManage.vue'),
                },
                // 产品管理
                {
                    path: '/products',
                    name: 'products',
                    component: () => import(/* webpackChunkName: "product" */ './views/ProductManage.vue'),
                },
                {
                    path: '/about',
                    name: 'about',
                    component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
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
