$(document).ready(function() {
    Topic.prepareTemplates();
    Topic.queryTopics(); // 从服务器请求所有的 topics

    // 点击创建 Topic 按钮进入创建 Topic 页面
    $('#create-topic-button').click(function() {
        var topicContent = $('#topic-content').html();
        new Topic(0, topicContent, '').insert();
    });

    // 点击删除按钮，设置删除窗口中 topic 的内容，然后弹出删除窗口
    $(document).on('click', '.remove-topic-button', function(e) {
        e.preventDefault();
        var topic = Topic.extractTopicFromTopicList($(this).parent());

        Utils.showConfirm('删除调查问卷', '您确认要删除调查问卷吗？', function() {
            topic.remove();
        });
    });

    $('[data-toggle="tooltip"]').tooltip();

    // 拖拽结束时, 更新所有 topic 的 order
    $('#topic-list').sortable({opacity: 0.5,
        update: function (event, ui) {
            var order = 0;
            var orders = [];

            $('#topic-list').children('li').each(function() {
                var topicId = $(this).attr('data-topic-id');
                orders.push({topicId: topicId, order: order});
                order = order + 1;
            });

            Topic.updateOrders(orders);
        }
    });
});

