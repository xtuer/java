package exception;

public class ArgumentException extends RuntimeException {
    public ArgumentException(String message) {
        super(message);
    }

    /**
     * 重写堆栈填充，不填充错误堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
