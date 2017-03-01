package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Result<T> {
    private boolean success;
    private String message;
    private T data;

    public Result(boolean success, String message) {
        this(success, message, null);
    }

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
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
