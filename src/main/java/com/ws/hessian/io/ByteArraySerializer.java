/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;

public class ByteArraySerializer extends AbstractSerializer implements ObjectSerializer {
    public static final ByteArraySerializer SER = new ByteArraySerializer();

    private ByteArraySerializer() {
    }

    public Serializer getObjectSerializer() {
        return this;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        byte[] data = (byte[])((byte[])obj);
        if(data != null) {
            out.writeBytes(data, 0, data.length);
        } else {
            out.writeNull();
        }

    }
}
