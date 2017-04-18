package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Result<T> {
    private int code;        // 状态码，一般是当 success 为 true 或者 false 时不足够表达时可使用
    private boolean success; // 成功时为 true，失败时为 false
    private String message;  // 成功或则失败时的描述信息
    private T data;          // 成功或则失败时的更多详细数据，一般失败时不需要

    public Result(boolean success, String message) {
        this(success, message, null);
    }

    public Result(boolean success, String message, T data) {
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

    public static <T> Result<T> ok(String message, T data) {
        return new Result(true, message, data);
    }

    public static <T> Result<T> fail(String message, T data) {
        return new Result(false, message, data);
    }
}
