var rollNumber = 0;

var awards = [
    {name: '优惠券', info: '88 元'},
    {name: '优惠券', info: '188 元'},
    {name: '优惠券', info: '288 元'},
    {name: '优惠券', info: '388 元'},
    {name: '优惠券', info: '888 元'},
    {name: '苹果5S手机', info: ''}
];

$(document).ready(function() {
    $('#run').click(function() {
        if (rollNumber == 0) {
            getGift();
        } else {
            alert('每个用户只能参与一次抽奖');
        }
        // rotateWheel(3);
    });
});

// 从服务器获取 gift code: 为奖品数组的下标
function getGift() {
    $.ajax({
        url: Urls.gift,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json' // 1. 少了就会报错
    })
    .done(function(result) {
        if (result.success) {
            var giftCode = result.data;
            rotateWheel(giftCode);
        } else {
            alert(result.description);
        }
    })
    .fail(function(error) {
        console.log(error.responseText);
        alert(error.responseText);
    });
}

function saveGift(giftCode) {
    $.ajax({
        url: Urls.gift,
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json' // 1. 少了就会报错
    })
    .done(function(result) {
        if (result.success) {
            alert("您的奖品是: " + awards[giftCode].name + awards[giftCode].info);
            window.location.href = Urls.submitSuccess;
        } else {
            alert(result.description);
        }
    })
    .fail(function(error) {
        console.log(error.responseText);
        alert(error.responseText);
    });
}

function rotateWheel(giftCode) {
    var angles = 2160 * rollNumber + 3600 - 360 / awards.length * giftCode;
    ++rollNumber;

    $('#wheel').rotate({
        angle: 0,
        animateTo: angles,
        easing: $.easing.easeInOutExpo,
        duration: 2000,
        callback: function() {
            saveGift(giftCode);
        }
    });
}