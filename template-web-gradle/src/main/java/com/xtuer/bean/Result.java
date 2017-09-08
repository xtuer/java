package com.xtuer.bean;

import com.alibaba.fastjson.JSONPObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Result {
    private int code;        // 状态码，一般是当 success 为 true 或者 false 时不足够表达时可使用
    private boolean success; // 成功时为 true，失败时为 false
    private String message;  // 成功或则失败时的描述信息
    private Object data;     // 成功或则失败时的更多详细数据，一般失败时不需要

    public Result(boolean success, String message) {
        this(success, message, null);
    }

    public Result(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static Result ok() {
        return new Result(true, "success");
    }

    public static Result ok(String message) {
        return new Result(true, message);
    }

    public static Result fail(String message) {
        return new Result(false, message);
    }

    public static Result ok(String message, Object data) {
        return new Result(true, message, data);
    }

    public static Result fail(String message, Object data) {
        return new Result(false, message, data);
    }

    /**
     * 使用传入的回调函数名字 callback 和参数 params 构造一个 JSONP 响应格式的字符串。
     *
     * @param callback 浏览器端 JSONP 回调函数的名字
     * @param data 参数列表
     * @return 返回 JSONP 格式的字符串
     */
    public static String jsonp(String callback, Object data) {
        JSONPObject jp = new JSONPObject(callback);
        jp.addParameter(data);

        return jp.toString();
    }
}
