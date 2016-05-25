package com.xtuer.bean;

/**
 * 存储操作的结果, 操作成功时 success 为 true, 失败时为 false, 如果返回操作的数据, 可以保存到 data 属性,
 * 操作结果的描述, 保存到 message 属性.
 */
public class Result {
    private boolean success; // 操作结果是否成功, 为 true 时操作成功, 为 false 时操作失败
    private Object data;     // 操作返回的数据
    private String message;  // 操作结果的描述, 例如失败的原因

    /**
     * 创建 Result 对象, success 为传入的 success, data 为 null, 操作结果的描述为传入的 message.
     *
     * @param success 操作是否成功, 为 true 时操作成功, 为 false 时操作失败
     * @param message 操作结果的描述
     */
    public Result(boolean success, String message) {
        this(success, null, message);
    }

    /**
     * 创建 Result 对象, success 为传入的 success, data 为传入的 data, 操作结果的描述为传入的 message.
     * @param success 操作是否成功, 为 true 时操作成功, 为 false 时操作失败
     * @param data 操作返回的数据
     * @param message 操作结果的描述
     */
    public Result(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
