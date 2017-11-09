/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;

public class ThrowableSerializer extends JavaSerializer {
    public ThrowableSerializer(Class<?> cl, ClassLoader loader) {
        super(cl);
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Throwable e = (Throwable)obj;
        e.getStackTrace();
        super.writeObject(obj, out);
    }
}
