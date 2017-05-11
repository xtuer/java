require.config({
    paths: {
        jquery    : '//cdn.bootcss.com/jquery/1.9.1/jquery.min',
        layer     : '//cdn.staticfile.org/layer/2.3/layer',
        vue       : '//cdn.staticfile.org/vue/2.0.3/vue',
        semanticUi: '/lib/semantic/semantic.min',
        semanticUiCalendar: '/lib/semantic/calendar/calendar.min',
        ztree     : '//cdn.staticfile.org/zTree.v3/3.5.28/js/jquery.ztree.all.min',
        pagination: '/lib/jquery.jqpagination.min',
        rest      : '/lib/jquery.rest',
        urls      : '/js/urls',
        util      : '/js/util'
    },
    shim: {
        layer: {
            deps: ['jquery', 'css!//cdn.staticfile.org/layer/2.3/skin/layer.css']
        },
        semanticUi: {
            deps: ['jquery']
        },
        semanticUiCalendar: {
            deps: ['semanticUi', 'css!/lib/semantic/calendar/calendar.min.css']
        },
        ztree: {
            deps: ['jquery',
                   'css!//cdn.staticfile.org/font-awesome/4.7.0/css/font-awesome.min.css',
                   'css!//cdn.staticfile.org/zTree.v3/3.5.28/css/awesomeStyle/awesome.min.css',
                   'css!/css/ztree-awesome-custom.css']
        },
        pagination: {
            deps: ['jquery', 'css!/lib/jqpagination.css']
        },
        rest: {
            deps: ['jquery']
        },
        util: {
            deps: ['jquery']
        }
    },
    map: {
        '*': {
            css: '/lib/css.min.js'
        }
    }
});
