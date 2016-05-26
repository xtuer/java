var participantGifts = [{"id":11,"participant":{"id":20,"name":"Biao","gender":true,"telephone":"18001166440","mail":"bob@edu-edu.com.cn"},"gift":{"id":4,"name":"优惠券","info":"150 元","code":3}},{"id":1,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":6,"name":"优惠券","info":"250 元","code":5}},{"id":10,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":1,"name":"优惠券","info":"10 元","code":0}},{"id":5,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":6,"name":"优惠券","info":"250 元","code":5}},{"id":9,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":4,"name":"优惠券","info":"150 元","code":3}},{"id":4,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":4,"name":"优惠券","info":"150 元","code":3}},{"id":8,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":2,"name":"优惠券","info":"50 元","code":1}},{"id":3,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":4,"name":"优惠券","info":"150 元","code":3}},{"id":7,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":6,"name":"优惠券","info":"250 元","code":5}},{"id":2,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":6,"name":"优惠券","info":"250 元","code":5}},{"id":6,"participant":{"id":19,"name":"Nacht","gender":false,"telephone":"13315257807","mail":"bi@gmail.com"},"gift":{"id":1,"name":"优惠券","info":"10 元","code":0}}];

$(document).ready(function() {
    ParticipantGifts.prepareTemplates();
    ParticipantGifts.queryParticipantGifts(1);
    ParticipantGifts.queryParticipantGiftsPagesCount();

    // 点击 td 显示编辑的 input
    $(document).on('click', 'table td[data-flag="gift-description"]', function() {
        var $td = $(this);
        var desc = $td.text();
        var $input = $('<input type="text" class="form-control">');
        $td.html($input).addClass('editing'); // 显示 <input>
        $input.val(desc).focus();
    });

    // 防止 input 的事件传递给 td
    $(document).on('click', 'table input', function(e) {
        e.stopPropagation();
    });

    // input 失去焦点时保存 gift code 并删除 input
    $(document).on('blur', 'table input', function(e) {
        e.stopPropagation();
        ParticipantGifts.saveParticipantGiftDescriptionWithInput($(this));
    });

    // input 失去焦点时保存 gift code 并删除 input
    $(document).on('keypress', 'table input', function(e) {
        e.stopPropagation();
        if('13' == e.keyCode) {
            ParticipantGifts.saveParticipantGiftDescriptionWithInput($(this));
        } else if ('27' == e.keyCode) {
            $(this).blur();
        }
    });
});

function ParticipantGifts() {

}

ParticipantGifts.prepareTemplates = function() {
    ParticipantGifts.ParticipantGiftTd = $('table tr.template');
    ParticipantGifts.ParticipantGiftTd.removeClass('template').remove();
}

ParticipantGifts.queryParticipantGiftsPagesCount = function(page) {
    Utils.restGet(Urls.REST_PARTICIPANT_GIFTS_PAGES_COUNT, {}, function(pagesCount) {
        $('#page-section').pagination({
            pages: pagesCount,
            currentPage: 1,
            edges: 3,
            prevText: '上一页',
            nextText: '下一页',
            ellipsePageSet: false,
            cssStyle: 'compact-theme',
            onPageClick: function(page) {
                ParticipantGifts.queryParticipantGifts(page);
            }
        });
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

ParticipantGifts.queryParticipantGifts = function(page) {
    Utils.restGet(Urls.REST_PARTICIPANT_GIFTS, {page: page}, function(participantGifts) {
        $('table tr:gt(0)').remove();

        for (var i = 0; i < participantGifts.length; ++i) {
            ParticipantGifts.showParticipantGifts(participantGifts[i]);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}

ParticipantGifts.showParticipantGifts = function(participantGift) {
    var $tr = ParticipantGifts.ParticipantGiftTd.clone();
    var pg = participantGift;
    var gender = pg.participant.gender === true ? '男' : '女';

    $tr.attr('data-participant-gift-id', pg.id);
    $('td[data-flag="participant-name"]', $tr).text(pg.participant.name);
    $('td[data-flag="participant-gender"]', $tr).text(gender);
    $('td[data-flag="participant-telephone"]', $tr).text(pg.participant.telephone);
    $('td[data-flag="participant-mail"]', $tr).text(pg.participant.mail);
    $('td[data-flag="gift-name"]', $tr).text(pg.gift.name);
    $('td[data-flag="gift-info"]', $tr).text(pg.gift.info);
    $('td[data-flag="gift-description"]', $tr).text(pg.description);

    $('table').append($tr);
}

ParticipantGifts.saveParticipantGiftDescriptionWithInput = function($input) {
    var $tr = $input.closest('tr');
    var $td = $input.closest('td');
    var participantGiftId = $tr.attr('data-participant-gift-id');
    var giftDescription = $.trim($input.val());
    $td.html(giftDescription).removeClass('editing'); // 会删除 <input>
    ParticipantGifts.saveParticipantGiftDescription(participantGiftId, giftDescription);
}

ParticipantGifts.saveParticipantGiftDescription = function(participantGiftId, description) {
    var url = Urls.REST_PARTICIPANT_GIFTS_DESCRIPTION.format({participantGiftId: participantGiftId});

    Utils.restUpdate(url, {description: description}, function(result) {
        if (!result.success) {
            Utils.showError(result.message);
        }
    }, function(error) {
        Utils.showError(error.responseText);
    });
}
