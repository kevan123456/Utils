/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.Serializable;

public class ByteHandle implements Serializable {
    private byte _value;

    private ByteHandle() {
    }

    public ByteHandle(byte value) {
        this._value = value;
    }

    public byte getValue() {
        return this._value;
    }

    public Object readResolve() {
        return new Byte(this._value);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._value + "]";
    }
}
