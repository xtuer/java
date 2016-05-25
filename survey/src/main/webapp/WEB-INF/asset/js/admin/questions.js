$(document).ready(function() {
    // Topic 相关
    $('#topic .edit-topic-button').click(function(e) {
        e.preventDefault();
        var topic = Topic.extractTopicFromPanelHeading();
        topic.showInEditDialog();
    });

    $('#edit-topic-dialog .save-topic-button').click(function() {
        Topic.extractTopicFromEditDialog().saveOrUpdate();
    });

    // ----------------------------------------------------------------------------
    // Question 相关
    Question.prepareTemplates();

    if (Topic.getTopicId() == 0) {
        Topic.hideInsertQuestionButton();
    } else {
        Question.queryTopicAndQuestionsByTopicId(Topic.getTopicId());
    }

    // 添加新问题
    $('#insert-question-button').click(function() {
        // new Question(0, '你喜欢的编程语言是？', Question.TYPE_SINGLE, ['Java', 'Ruby', 'Python', '']).showInEditDialog();
        var items = [];
        items.push(new QuestionItem(0, '', 0));
        items.push(new QuestionItem(0, '', 0));
        items.push(new QuestionItem(0, '', 0));
        new Question(0, '', Question.TYPE_SINGLE, items).showInEditDialog();
    });

    // 添加新的问题选项
    $(document).on('click', '#edit-question-dialog .insert-question-item-button', function() {
        $('#edit-question-dialog .modal-body').append(QuestionItem.templateForEdit.clone());
    });

    // 删除问题的选项
    $(document).on('click', '#edit-question-dialog .remove-question-item-button', function() {
        $(this).parent().parent().remove()
    });

    // 设置问题为单选题
    $('#edit-question-dialog .modal-body a.single').click(function(e) {
        e.preventDefault();
        Question.setQuestionTypeInEditDialog(Question.TYPE_SINGLE);
    });

    // 设置问题为多选题
    $('#edit-question-dialog .modal-body a.multiple').click(function(e) {
        e.preventDefault();
        Question.setQuestionTypeInEditDialog(Question.TYPE_MULTIPLE);
    });

    // 设置问题为建议
    $('#edit-question-dialog .modal-body a.suggestion').click(function(e) {
        e.preventDefault();
        Question.setQuestionTypeInEditDialog(Question.TYPE_SUGGESTION);
    });

    // 设置问题为描述
    $('#edit-question-dialog .modal-body a.description').click(function(e) {
        e.preventDefault();
        Question.setQuestionTypeInEditDialog(Question.TYPE_DESCRIPTION);
    });

    // 设置选项为普通
    $(document).on('click', '#edit-question-dialog .modal-body .question-item a.normal', function(e) {
        e.preventDefault();
        var $questionItem = $(this).parents('.question-item');
        QuestionItem.setQuestionItemTypeInEditDialog($questionItem, QuestionItem.TYPE_NORMAL);
    });

    // 设置选项为输入
    $(document).on('click', '#edit-question-dialog .modal-body .question-item a.with-input', function(e) {
        e.preventDefault();
        var $questionItem = $(this).parents('.question-item');
        QuestionItem.setQuestionItemTypeInEditDialog($questionItem, QuestionItem.TYPE_WITH_INPUT);
    });

    // 点击编辑问题按钮，在编辑对话框里显示问题
    $(document).on('click', '#question-list .edit-question-button', function(e) {
        e.preventDefault();

        var $question = $(this).parent().parent();
        var question = Question.extractQuestionFromQuestionList($question);
        question.showInEditDialog();
    });

    // 保存问题
    $('#edit-question-dialog .modal-footer .btn-primary').click(function() {
        Question.extractQuestionFromEditDialog().saveOrUpdate();
    });

    // 点击删除问题按钮，显示删除对话框
    $(document).on('click', '#question-list .remove-question-button', function(e) {
        e.preventDefault();
        var $question = $(this).parent().parent();
        var question = Question.extractQuestionFromQuestionList($question);

        Utils.showConfirm('删除问题', '您确认要删除选中的问题吗？', function() {
            new Question(question.id, '', Question.TYPE_SINGLE, []).remove();
        });
    });

    // 拖拽结束时, 更新所有 topic 的 order
    $('#question-list').sortable({opacity: 0.5,
        update: function (event, ui) {
            var topicId = Topic.getTopicId();
            var order = 0;
            var orders = [];

            $('#question-list').children('li').each(function() {
                var questionId = $(this).attr('data-question-id');
                orders.push({questionId: questionId, order: order});
                order = order + 1;
            });

            Question.updateOrders(topicId, orders);
        }
    });

    reloadPlugins();
});
