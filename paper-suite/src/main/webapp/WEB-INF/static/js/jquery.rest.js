'use strict';

(function($) {
    /**
     * 执行 REST 请求的 jQuery 插件，sync 开头的为同步请求，不以 sync 开头的为异步请求:
     *      Get    请求调用 $.rest.get(),    $.rest.syncGet()
     *      Create 请求调用 $.rest.create(), $.rest.syncCreate()
     *      Update 请求调用 $.rest.update(), $.rest.syncUpdate()
     *      Delete 请求调用 $.rest.remove(), $.rest.syncRemove()
     *
     * 调用例子:
     *      $.rest.get({url: '/rest', data: {name: 'Alice'}, success: function(result) {
     *          console.log(result);
     *      }});
     *
     *      $.rest.syncGet({url: '/rest', data: {name: 'Alice'}, success: function(result) { // 同步请求
     *          console.log(result);
     *      }});
     *
     *      $.rest.update({url: '/rest/books/{bookId}', urlParams: {bookId: 23}, data: {name: 'C&S'}, success: function(result) {
     *          console.log(result);
     *      }}, fail: function(failResponse) {});
     */
    $.rest = {
        /**
         * 使用 Ajax 的方式执行 REST 的 GET 请求(服务器响应的数据根据 REST 的规范，必须是 Json 对象).
         * 以下几个 REST 的函数 $.rest.create(), $.rest.update(), $.rest.remove() 只是请求的 HTTP 方法和 data 处理不一样，其他的都是相同的.
         *
         * @param {json} options 有以下几个选项:
         *        {string}   url       请求的 URL        (必选)
         *        {json}     urlParams URL 中的变量，例如 /rest/users/{id}，其中 {id} 为要被 urlParams.id 替换的部分 (可选)
         *        {json}     data      请求的参数         (可选)
         *        {boolean}  async     默认为异步方式     (可选)
         *        {function} success   请求成功时的回调函数(可选)
         *        {function} fail      请求失败时的回调函数(可选)
         *        {function} complete  请求完成后的回调函数(可选)
         * @return 没有返回值
         */
        get: function(options) {
            options.httpMethod = 'GET';
            this.sendRequest(options);
        },
        create: function(options) {
            options.data = options.data || {};
            options.httpMethod = 'POST';
            this.sendRequest(options);
        },
        update: function(options) {
            options.data = options.data || {};
            options.httpMethod   = 'POST';
            options.data._method = 'PUT'; // SpringMvc HiddenHttpMethodFilter 的 PUT 请求
            this.sendRequest(options);
        },
        remove: function(options) {
            options.data = options.data || {};
            options.httpMethod   = 'POST';
            options.data._method = 'DELETE'; // SpringMvc HiddenHttpMethodFilter 的 DELETE 请求
            this.sendRequest(options);
        },
        // 阻塞请求
        syncGet: function(options) {
            options.async = false;
            this.get(options);
        },
        syncCreate: function(options) {
            options.async = false;
            this.create(options);
        },
        syncUpdate: function(options) {
            options.async = false;
            this.update(options);
        },
        syncRemove: function(options) {
            options.async = false;
            this.remove(options);
        },

        /**
         * 执行 Ajax 请求，不推荐直接调用这个方法.
         *
         * @param {json} options 有以下几个选项:
         *        {string}   url        请求的 URL        (必选)
         *        {string}   httpMethod 请求的方式，有 GET, PUT, POST, DELETE (必选)
         *        {json}     urlParams  URL 中的变量      (可选)
         *        {json}     data       请求的参数        (可选)
         *        {boolean}  async      默认为异步方式     (可选)
         *        {function} success    请求成功时的回调函数(可选)
         *        {function} fail       请求失败时的回调函数(可选)
         *        {function} complete   请求完成后的回调函数(可选)
         */
        sendRequest: function(options) {
            var defaults = {
                data: {},
                async: true,
                success: function() {},
                fail: function(error) { console.error(error) }, // 默认把错误打印到控制台
                complete: function() {}
            };

            // 使用 jQuery.extend 合并参数
            var settings = $.extend({}, defaults, options);

            // 替换路径中的变量，例如 /rest/users/{id}, 其中 {id} 为要被 settings.urlParams.id 替换的部分
            if (settings.urlParams) {
                settings.url = settings.url.replace(/\{\{|\}\}|\{(\w+)\}/g, function(m, n) {
                    // m 是正则中捕捉的组 $0，n 是 $1，function($0, $1, $2, ...)
                    if (m == '{{') { return '{'; }
                    if (m == '}}') { return '}'; }
                    return settings.urlParams[n];
                });
            }

            // 执行 AJAX 请求
            $.ajax({
                url:   settings.url,
                data:  settings.data,
                async: settings.async,
                type:  settings.httpMethod,
                dataType:    'json',
                contentType: 'application/x-www-form-urlencoded',
                // 服务器抛异常时，有时 Windows 的 Tomcat 环境下竟然取不到 header X-Requested-With, Mac 下没问题，
                // 正常请求时是好的，手动添加 X-Requested-With 后正常和异常时都能取到了
                headers: {'X-Requested-With': 'XMLHttpRequest'}
            })
            .done(function(data, textStatus, jqXHR) {
                settings.success(data, textStatus, jqXHR);
            })
            .fail(function(jqXHR, textStatus, failThrown) {
                // data|jqXHR, textStatus, jqXHR|failThrown
                settings.fail(jqXHR, textStatus, failThrown);
            })
            .always(function() {
                settings.complete();
            });
        }
    };
})(jQuery);
