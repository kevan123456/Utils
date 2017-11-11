package com.ws.hessian.io;

public class HessianMethodSerializationException extends HessianException {
    public HessianMethodSerializationException() {
    }

    public HessianMethodSerializationException(String message) {
        super(message);
    }

    public HessianMethodSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HessianMethodSerializationException(Throwable cause) {
        super(cause);
    }
}
