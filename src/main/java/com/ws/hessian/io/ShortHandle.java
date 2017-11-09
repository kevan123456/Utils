/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.Serializable;

public class ShortHandle implements Serializable {
    private short _value;

    private ShortHandle() {
    }

    public ShortHandle(short value) {
        this._value = value;
    }

    public short getValue() {
        return this._value;
    }

    public Object readResolve() {
        return new Short(this._value);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._value + "]";
    }
}
