import Vue from 'vue';
import Router from 'vue-router';
// import Home from './views/Home.vue';

Vue.use(Router);

export default new Router({
    routes: [{
        path: '/',
        redirect: 'bd',
    },
    {
        path: '/main',
        name: 'name',
        component: () => import('./subpage/Main.vue'),
        children: [
            {
                path: '/bd',
                name: 'bd',
                component: () => import(/* webpackChunkName: "bd" */ './subpage/BD.vue'),
            },
            {
                path: '/chip-store',
                name: 'chip-store',
                component: () => import(/* webpackChunkName: "bd" */ './subpage/ChipStore.vue'),
            }
        ]
    },
    {
        path: '/about',
        name: 'about',
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        component: () => import(/* webpackChunkName: "about" */ './subpage/About.vue'),
    }],
});
