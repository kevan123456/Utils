/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.Serializable;

public class FloatHandle implements Serializable {
    private float _value;

    private FloatHandle() {
    }

    public FloatHandle(float value) {
        this._value = value;
    }

    public float getValue() {
        return this._value;
    }

    public Object readResolve() {
        return new Float(this._value);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._value + "]";
    }
}
