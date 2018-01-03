变量名和 URI 规则:

1. 页面 URI 的变量名以 PAGE_ 开头，此 URI 以 /page 开头，看到 URL 就知道是什么用途了
2. 页面对应模版文件的变量名以 FILE_ 开头，表明这个 URI 是文件的路径，即模版的路径
3. 普通 FORM 表单处理 URI 的变量名以 FORM_ 开头，此 URI 以 /form 开头
4. 操作资源的 api 变量名以 API_ 开头，此 URI 以 /api 开头，使用 RESTful 风格

```java
String PAGE_LOGIN       = "/page/login";  // 登陆
String FORM_DEMO_UPLOAD = "/form/demo/upload";
String API_SUBJECTS        = "/api/subjects";
String API_SUBJECTS_BY_ID  = "/api/subjects/{subjectId}";
```

更多请参考 **Urls.java**。