package com.xtuer.exception;

/**
 * 定义应用程序级别统一的异常，能够指定异常显示的页面，即 error view name。
 */
public class ApplicationException extends RuntimeException {
    private String errorViewName = null;

    public ApplicationException(String message) {
        this(message, null);
    }

    public ApplicationException(String message, String errorViewName) {
        super(message);
        this.errorViewName = errorViewName;
    }

    public String getErrorViewName() {
        return errorViewName;
    }
}
