/*-----------------------------------------------------------------------------|
 |                             require entry point                             |
 |----------------------------------------------------------------------------*/
require(['jquery', 'vue', 'layer', 'semanticUi', 'ztree', 'pagination', 'rest', 'urls', 'util'], function($, Vue) {
    Util.activateSidebarItem(1);
    Tree.init(); // 初始化目录树

    $('#popupPapersDialogButton').click(function(event) {
        layer.open({
            type: 1,
            // title: '知识点分类管理',
            title: false,
            closeBtn: 0,
            area: ['620px', '540px'], //宽高
            content: $('#papers-dialog'),
            btn: ['添加', '取消']
        });
    });

    $('.pagination').jqPagination({
            max_page: 30, // 总页数
            page_string: '{max_page} 页之 {current_page}', // 页数显示样式
            paged: function(page) {
                alert(page);
            }
        });

    var vueSegments = new Vue({
        el: '#vue-segments',
        data: {
            items: [{
                text: '<i class="icon tag"></i> This segment is on top'
            }, {
                text: 'This method is a shortcut for .on( "mouseenter", handler ) in the first two variations, and .trigger( "mouseenter" ) in the third.'
            }, {
                text: 'A function to execute each time the event is triggered.'
            }, {
                text: 'This segment is on bottom'
            }]
        },
        methods: {
            // 动态计算 segment 的 class
            segmentClass: function(index) {
                if (index === 0) {
                    return 'ui top attached segment';
                } else if (index === this.items.length - 1) {
                    return 'ui bottom attached segment';
                } else {
                    return 'ui attached segment';
                }
            }
        }
    });

    for (var i = 0; i < 20; ++i) {
        vueSegments.items.push({text: 'This segment is on bottom'});
    }

    /*-----------------------------------------------------------------------------|
    |                                    File                                      |
    |-----------------------------------------------------------------------------*/
    var vueFiles = new Vue({
        el: '#vue-files',
        data: {
            files: [{
                fileId: 1,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 2,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 3,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 4,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 5,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 6,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 7,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 8,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 9,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 10,
                image: '/img/default.jpg',
                name: 'Kristy'
            }, {
                fileId: 11,
                image: '/img/default.jpg',
                name: 'Kristy'
            }]
        }
    });
});
