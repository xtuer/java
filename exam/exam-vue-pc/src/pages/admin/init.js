import Vue from 'vue';
import { LoadingBar, Message, Notice } from 'view-design';

import '@/../public/static/js/urls';
import '@/../public/static/js/utils';
import '@/../public/static/js/constants';
import '@/../public/static/lib/axios.rest';

import filters from '@/../public/static/js/filters';
import methods from '@/../public/static/js/methods';
import UserDao from '@/../public/static/js/dao/UserDao';
import PaperDao from '@/../public/static/js/dao/PaperDao';
import ExamDao from '@/../public/static/js/dao/ExamDao';
import QuestionUtils from '@/../public/static/js/QuestionUtils';
import RichText from '@/components/RichText.vue';
import Question from '@/components/Question.vue';

// [1] 注册 Vue 的原型函数
Object.keys(methods).forEach((key) => {
    Vue.prototype[key] = methods[key];
});

// [2] 注册全局过滤器
Object.keys(filters).forEach(key => {
    Vue.filter(key, filters[key]);
});

// [3] 注册 Dao，工具类
window.UserDao  = UserDao;
window.PaperDao = PaperDao;
window.ExamDao  = ExamDao;
window.QuestionUtils = QuestionUtils;

// [4] 注册全局组件
Vue.component('RichText', RichText);
Vue.component('Question', Question);

// [5] 定义全局的 Message, Notice，为了能够在 Dao 中使用
window.LoadingBar = LoadingBar;
window.Message    = Message;
window.Notice     = Notice;
