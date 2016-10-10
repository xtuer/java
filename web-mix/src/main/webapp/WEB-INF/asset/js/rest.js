/**
 * REST 工具类，使用 Ajax 执行 REST 请求
 */
function Rest() {
}

/**
 * 使用 Ajax 的方式执行 REST 的 GET 操作，并且表明服务器响应的数据格式是 JSON 格式.
 * [以下几个 REST 的函数 create, update, delete 只是请求的 HTTP 方法和 data 处理不一样，其他的都是相同的]
 *
 * @param  {[string]}   url              请求的 URL
 * @param  {[json]}     data             请求参数的 Json 对象
 * @param  {[string]}   httpMethod       请求的方法，为 'GET', 'PUT'(更新), 'POST'(创建), 'DELETE'
 * @param  {[function]} successCallback  请求成功时的回调函数
 * @param  {[function]} failCallback     请求失败时的回调函数
 * @param  {[function]} completeCallback 请求完成后的回调函数
 * @return 没有返回值
 */
Rest.get = function(url, data, successCallback, failCallback, completeCallback) {
    Rest.ajax(url, data, 'GET', successCallback, failCallback, completeCallback);
};

Rest.create = function(url, data, successCallback, failCallback, completeCallback) {
    Rest.ajax(url, JSON.stringify(data), 'POST', successCallback, failCallback, completeCallback);
};

Rest.update = function(url, data, successCallback, failCallback, completeCallback) {
    Rest.ajax(url, JSON.stringify(data), 'PUT', successCallback, failCallback, completeCallback);
};

Rest.delete = function(url, data, successCallback, failCallback, completeCallback) {
    Rest.ajax(url, JSON.stringify(data), 'DELETE', successCallback, failCallback, completeCallback);
};

/**
 * 执行 Ajax 请求.
 * 因为发送给 SpringMVC 的 Ajax 请求中必须满足下面的条件才能执行成功:
 *     1. dataType: 'json'
 *     2. contentType: 'application/json'
 *     3. data: GET 是为 json 对象，POST, PUT, DELETE 时必须为 JSON.stringify(data)
 * 所以把执行 Ajax 请求的函数提取出来作为一个函数，以免不小心出错.
 *
 * @param  {[string]}   url              请求的 URL
 * @param  {[json]}     data             请求参数的 Json 对象
 * @param  {[string]}   httpMethod       请求的方法，为 'GET', 'PUT'(更新), 'POST'(创建), 'DELETE'
 * @param  {[function]} successCallback  请求成功时的回调函数
 * @param  {[function]} failCallback     请求失败时的回调函数
 * @param  {[function]} completeCallback 请求完成后的回调函数
 * @return 没有返回值
 */
Rest.ajax = function(url, data, httpMethod, successCallback, failCallback, completeCallback) {
    $.ajax({
        url: url,
        data: data,
        type: httpMethod,
        dataType: 'json',
        contentType: 'application/json'
    })
    .done(function(result) {
        if ($.isFunction(successCallback)) {
            successCallback(result);
        }
    })
    .fail(function(response) {
        if ($.isFunction(failCallback)) {
            failCallback(response);
        }
    })
    .always(function() {
        if ($.isFunction(completeCallback)) {
            completeCallback();
        }
    });
};
