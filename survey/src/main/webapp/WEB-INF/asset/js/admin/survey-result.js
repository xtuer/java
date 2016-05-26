$(document).ready(function() {
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    $('.load-button').click(function() {
        loadSuggestions($(this));
    });

    var topicId = $('body').attr('data-topic-id');
    var url = Urls.REST_QUESTIONS_ANSWERS_STATISTICS.format({topicId: topicId});
    Utils.restGet(url, {}, function(result) {
        // Result 的格式: [{questionId: 108, questionItemId: 0, questionItemAnswersCount: 1}]
        var questionAnswersCountArray = {};

        // 计算没个问题回答的总数
        for (var i = 0; i < result.length; ++i) {
            var questionId = result[i].questionId;
            var itemAnswersCount = result[i].questionItemAnswersCount;

            if (questionAnswersCountArray.hasOwnProperty(questionId)) {
                questionAnswersCountArray[questionId] += itemAnswersCount;
            } else {
                questionAnswersCountArray[questionId] = itemAnswersCount;
            }
        }

        // 计算每个选项回答的情况
        for (var i = 0; i < result.length; ++i) {
            var questionId = result[i].questionId;
            var questionItemId = result[i].questionItemId;
            var itemAnswersCount = result[i].questionItemAnswersCount;
            var questionAnswersCount = questionAnswersCountArray[questionId];
            var $progressBar = findProgressBar(questionId, questionItemId);

            if (questionAnswersCountArray[questionId] != 0) {
                var progress = new Number(itemAnswersCount / questionAnswersCount * 100).toFixed(0); // 百分比
                $progressBar.css('width', progress + '%');
                $progressBar.html('<span>{0}% － {1} / {2}</span>'.format(progress, itemAnswersCount,questionAnswersCount));
            }
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });

    function findProgressBar(questionId, questionItemId) {
        return $('#question-list .question[data-question-id="{questionId}"] .question-item[data-question-item-id="{questionItemId}"] .progress-bar'
                .format({questionId: questionId, questionItemId: questionItemId}));
    }

    function loadSuggestions($loadButton) {
        var topicId = $('body').attr('data-topic-id');
        var questionId = $loadButton.closest('.question').attr('data-question-id');
        var questionItemId = $loadButton.closest('.question-item').attr('data-question-item-id');
        var offset = parseInt($loadButton.attr('data-offset'));

        var url = Urls.REST_QUESTIONS_ANSWERS_SUGGESTIONS.format({topicId: topicId, questionId: questionId, questionItemId: questionItemId});
        Utils.restGet(url, {offset: offset}, function(suggestions) {
            // suggestions 是字符串数组
            var $suggestions = $loadButton.closest('.question-item').find('.suggestions');
            $loadButton.attr('data-offset', offset + suggestions.length);

            for (var i = 0; i < suggestions.length; ++i) {
                $suggestions.append('<span class="badge pull-left">{0}</span>'.format(suggestions[i]));
            }
        }, function(error) {
            Utils.showError(error.responseText);
        });
    }
});


