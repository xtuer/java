/**
 * 使用 Ajax 的方式执行 REST 的 GET 请求(服务器响应的数据根据 REST 的规范，应该是 Json 对象).
 * 以下几个 REST 的函数 $.restCreate(), $.restUpdate(), $.restDelete() 只是请求的 HTTP 方法和 data 处理不一样，其他的都是相同的.
 *
 * @param {[json]} options 有以下几个选项:
 *        {[string]}   url   请求的 URL(必选)
 *        {[json]}     data  请求的参数(可选)
 *        {[function]} done  请求成功时的回调函数(可选)
 *        {[function]} fail  请求失败时的回调函数(可选)
 *        {[function]} alway 请求完成后的回调函数(可选)
 * @return 没有返回值
 */
$.restGet = function(options) {
    options.httpMethod = 'GET';
    $.restAjax(options);
};

$.restCreate = function(options) {
    options.httpMethod = 'POST';
    options.data = options.data ? JSON.stringify(options.data) : {};
    $.restAjax(options);
};

$.restUpdate = function(options) {
    options.httpMethod = 'PUT';
    options.data = options.data ? JSON.stringify(options.data) : {};
    $.restAjax(options);
};

$.restDelete = function(options) {
    options.httpMethod = 'DELETE';
    options.data = options.data ? JSON.stringify(options.data) : {};
    $.restAjax(options);
};

/**
 * 执行 Ajax 请求.
 * 因为发送给 SpringMVC 的 Ajax 请求中必须满足下面的条件才能执行成功:
 *     1. dataType: 'json'
 *     2. contentType: 'application/json'
 *     3. data: GET 时为 json 对象，POST, PUT, DELETE 时必须为 JSON.stringify(data)
 *
 * @param {[json]} options 有以下几个选项:
 *        {[string]}   url        请求的 URL(必选)
 *        {[string]}   httpMethod 请求的方式，有 GET, PUT, POST, DELETE(必选)
 *        {[json]}     data       请求的参数(可选)
 *        {[function]} done       请求成功时的回调函数(可选)
 *        {[function]} fail       请求失败时的回调函数(可选)
 *        {[function]} alway      请求完成后的回调函数(可选)
 */
$.restAjax = function(options) {
    var defaults = {
        // url: 'http://xxx'
        // httpMethod: 'GET'
        data: {},
        done: function() {},
        fail: function() {},
        always: function() {}
    };

    var settings = $.extend({}, defaults, options);

    $.ajax({
        url:  settings.url,
        data: settings.data,
        type: settings.httpMethod,
        dataType: 'json',
        contentType: 'application/json'
    })
    .done(function(result) {
        settings.done(result);
    })
    .fail(function(error) {
        settings.fail(error);
    })
    .always(function() {
        settings.always();
    });
};
