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
    Utils.restGet(Urls.REST_GIFTS, {}, function(result) {
        if (result.success) {
            var giftCode = result.data;
            rotateWheel(giftCode);
        } else {
            alert(result.message);
        }
    }, function(error) {
        alert(error.responseText);
    });
}

function saveGift(giftCode) {
    Utils.restCreate(Urls.REST_GIFTS, {}, function(result) {
        if (result.success) {
            alert("您的奖品是: " + awards[giftCode].name + awards[giftCode].info);
            window.location.href = Urls.SUBMIT_SUCCESS;
        } else {
            alert(result.message);
        }
    }, function(error) {
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
