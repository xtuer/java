```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>REST</title>
</head>
<body>
    <script src="http://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="/js/jquery.rest.js"></script>
    <script>
        // [1] 服务器端的 GET 需要启用 UTF-8 才不会乱吗
        $.rest.get({url: '/rest', data: {name: 'Alice'}, success: function(result) {
            console.log(result);
        }});
      
        // [2] 普通 form 表单提交 rest Ajax 请求
        $.rest.create({url: '/rest', success: function(result) {
            console.log(result);
        }});
        $.rest.update({url: '/rest', data: {name: '黄飞鸿', age: 22}, success: function(result) {
            console.log(result);
        }});
      
        $.rest.remove({url: '/rest', success: function(result) {
            console.log(result);
        }});
      
        // [3] 使用 request body 传递复杂 Json 对象
        $.rest.create({url: '/rest/requestBody', data: {name: 'Alice'}, jsonRequestBody: true, success: function(result) {
            console.log(result);
        }});
      
        $.rest.update({url: '/rest/requestBody', data: {name: 'Alice'}, jsonRequestBody: true, success: function(result) {
            console.log(result);
        }});
      
        $.rest.remove({url: '/rest/requestBody', data: {name: 'Alice'}, jsonRequestBody: true, success: function(result) {
            console.log(result);
        }});
    </script>
</body>
</html>
```

