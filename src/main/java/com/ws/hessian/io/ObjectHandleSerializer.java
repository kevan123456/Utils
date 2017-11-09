/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;

public class ObjectHandleSerializer extends AbstractSerializer {
    public static final Serializer SER = new ObjectHandleSerializer();

    public ObjectHandleSerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(obj == null) {
            out.writeNull();
        } else {
            if(out.addRef(obj)) {
                return;
            }

            int ref = out.writeObjectBegin("object");
            if(ref < -1) {
                out.writeMapEnd();
            } else if(ref == -1) {
                out.writeInt(0);
                out.writeObjectBegin("object");
            }
        }

    }
}

