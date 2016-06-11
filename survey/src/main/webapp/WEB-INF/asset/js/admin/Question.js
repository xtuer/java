///////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                               //
//                                          QuestionItem                                         //
//                                                                                               //
///////////////////////////////////////////////////////////////////////////////////////////////////
QuestionItem.TYPE_NORMAL = 0;
QuestionItem.TYPE_WITH_INPUT = 1;

function QuestionItem(id, content, type) {
    this.id = id;
    this.content = content;
    this.type = type || 0;
}

QuestionItem.prepareTemplates = function($questionTemplate) {
    // 1. 问题列表里单选项的模版
    QuestionItem.singleTemplateForList = $questionTemplate.find('.question-item.single');
    QuestionItem.singleTemplateForList.remove();

    // 2. 问题列表里单选项的模版, 可以输入
    QuestionItem.singleWithInputTemplateForList = $questionTemplate.find('.question-item.single-with-input');
    QuestionItem.singleWithInputTemplateForList.remove();

    // 3. 问题列表里多选项的模版
    QuestionItem.multipleTemplateForList = $questionTemplate.find('.question-item.multiple');
    QuestionItem.multipleTemplateForList.remove();

    // 4. 问题列表里多选项的模版, 可以输入
    QuestionItem.multipleWithInputTemplateForList = $questionTemplate.find('.question-item.multiple-with-input');
    QuestionItem.multipleWithInputTemplateForList.remove();

    // 5. 问题列表里的建议的模版
    QuestionItem.suggestionTemplateForList = $questionTemplate.find('.question-item.suggestion');
    QuestionItem.suggestionTemplateForList.remove();

    // 6. 编辑窗口里选项的模版
    QuestionItem.templateForEdit = $('#edit-question-dialog .question-item.template');
    QuestionItem.templateForEdit.removeClass('hide').removeClass('template').remove();
}

QuestionItem.prototype.showInQuestionList = function($question, questionId, questionType) {
    if (Question.TYPE_DESCRIPTION == questionType) {
        return;
    }

    var $questionItems = $question.find('.question-items');
    var $item;

    if (Question.TYPE_SUGGESTION == questionType) {
        $item = QuestionItem.suggestionTemplateForList.clone();
        $item.attr('data-question-item-id', this.id);
        $questionItems.append($item);
        return;
    }

    if (Question.TYPE_SINGLE == questionType && QuestionItem.TYPE_NORMAL == this.type) {
        // 单选没输入
        $item = QuestionItem.singleTemplateForList.clone();
    } else if (Question.TYPE_SINGLE == questionType && QuestionItem.TYPE_WITH_INPUT == this.type) {
        // 单选有没输入
        $item = QuestionItem.singleWithInputTemplateForList.clone();
    } else if (Question.TYPE_MULTIPLE == questionType && QuestionItem.TYPE_NORMAL == this.type) {
        // 多选没输入
        $item = QuestionItem.multipleTemplateForList.clone();
    } else if (Question.TYPE_MULTIPLE == questionType && QuestionItem.TYPE_WITH_INPUT == this.type) {
        // 多选有输入
        $item = QuestionItem.multipleWithInputTemplateForList.clone();
    }

    $item.attr('data-question-item-id', this.id);
    $item.find('span').text(this.content);
    $item.find('input[type="radio"]').attr('name', 'question_' + questionId); // 使得单选时只有一个能被选中
    $questionItems.append($item);
}

QuestionItem.prototype.showInEditDialog = function() {
    var $questionItems = $('#edit-question-dialog .question-items');
    var $item = QuestionItem.templateForEdit.clone();
    $item.find('input.content').val(this.content);
    QuestionItem.setQuestionItemTypeInEditDialog($item, this.type);

    $questionItems.append($item);
}

QuestionItem.setQuestionItemTypeInEditDialog = function($questionItem, type) {
    var name = '';
    var className = 'btn-default';

    if (QuestionItem.TYPE_NORMAL == type) {
        name = '普通';
    } else if (QuestionItem.TYPE_WITH_INPUT == type) {
        name = '输入';
        className = 'btn-info'
    } else {
        console.log('选项类型不可识别: ' + type);
        return;
    }

    $questionItem.attr('data-question-item-type', type);
    $questionItem.find('.question-item-type-button')
        .html(name + ' <span class="caret"></span>')
        .removeClass('btn-info').addClass(className);
}

///////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                               //
//                                            Question                                           //
//                                                                                               //
///////////////////////////////////////////////////////////////////////////////////////////////////
Question.TYPE_SINGLE = 1;
Question.TYPE_MULTIPLE = 2;
Question.TYPE_SUGGESTION = 3;
Question.TYPE_DESCRIPTION = 4;

/**
 * 问题的构造函数
 *
 * @param id 问题的 ID，为整数
 * @param content 问题的内容
 * @param type 问题的类型，为 1 时是单选，为 2 时是多选
 * @param items 问题的选项，为 QuestionItem 数组
 * @param order 问题的顺序，为浮点数，默认为 0
 */
function Question(id, content, type, items, order) {
    this.id = id;
    this.content = content;
    this.type = type;
    this.items = items;
    this.order = order || 1000;
    self = this;
}

/**
 * 提取模版，并把他们从 DOM 里删除.
 * 在 HTML 里定义模版，然后提取出来，使用它们来创建新的元素，
 * 就可以不用在 JavaScript 里用字符串拼接 HTML 元素了.
 */
Question.prepareTemplates = function() {
    // 1. 问题列表里问题的模版
    Question.questionTemplateForList = $('#question-list .question.template');
    Question.questionTemplateForList.remove();
    Question.questionTemplateForList.removeClass('hide');
    Question.questionTemplateForList.removeClass('template');

    // 2. 读取 item 的模版
    QuestionItem.prepareTemplates(Question.questionTemplateForList);
}

/**
 * 在问题列表里显示问题，如果已经存在则替换，如果不存在则追加到列表末尾
 */
Question.prototype.showInQuestionList = function() {
    var $question = Question.findQuestionFromQuestionList(this.id);
    var notFound = ($question.length == 0);

    if (notFound == true) {
        // 不存在就新创建.
        $question = Question.questionTemplateForList.clone();
    } else {
        // 存在则删除选项(因为要新建选项)
        $question.find('.question-items').html('');
    }

    $question.attr('data-question-id', this.id);
    $question.attr('data-question-type', this.type);
    $question.find('.question-content').html(this.content);
    $question.find('input.order').val(this.order);

    // 构建选项
    // type 为 TYPE_SUGGESTION 和 TYPE_DESCRIPTION
    if (this.items.length == 0) {
        new QuestionItem(0, '', 0).showInQuestionList($question, this.id, this.type);
    } else {
        for (var i = 0; i < this.items.length; ++i) {
            this.items[i].showInQuestionList($question, this.id, this.type);
        }
    }

    if (notFound) {
        $('#question-list').append($question);
    }

    reloadPlugins();
}

/**
 * 在编辑对话框里显示问题
 */
Question.prototype.showInEditDialog = function() {
    var $dialogBody = $('#edit-question-dialog .modal-body');
    $dialogBody.attr('data-question-id', this.id);
    $dialogBody.find('.question input').val(this.content);
    Question.setQuestionTypeInEditDialog(this.type);

    // 删除所有的 item
    $('#edit-question-dialog .question-item').remove();

    // 显示所有的 item
    for (var i = 0; i < this.items.length; ++i) {
        this.items[i].showInEditDialog();
    }

    $('#edit-question-dialog .alert').hide(); // 隐藏错误信息
    $('#edit-question-dialog').modal('show'); // 显示编辑对话框
}

Question.prototype.saveOrUpdate = function() {
    if (0 == this.id) {
        this.insert();
    } else {
        this.update();
    }
}

Question.prototype.insert = function() {
    if (!this.validateForEdit()) {
        return;
    }

    var self = this;
    Utils.restCreate(Urls.REST_QUESTIONS.format({topicId: Topic.getTopicId()}), self.jsonData(), function(result) {
        if (result.success) {
            self.id = result.data;
            self.showInQuestionList();
            $('#edit-question-dialog').modal('hide'); // 隐藏编辑对话框
        } else {
            Question.showEditAlert(result.message);
        }
    }, function(error) {
        Question.showEditAlert(error.responseText);
    });
}

Question.prototype.update = function() {
    if (!this.validateForEdit()) {
        return;
    }

    var self = this;
    var url = Urls.REST_QUESTIONS_WITH_ID.format({topicId: Topic.getTopicId(), questionId: self.id});
    Utils.restUpdate(url, self.jsonData(), function(result) {
        if (result.success) {
            self.showInQuestionList();
            $('#edit-question-dialog').modal('hide'); // 隐藏编辑对话框
        } else {
            Question.showEditAlert(result.message);
        }
    }, function(error) {
        Question.showEditAlert(error.responseText);
    });
}

Question.updateOrders = function(topicId, orders) {
    Utils.restUpdate(Urls.REST_QUESTIONS_ORDERS.format({topicId: topicId}), orders, function(result) {
        if (!result.success) {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

Question.prototype.remove = function() {
    var self = this;
    var url = Urls.REST_QUESTIONS_WITH_ID.format({topicId: Topic.getTopicId(), questionId: self.id});

    Utils.restDelete(url, {}, function(result) {
        if (result.success) {
            Question.findQuestionFromQuestionList(self.id).remove();
        } else {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

Question.prototype.serialize = function() {
    return JSON.stringify({
        id: this.id,
        content: this.content,
        topicId: Topic.getTopicId(),
        type: this.type,
        items: this.items
    });
}

Question.prototype.jsonData = function() {
    return {
       id: this.id,
       content: this.content,
       topicId: Topic.getTopicId(),
       type: this.type,
       items: this.items
   };
}

/**
 * 验证问题的合法性:
 *    问题的标题不能为空
 *    必须有选项
 *    选项不能为空
 *
 * @return 如果问题合法，返回 Json 对象 {valid: true, ''}，
 *         如果问题不合法，返回 Json 对象 {valid: false, '具体原因'}
 */
Question.prototype.validate = function() {
    var result = { valid: true, message: ''};

    if ('' == this.content) {
        result = { valid: false, message: '问题不能为空'};
        return result;
    }

    // 如果是建议，不需要验证选项
    if (Question.TYPE_SUGGESTION == this.type || Question.TYPE_DESCRIPTION == this.type) {
        return result;
    }

    if (0 == this.items.length) {
        result = { valid: false, message: '不能没有选项'};
        return result;
    }

    for (var i = 0; i < this.items.length; ++i) {
        if ('' == this.items[i]) {
            result = { valid: false, message: '不能有空的选项'};
            return result;
        }
    }

    return result;
}

Question.prototype.validateForEdit = function() {
    var result = this.validate();

    // 如果问题不合法，则显示错误信息
    if (!result.valid) {
        Question.showEditAlert(result.message);
    }

    return result.valid;
}

/**
 * 设置对话框中问题的类型，如果 type 是 3，表示类型为建议，需要把所有的选项删除
 *
 * @param type 问题的类型，值为 1, 2 或者 3
 */
Question.setQuestionTypeInEditDialog = function(type) {
    var name = '';
    var shouldRemoveItems = false;

    if (Question.TYPE_SINGLE == type) {
        name = '单选';
    } else if (Question.TYPE_MULTIPLE == type) {
        name = '多选';
    } else if (Question.TYPE_SUGGESTION == type) {
        name = '问答';
        shouldRemoveItems = true;
    } else if (Question.TYPE_DESCRIPTION == type) {
        name = '描述';
        shouldRemoveItems = true;
    } else {
        console.log('问题类型不可识别: ' + type);
        return;
    }

    var $dialogBody = $('#edit-question-dialog .modal-body');
    $dialogBody.find('.question .question-type')
        .html(name + ' <span class="caret"></span>')
        .attr('data-question-type', type);

    if (shouldRemoveItems) {
        $dialogBody.find('.question-item').remove();
    }
}

/**
 * 使用 id 从 question 列表里查找 question
 *
 * @param id Question 的 id
 * @return Question 的 HTML 元素
 */
Question.findQuestionFromQuestionList = function(id) {
    return $('#question-list .question[data-question-id="' + id + '"]');
}

/**
 * 从编辑对话框提取出问题
 *
 * @return Question 对象
 */
Question.extractQuestionFromEditDialog = function() {
    var $dialogBody = $('#edit-question-dialog .modal-body');
    var id = $dialogBody.attr('data-question-id');
    var content = $.trim($dialogBody.find('.question input').val());
    var type = $dialogBody.find('.question .question-type').attr('data-question-type');
    var items = [];

    $('#edit-question-dialog .question-item').each(function() {
        var itemId = 0;
        var itemContent = $.trim($('input.content', $(this)).val());
        var itemType = $(this).attr('data-question-item-type');

        items.push(new QuestionItem(itemId, itemContent, itemType));
    });

    // 输入的情况下是没有 item的，给创建一个方便以后使用
    if (type == Question.TYPE_SUGGESTION) {
        items.push(new QuestionItem(0, '', 0));
    }

    return new Question(id, content, type, items);
}

/**
 * 从 Question 列表提取出问题
 *
 * @button 编辑或者删除按钮
 * @return Question 对象
 */
Question.extractQuestionFromQuestionList = function($question) {
    var id = $question.attr('data-question-id');
    var type = $question.attr('data-question-type');
    var content = $question.find('.question-content').html();
    var items = [];

    // 只有单选或则多选才有选项
    if (Question.TYPE_SINGLE == type || Question.TYPE_MULTIPLE == type) {
        $question.find('.question-item').each(function() {
            var itemId = 0;
            var itemContent = $(this).find('span').text();
            var itemType = $(this).attr('data-question-item-type');

            items.push(new QuestionItem(itemId, itemContent, itemType));
        });
    }

    return new Question(id, content, type, items);
}

Question.queryTopicAndQuestionsByTopicId = function(topicId) {
    // 请求 topic
    Utils.restGet(Urls.REST_TOPICS_WITH_ID.format({topicId: topicId}), {}, function(topic) {
        new Topic(topic.id, topic.content, topic.url, topic.forceComplete).showInPanelHeading();
    }, function(error) {
        if (200 == error.status) {
            $('body').empty();
            window.location.href = Urls.ERROR_404;
        } else {
            Utils.showError(error.responseText);
        }
    });

    // 请求 topic's questions
    Utils.restGet(Urls.REST_QUESTIONS.format({topicId: topicId}), {}, function(questions) {
        for (var i = 0; i < questions.length; ++i) {
            var q = questions[i];
            var items = [];

            for (var j = 0; j < q.items.length; ++j) {
                var item = q.items[j];
                items.push(new QuestionItem(item.id, item.content, item.type));
            }

            new Question(q.id, q.content, q.type, items, q.order).showInQuestionList();
        }

        reloadPlugins();
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

/**
 * 显示编辑对话框里的 alert 信息
 */
Question.showEditAlert = function(message) {
    $('#edit-question-dialog .alert p').text(message);
    $('#edit-question-dialog .alert').slideDown();
}

function reloadPlugins() {
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    $('[data-toggle="tooltip"]').tooltip({container: 'body'});
}
