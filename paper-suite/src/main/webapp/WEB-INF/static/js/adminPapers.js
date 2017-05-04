/*-----------------------------------------------------------------------------|
 |                                 Class Paper                                 |
 |----------------------------------------------------------------------------*/
function Paper() {

}

Paper.search = function(subject, nameFilter) {
    var chooser = window.vueChoosePapers;
    $.rest.get({url: Urls.REST_PAPERS_SEARCH, data: {subject: subject, nameFilter: nameFilter}, success: function(result) {
        if (!result.success) {
            layer.msg(result.message);
            return;
        }

        // 1. Vue 更新试卷的数据，Vue 会同时更新试卷的 DOM
        // 2. Vue 更新完试卷的 DOM 后调用 checkbox() 使得新的 checkbox 支持完全的功能
        chooser.papers = result.data;
        chooser.$nextTick(function() {
            $('#vue-choose-papers .checkbox').checkbox();
        });
    }});
};

/*-----------------------------------------------------------------------------|
 |                             require entry point                             |
 |----------------------------------------------------------------------------*/
require(['jquery', 'vue', 'layer', 'semanticUi', 'ztree', 'pagination', 'rest', 'urls', 'util'], function($, Vue) {
    Util.activateSidebarItem(1);
    Tree.init(); // 初始化目录树

    /*-----------------------------------------------------------------------------|
    |                                   试卷列表                                     |
    |-----------------------------------------------------------------------------*/
    window.vuePapers = new Vue({
        el: '#vue-papers',
        data: {
            papers: []
        },
        watch: {
            papers: function(newValue, oldValue) {
                // papers 的数据变化后，DOM 更新完成时调用下面的函数
                this.$nextTick(function() {
                    $('#vue-papers .dimmable').dimmer({on: 'hover'}); // 鼠标放到 dimmable 上时显示 dimmer
                    $('#vue-papers .paper-name').popup({position: 'bottom left'}); // 初始化 popup
                });
            }
        },
        methods: {
            foo: function() {
                alert('image clicked');
            }
        }
        // 更新渲染后调用 updated()
        // updated: function() {
        //     $('.paper-name').popup({position: 'bottom left'});
        // }
    });

    window.vuePapers.papers.push({paperId: 1, name: 'Soul', originalName: '09-12年安徽省合肥一中冲刺高考最后一卷(理数).doc'});
    window.vuePapers.papers.push({paperId: 2, name: 'Google'});

    /*-----------------------------------------------------------------------------|
    |                                    选择试卷                                   |
    |-----------------------------------------------------------------------------*/
    window.vueChoosePapers = new Vue({
        el: '#vue-choose-papers',
        data: {
            nameFilter: '',
            subject: '高中数学', // 默认选中高中数学
            papers: []
        },
        methods: {
            // 动态计算 attached segment 的 class
            segmentClass: function(index) {
                if (index === 0) {
                    return 'ui top attached segment';
                } else if (index === this.papers.length - 1) {
                    return 'ui bottom attached segment';
                } else {
                    return 'ui attached segment';
                }
            },
            // 使用学科和试卷名字的部分查找试卷
            findPapers: function() {
                Paper.search(this.subject, $.trim(this.nameFilter));
            }
        }
    });

    // 弹出选择试卷对话框
    $('#popupChoosePapersDialogButton').click(function(event) {
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0,
            area: ['620px', '440px'], //宽高
            content: $('#choose-papers-dialog'),
            btn: ['添加', '取消'],
            btn1: function() {
                // 取得选中的试卷，添加到当前目录，选择时从后向前遍历
                var papers = window.vueChoosePapers.papers;
                for (var i = papers.length-1; i >= 0; --i) {
                    if (papers[i].checked) {
                        window.vuePapers.papers.push(papers[i]);
                        papers.splice(i, 1);
                    }
                }
            }
        });
    });

    $('.pagination').jqPagination({
        max_page: 30, // 总页数
        page_string: '{max_page} 页之 {current_page}', // 页数显示样式
        paged: function(page) {
            alert(page);
        }
    });

    $('.dropdown').dropdown();
});
