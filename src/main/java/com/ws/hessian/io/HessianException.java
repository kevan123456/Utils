/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
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
