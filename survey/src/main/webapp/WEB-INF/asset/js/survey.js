function Answer(id, topicId, questionId, questionItemId, content) {
    this.id = id;
    this.topicId = topicId;
    this.questionId = questionId;
    this.questionItemId = questionItemId;
    this.content = content;
}

Answer.extractQuestionAnswers = function($question) {
    var answers = [];
    var topicId = $('body').attr('data-topic-id');
    var questionId = $question.attr('data-question-id');

    $('.question-item', $question).each(function() {
        var $questionItem = $(this);
        var questionItemId = $questionItem.attr('data-question-item-id');

        // 单选或多选题选中的答案
        $('input:checked', $questionItem).each(function() {
            var content = $.trim($('input.extra-input', $questionItem).val());
            answers.push(new Answer(0, topicId, questionId, questionItemId, content));
        });

        // 输入提醒的答案
        $('textarea', $questionItem).each(function() {
            var content = $.trim($(this).val());
            if (content) {
                answers.push(new Answer(0, topicId, questionId, questionItemId, content));
            }
        });
    });

    return answers;
}

Answer.extractAllQuestionsAnswers = function() {
    var answers = [];
    var canSubmit = true;
    var $questions = $('#question-list');

    // 提取选择题的答案
    $('.question[data-question-type="1"], .question[data-question-type="2"]', $questions).each(function() {
        var $question = $(this);
        var tempAnswers = Answer.extractQuestionAnswers($question);

        if (tempAnswers.length > 0) {
            answers = answers.concat(tempAnswers);
        } else {
            canSubmit = false;
        }
    });

    // 如果输入题有输入内容，则提取答案
    $('.question[data-question-type="3"]', $questions).each(function() {
        var $question = $(this);
        var tempAnswers = Answer.extractQuestionAnswers($question);

        if (tempAnswers.length > 0) {
            answers = answers.concat(tempAnswers);
        }
    });

    return {
        "canSubmit": canSubmit,
        "answers": answers
    };
}

$(document).ready(function() {
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    // 防止点击 input 输入的时候事件传播到上面的 checkbox
    $('input').click(function(e) {
        e.stopPropagation();
    });

    $('#survey-submit-form').submit(function() {
        var result = Answer.extractAllQuestionsAnswers();

        // 条件不满足，不能提交
        if (!result.canSubmit) {
            Utils.showError('还有选择题没有回答<br>请先回答完所有的选择题后再提交');
            return false;
        }
        // 设置表单数据，提交表单
        $('input', $(this)).val(JSON.stringify(result.answers));
    });
});
