function Topic(id, content, url) {
    this.id = id;
    this.content = content;
    this.url = url;
    self = this;
}

/**
 * Topic 的模版
 */
Topic.prepareTemplates = function() {
    Topic.topicTemplateForList = $('#topic-list .topic.template');
    Topic.topicTemplateForList.remove();
    Topic.topicTemplateForList.removeClass('template');
}

/**
 * 显示 topic 到 topic 列表.
 * 如果 topic 已存在，替换其内容
 * 如果 topic 不存在，则创建并追加到 topic 列表
 */
Topic.prototype.showInTopicList = function() {
    var $topic = Topic.findTopicFromTopicList(this.id);
    var notFound = ($topic.length == 0);

    if (notFound == true) {
        $topic = Topic.topicTemplateForList.clone();
    }

    var adminQuestionUrl = Urls.ADMIN_QUESTIONS.format({topicId: this.id});
    var surveyUrl = Urls.SURVEYS.format({surveyId: this.id});
    var surveyResultUrl = Urls.SURVEYS_RESULT.format({surveyId: this.id});

    $topic.attr('data-topic-id', this.id);
    $topic.attr('data-topic-url', this.url);
    $topic.find('.topic-content').html(this.content);
    $topic.find('.topic-content').attr('href', adminQuestionUrl);
    $topic.find('.survey-address').attr('href', surveyUrl).text('发布地址: ' + surveyUrl);
    $topic.find('.survey-result').attr('href', surveyResultUrl).text('结果统计: ' + surveyResultUrl);

    if (notFound) {
        $('#topic-list').append($topic);
    }
}

/**
 * 从 topic 列表提取出问题
 *
 * @param $button 编辑或者删除按钮
 * @return Topic 对象
 */
Topic.extractTopicFromTopicList = function($topic) {
    var id = $topic.attr('data-topic-id');
    var url = $topic.attr('data-topic-url');
    var content = $topic.find('.topic-content').html();

    return new Topic(id, content, url);
}

Topic.prototype.showInPanelHeading = function() {
    var $topic = $('#topic');
    $topic.attr('data-topic-id', this.id);
    $topic.attr('data-topic-url', this.url);
    $topic.find('.topic-content').html(this.content);
}

Topic.extractTopicFromPanelHeading = function() {
    var $topic = $('#topic');
    var id = $topic.attr('data-topic-id');
    var url = $topic.attr('data-topic-url');
    var content = $topic.find('.topic-content').html();

    return new Topic(id, content, url);
}

Topic.prototype.showInEditDialog = function() {
    $('#edit-topic-dialog .modal-body').attr('data-topic-id', this.id);
    CKEDITOR.instances.topicContentEditor.setData(this.content);
    $('#edit-topic-dialog .modal-body input').val(this.url);
    $('#edit-topic-dialog .alert').hide();
    $('#edit-topic-dialog').modal('show');
}

/**
 * 从编辑对话框提取出 topic
 *
 * @return Topic 对象
 */
Topic.extractTopicFromEditDialog = function() {
    var $modalBody = $('#edit-topic-dialog .modal-body');
    var id = $modalBody.attr('data-topic-id');
    var url = $.trim($modalBody.find('input').val());
    var content = CKEDITOR.instances.topicContentEditor.getData();

    return new Topic(id, content, url);
}

Topic.prototype.saveOrUpdate = function() {
    if (0 == this.id) {
        this.insert(); // 保存
    } else {
        this.update(); // 更新
    }
}

/**
 * 服务器成功插入 topic，保存 topic id, 则显示到 topic 到 panel heading,
 * 并且显示天津问题按钮
 */
Topic.prototype.insert = function() {
    // 服务器端创建 topic 成功, 进入编辑 topic 的问题页面
    Utils.restCreate(Urls.TOPICS, {content: this.content}, function(result) {
        if (result.success) {
            var topicId = result.data;
            window.location.href = Urls.ADMIN_QUESTIONS.format({topicId: topicId});
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

/**
 * 更新 topic 到服务器，成功的话也同时在 panel heading 更新
 */
Topic.prototype.update = function() {
    if (!this.validateForEdit()) {
        return;
    }

    var self = this;

    Utils.restUpdate(Urls.TOPICS_WITH_ID.format({topicId: self.id}),
        {id: self.id, content: self.content, url: self.url},
        function(result) {
            if (result.success) {
                self.showInPanelHeading();
                $('#edit-topic-dialog').modal('hide'); // 隐藏编辑对话框
            } else {
                Topic.showEditAlert(result.description);
            }
        }, function(error) {
            Utils.showError(error.responseText);
        }
    );

//    $.ajax({
//        url: Urls.topic.replace('{id}', this.id),
//        type: 'PUT',
//        dataType: 'json',
//        contentType: 'application/json', // 1. 少了就会报错
//        data: this.serialize() // 2. data 需要序列化一下
//    })
//    .done(function(result) {
//        if (result.success) {
//            self.showInPanelHeading();
//            $('#edit-topic-dialog').modal('hide'); // 隐藏编辑对话框
//        } else {
//            Topic.showEditAlert(result.description);
//        }
//    })
//    .fail(function(error) {
//        Topic.showEditAlert(result.description);
//    });
}

/**
 * 从数据库删除 topic，并同时从 topic 列表删除
 */
Topic.prototype.remove = function() {
    var topicId = this.id;

    Utils.restDelete(Urls.TOPICS_WITH_ID.format({topicId: topicId}), {}, function(result) {
        if (result.success) {
            Topic.findTopicFromTopicList(topicId).remove();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

/**
 * 更新 topic 的顺序
 */
Topic.updateOrders = function(orders) {
    Utils.restUpdate(Urls.TOPICS_ORDERS, orders, function(result) {
        if (!result.success) {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

/**
 * 序列化 topic
 */
Topic.prototype.serialize = function() {
    return JSON.stringify({id: this.id, content: this.content, url: this.url});
}

/**
 * 验证 topic 的合法性:
 *    问卷的标题内容不能为空
 *
 * @return 如果问卷合法，返回 Json 对象 {valid: true, ''}，
 *         如果问卷不合法，返回 Json 对象 {valid: false, '具体原因'}
 */
Topic.prototype.validate = function() {
    var result = { valid: true, message: ''};

    if ('' == this.content) {
        result = { valid: false, message: '调查问卷的标题内容不能为空'};
    }

    return result;
}

/**
 * 验证 topic 的合法性，如果不合格，则在编辑对话框里显示错误信息
 */
Topic.prototype.validateForEdit = function() {
    var result = this.validate();

    // 如果问题不合法，则显示错误信息
    if (!result.valid) {
        Topic.showEditAlert(result.message);
    }

    return result.valid;
}

/**
 * 显示编辑对话框里的 alert 信息
 */
Topic.showEditAlert = function(message) {
    $('#edit-topic-dialog .alert p').text(message);
    $('#edit-topic-dialog .alert').slideDown();
}

/**
 * 使用 id 从 topic 列表里查找 topic
 *
 * @param id Topic 的 id
 * @return Topic 的 HTML 元素
 */
Topic.findTopicFromTopicList = function(id) {
    return $('#topic-list .topic[data-topic-id="' + id + '"]');
}

/**
 * 使用弹出对话框显示信息
 */
function showInformation(information) {
    $('#information-dialog .modal-body p').text(information);
    $('#information-dialog').modal('show');
}

/**
 * 从服务器请求所有的 topics 并显示到 topic 列表
 */
Topic.queryTopics = function() {
    $.ajax({
        url: Urls.TOPICS, // 访问一下看看具体的格式
        type: 'GET',
        dataType: 'json',
        data: {},
    })
    .done(function(topics) {
        for (var i = 0; i < topics.length; ++i) {
            var topic = new Topic(topics[i].id, topics[i].content, topics[i].url);
            topic.showInTopicList();
        }

        $('[data-toggle="tooltip"]').tooltip(); // 重新生成 tooltip
    })
    .fail(function(error) {
        showInformation(error.responseText);
    });
}

/**
 * 隐藏添加问题按钮
 */
Topic.hideInsertQuestionButton = function() {
    $('#insert-question-button').hide();
}

/**
 * 显示添加问题按钮
 */
Topic.showInsertQuestionButton = function() {
    $('#insert-question-button').show();
}

/**
 * 从 URL 中获取 topic's id
 *
 * @return topic's id
 */
Topic.getTopicId = function() {
    var topicId = 0; // 默认值
    var href = window.location.href;
    var regex = /admin\/topics\/(\d+)\/questions/;

    href.replace(regex, function() {
        topicId = arguments[1];
    });

    return topicId;
}
