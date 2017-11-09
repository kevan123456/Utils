/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;

public class IOExceptionWrapper extends IOException {
    private Throwable _cause;

    public IOExceptionWrapper(Throwable cause) {
        super(cause.toString());
        this._cause = cause;
    }

    public IOExceptionWrapper(String msg, Throwable cause) {
        super(msg);
        this._cause = cause;
    }

    public Throwable getCause() {
        return this._cause;
    }
}