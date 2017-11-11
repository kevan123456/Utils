package com.ws.hessian.io;

public class HessianException extends RuntimeException {
    public HessianException() {
    }

    public HessianException(String message) {
        super(message);
    }

    public HessianException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public HessianException(Throwable rootCause) {
        super(rootCause);
    }
}
