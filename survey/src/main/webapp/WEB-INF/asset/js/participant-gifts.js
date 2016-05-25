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
        $td.html($input).addClass('editing');
        $input.val(desc).focus();
    });

    // 防止 input 的事件传递给 td
    $(document).on('click', 'table input', function(e) {
        e.stopPropagation();
    });

    // input 失去焦点时保存 gift code 并删除 input
    $(document).on('blur', 'table input', function(e) {
        e.stopPropagation();
        var $input = $(this);
        var $tr = $input.closest('tr');
        var $td = $input.closest('td');
        var participantGiftId = $tr.attr('data-participant-gift-id');
        var giftDescription = $.trim($input.val());
        $td.html(giftDescription).removeClass('editing');
        ParticipantGifts.saveParticipantGiftDescription(participantGiftId, giftDescription);
    });
});

function ParticipantGifts() {

}

ParticipantGifts.prepareTemplates = function() {
    ParticipantGifts.ParticipantGiftTd = $('table tr.template');
    ParticipantGifts.ParticipantGiftTd.removeClass('template').remove();
}

ParticipantGifts.queryParticipantGiftsPagesCount = function(page) {
    $.ajax({
        url: Urls.participantGiftsPagesCount,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json' // 1. 少了就会报错
    })
    .done(function(pagesCount) {
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
    })
    .fail(function(error) {
        console.log(error.responseText);
        alert(error.responseText);
    });
}

ParticipantGifts.queryParticipantGifts = function(page) {
    $.ajax({
        url: Urls.participantGifts.replace('{page}', page),
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json' // 1. 少了就会报错
    })
    .done(function(participantGifts) {
        $('table tr:gt(0)').remove();

        for (var i = 0; i < participantGifts.length; ++i) {
            ParticipantGifts.showParticipantGifts(participantGifts[i]);
        }
    })
    .fail(function(error) {
        console.log(error.responseText);
        alert(error.responseText);
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

ParticipantGifts.saveParticipantGiftDescription = function(participantGiftId, giftDescription) {
    var data = {id: participantGiftId, description: giftDescription};
    console.log(data);

    $.ajax({
        url: Urls.participantGiftDescription,
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json', // 1. 少了就会报错
        data: JSON.stringify(data) // 2. data 需要序列化一下
    })
    .done(function(result) {
        if (!result.success) {
            alert(result.description);
        }
    })
    .fail(function(error) {
        alert(result.responseText);
    });
}