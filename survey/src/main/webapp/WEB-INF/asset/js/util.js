/**
 * 工具类，提供了
 *     使用 Ajax 执行 REST 请求
 *     判断函数，变量是否已经定义
 */
function Utils() {

}

/**
 * 使用 Ajax 的方式执行 REST 的 GET 操作，并且表明服务器响应的数据格式是 JSON 格式.
 * [一下几个 REST 的函数 restCreate, restUpdate, restDelete 只是请求的 HTTP 方法不一样，其他的都是相同的]
 *
 * @param url 请求的 URL
 * @param data 请求的参数: Json 对象
 * @param successCallback 请求成功时的回调函数
 * @param failCallback 请求失败时的回调函数
 * @param completeCallback 不管请求是否成功，最后都会被调用
 */
Utils.restGet = function(url, data, successCallback, failCallback, completeCallback) {
    Utils.ajax(url, data, 'GET', successCallback, failCallback, completeCallback);
}

Utils.restCreate = function(url, data, successCallback, failCallback, completeCallback) {
    Utils.ajax(url, JSON.stringify(data), 'POST', successCallback, failCallback, completeCallback);
}

Utils.restUpdate = function(url, data, successCallback, failCallback, completeCallback) {
    Utils.ajax(url, JSON.stringify(data), 'PUT', successCallback, failCallback, completeCallback);
}

Utils.restDelete = function(url, data, successCallback, failCallback, completeCallback) {
    Utils.ajax(url, JSON.stringify(data), 'DELETE', successCallback, failCallback, completeCallback);
}

/**
 * 执行 Ajax 请求.
 * 因为发送给 SpringMVC 的 Ajax 请求中必须满足下面的条件才能执行成功:
 *     1. dataType: 'json'
 *     2. contentType: 'application/json'
 *     3. data: JSON.stringify(data)
 * 所以把执行 Ajax 请求的函数提取出来作为一个函数，以免不小心出错.
 *
 * @param url 请求的 URL
 * @param data 请求的参数: Json 对象
 * @param httpMethod 请求的方法，为 'GET', 'PUT'(更新), 'POST'(创建), 'DELETE'
 * @param successCallback 请求成功时的回调函数
 * @param failCallback 请求失败时的回调函数
 * @param completeCallback 不管请求是否成功，最后都会被调用
 */
Utils.ajax = function(url, data, httpMethod, successCallback, failCallback, completeCallback) {
    $.ajax({
        url: url,
        type: httpMethod,
        dataType: 'json',
        contentType: 'application/json',
        data: data
    })
    .done(function(result) {
        if (Utils.isFunctionExist(successCallback)) {
            successCallback(result);
        }
    })
    .fail(function(error) {
        if (Utils.isFunctionExist(failCallback)) {
            failCallback(error);
        }
    })
    .always(function() {
        if (Utils.isFunctionExist(completeCallback)) {
            completeCallback();
        }
    });
}

/**
 * 判断函数是否存在
 *
 * @param functionName 函数名
 * @return 存在返回 true，否则返回 false
 */
Utils.isFunctionExist = function(functionName) {
    try {
        if (typeof(eval(functionName)) == "function") {
            return true;
        }
    } catch(e) {}

    return false;
}

/**
 * 判断变量是否存在
 *
 * @param variableName 变量名
 * @return 存在返回 true，否则返回 false
 */
Utils.isVariableExist = function(variableName) {
    try {
        if (typeof(variableName) == "undefined") {
            return false;
        } else {
            return true;
        }
    } catch(e) {}

    return false;
}

/**
 * 选中输入框中下标为 start 到 end 的部分(不包含 end)
 *
 * @param input 输入框 <input>
 * @param start 输入框中被选中文本开始的下标
 * @param end 输入框中被选中文本结束的下标
 */
Utils.selectInputText = function(input, start, end) {
    input.focus();

    if (typeof input.selectionStart != "undefined") {
        input.selectionStart = start;
        input.selectionEnd = end;
    } else if (document.selection && document.selection.createRange) {
        // IE branch
        input.select();
        var range = document.selection.createRange();
        range.collapse(true);
        range.moveEnd("character", end);
        range.moveStart("character", start);
        range.select();
    }
}

/**
 * 扩展了 String 类型，给其添加格式化的功能，替换字符串中 {placeholder} 或者 {0}, {1} 等模式部分为参数中传入的字符串
 * 使用方法:
 *     'I can speak {language} since I was {age}'.format({language: 'Javascript', age: 10})
 *     'I can speak {0} since I was {1}'.format('Javascript', 10)
 * 输出都为:
 *     I can speak Javascript since I was 10
 *
 * @param replacements 用来替换 placeholder 的 JSON 对象或者数组
 * @return 格式化后的字符串
 */
String.prototype.format = function(replacements) {
    replacements = (typeof replacements === 'object') ? replacements : Array.prototype.slice.call(arguments, 0);
    return formatString(this, replacements);
}

/**
 * 替换字符串中 {placeholder} 或者 {0}, {1} 等模式部分为参数中传入的字符串
 * 使用方法:
 *     formatString('I can speak {language} since I was {age}', {language: 'Javascript', age: 10})
 *     formatString('I can speak {0} since I was {1}', 'Javascript', 10)
 * 输出都为:
 *     I can speak Javascript since I was 10
 *
 * @param str 带有 placeholder 的字符串
 * @param replacements 用来替换 placeholder 的 JSON 对象或者数组
 * @return 格式化后的字符串
 */
var formatString = function (str, replacements) {
    replacements = (typeof replacements === 'object') ? replacements : Array.prototype.slice.call(arguments, 1);

    return str.replace(/\{\{|\}\}|\{(\w+)\}/g, function(m, n) {
        if (m == '{{') { return '{'; }
        if (m == '}}') { return '}'; }
        return replacements[n];
    });
};

Utils.showConfirm = function(title, message, callback) {
    BootstrapDialog.confirm({
        title: title,
        message: message,
        type: BootstrapDialog.TYPE_WARNING,
        btnCancelLabel: '取消',
        btnOKLabel: '确定',
        callback: function(result) {
            // result will be true if button was click, while it will be false if users close the dialog directly.
            if(result) {
                if (Utils.isFunctionExist(callback)) {
                    callback();
                }
            }
        }
    });
}

Utils.showError = function(errorMessage) {
    console.log(errorMessage);
    BootstrapDialog.show({title: '错误', message: errorMessage, type: BootstrapDialog.TYPE_DANGER});
}
