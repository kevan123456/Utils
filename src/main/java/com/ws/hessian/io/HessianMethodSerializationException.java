/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
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
