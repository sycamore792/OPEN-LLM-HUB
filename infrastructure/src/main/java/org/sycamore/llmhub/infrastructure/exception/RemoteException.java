package org.sycamore.llmhub.infrastructure.exception;

/**
 * 远程服务调用异常
 *
 * @author 桑运昌
 */
public class RemoteException extends AbstractException {

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}