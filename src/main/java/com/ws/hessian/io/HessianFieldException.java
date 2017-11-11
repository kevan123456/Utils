package com.ws.hessian.io;

public class HessianFieldException extends HessianProtocolException {
    public HessianFieldException() {
    }

    public HessianFieldException(String message) {
        super(message);
    }

    public HessianFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public HessianFieldException(Throwable cause) {
        super(cause);
    }
}
