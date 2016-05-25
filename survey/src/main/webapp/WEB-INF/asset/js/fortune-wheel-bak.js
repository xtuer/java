$(document).ready(function() {
    // 奖品
    var awards = [
        {name: '优惠券', info: '10 元'},
        {name: '优惠券', info: '50 元'},
        {name: '优惠券', info: '100 元'},
        {name: '优惠券', info: '150 元'},
        {name: '优惠券', info: '200 元'},
        {name: '优惠券', info: '250 元'}
    ];

    var winner; // 中奖的下标
    var angles; // 旋转角度
    var clickNumber = 50; // 可抽奖次数
    var rollNumber = 0;  // 旋转次数
    var colors = ['#626262', '#787878', 'rgba(0,0,0,0.5)', '#CCC', '#AAA', '#626262'];

    drawFortuneWheel(); // 初始化幸运轮盘

    $('#startButton').bind('click',function(){
        if (clickNumber >= 1) {
            $('#startButton').attr("disabled", true); // 转盘旋转过程“开始抽奖”按钮无法点击
            clickNumber = clickNumber - 1; //可抽奖次数减一
            rollNumber = rollNumber + 1;   // 旋转次数加一
            // runFortuneWheel(); // 转盘旋转
            getGift();
        } else {
            alert("亲，抽奖次数已用光！");
        }
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
                winner = giftCode; // 从服务器获取
                angles = 2160 * rollNumber + 1800 - 360 / awards.length * winner;

                runFortuneWheel();
            } else {
                alert(result.description);
            }
        })
        .fail(function(error) {
            console.log(error.responseText);
            alert(error.responseText);
        });
    }

    function saveGift() {
        $.ajax({
            url: Urls.gift,
            type: 'PUT',
            dataType: 'json',
            contentType: 'application/json' // 1. 少了就会报错
        })
        .done(function(result) {
            if (result.success) {
                alert("您的奖品是: " + awards[winner].name + awards[winner].info);
            } else {
                alert(result.description);
            }
        })
        .fail(function(error) {
            console.log(error.responseText);
            alert(error.responseText);
        });
    }

    // 旋转转盘
    function runFortuneWheel() {
        // probability();

        $('#myCanvas00').rotate({
            angle: 0,
            animateTo: angles,
            easing: $.easing.easeInOutExpo,
            duration: 1000,
            callback: function() {
                // $('#startButton').removeAttr("disabled", true);
                saveGift(winner);
                // $('#info').text('Award: ' + awards[winner].name + awards[winner].info);
            }
        });

        // var degValue = 'rotate(' + angles + 'deg' + ')';
        // $('#myCanvas00').css('-o-transform', degValue);      //Opera
        // $('#myCanvas00').css('-ms-transform', degValue);     //IE浏览器
        // $('#myCanvas00').css('-moz-transform', degValue);    //Firefox
        // $('#myCanvas00').css('-webkit-transform', degValue); //Chrome和Safari
        // $('#myCanvas00').css('transform', degValue);

        // var deg = 2160 * rollNumber + 1800;
        // degValue = 'rotate(-' + deg + 'deg' + ')';
        // $('#myCanvas02').css('-o-transform', degValue);      //Opera
        // $('#myCanvas02').css('-ms-transform', degValue);     //IE浏览器
        // $('#myCanvas02').css('-moz-transform', degValue);    //Firefox
        // $('#myCanvas02').css('-webkit-transform', degValue); //Chrome和Safari
        // $('#myCanvas02').css('transform', degValue);

        // $('#myCanvas03').css('-o-transform', degValue);      //Opera
        // $('#myCanvas03').css('-ms-transform', degValue);     //IE浏览器
        // $('#myCanvas03').css('-moz-transform', degValue);    //Firefox
        // $('#myCanvas03').css('-webkit-transform', degValue); //Chrome和Safari
        // $('#myCanvas03').css('transform', degValue);
    }

    // 各奖项对应的旋转角度
    function probability() {
        winner = parseInt(Math.random() * awards.length); // 从服务器获取
        angles = 2160 * rollNumber + 1800 - 360 / awards.length * winner;
    }

    //绘制转盘
    function drawFortuneWheel(){
        var ctx0 = document.getElementById('myCanvas00').getContext('2d');
        var ctx1 = document.getElementById('myCanvas01').getContext('2d');
        var ctx2 = document.getElementById('myCanvas02').getContext('2d');
        var ctx3 = document.getElementById('myCanvas03').getContext('2d');

        drawPie();
        drawPieText();
        drawIndicator();

        // 每个奖项的饼图
        function drawPie(){
            var length = awards.length;
            var spanAngle = Math.PI * 2 / length;

            // 绘制背景
            ctx0.save();
            ctx0.beginPath();
            ctx0.moveTo(150, 150);
            ctx0.arc(150, 150, 150, 0, Math.PI * 2, false);
            ctx0.closePath();
            ctx0.fillStyle = colors[0];
            ctx0.fill();
            ctx0.restore();

            // 画一个等份扇形组成的圆形
            for (var i = 0; i < length; i++){
                var startAngle = spanAngle * i - spanAngle / 2 - Math.PI / 2; // 指向正中
                var endAngle = startAngle + spanAngle;

                ctx0.save();
                ctx0.beginPath();
                ctx0.moveTo(150, 150);
                ctx0.arc(150, 150, 150, startAngle, endAngle, false);
                ctx0.closePath();
                ctx0.fillStyle = colors[i%2];
                ctx0.fill();
                ctx0.restore();
            }
        }

        // 每个奖项的文字
        function drawPieText(){
            var length    = awards.length;
            var spanAngle = Math.PI * 2 / length;
            var stringWidth;
            // ctx0.textAlign    = 'start';
            // ctx0.textBaseline = 'middle';
            // ctx0.fillStyle    = colors[3];

            for (var i = 0; i < length; i++) {
                ctx0.save();
                ctx0.beginPath();
                ctx0.translate(150, 150); // 移动到圆心
                ctx0.rotate(spanAngle * i);
                ctx0.fillStyle = colors[3];

                ctx0.font = "14px Microsoft YaHei";
                stringWidth = ctx0.measureText(awards[i].name).width;
                ctx0.fillText(awards[i].name, -stringWidth/2, -115, 60);
                ctx0.font = "14px Microsoft YaHei";
                stringWidth = ctx0.measureText(awards[i].info).width;
                ctx0.fillText(awards[i].info, -stringWidth/2, -95, 60);

                ctx0.closePath();
                ctx0.restore();
            }
        }

        // 中间的指示图
        function drawIndicator(){
            // 中间半透明圆圈
            ctx1.beginPath();
            ctx1.arc(100, 100, 75, 0, Math.PI*2, false);
            ctx1.fillStyle = colors[2];
            ctx1.fill();
            ctx1.closePath();

            // 箭头指针
            ctx2.beginPath();
            ctx2.moveTo(100, 24);
            ctx2.lineTo(90, 62);
            ctx2.lineTo(110, 62);
            ctx2.lineTo(100, 24);
            ctx2.fillStyle = colors[5];
            ctx2.fill();
            ctx2.closePath();

            // 中间小圆
            ctx3.beginPath();
            var gradient = ctx3.createRadialGradient(100, 100, 0, 100, 100, 170);
            gradient.addColorStop('0', 'gray');
            gradient.addColorStop('0.3', colors[5]);
            gradient.addColorStop('1.0', colors[5]);
            ctx3.fillStyle = gradient; // 用渐变进行填充
            // ctx3.fillStyle = colors[5];
            ctx3.arc(100, 100, 40, 0, Math.PI*2, false);
            ctx3.fill();
            ctx3.closePath();

            // 小圆文字
            ctx3.font = "Bold 20px Microsoft YaHei";
            ctx3.textAlign='start';
            ctx3.textBaseline='middle';
            ctx3.fillStyle = colors[4];
            ctx3.beginPath();
            ctx3.fillText('开始', 80, 90, 40);
            ctx3.fillText('抽奖', 80, 110, 40);
            ctx3.fill();
            ctx3.closePath();
        }
    }
});
