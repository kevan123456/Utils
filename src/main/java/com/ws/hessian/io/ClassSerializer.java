/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;

public class ClassSerializer extends AbstractSerializer {
    public ClassSerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Class cl = (Class)obj;
        if(cl == null) {
            out.writeNull();
        } else {
            if(out.addRef(obj)) {
                return;
            }

            int ref = out.writeObjectBegin("java.lang.Class");
            if(ref < -1) {
                out.writeString("name");
                out.writeString(cl.getName());
                out.writeMapEnd();
            } else {
                if(ref == -1) {
                    out.writeInt(1);
                    out.writeString("name");
                    out.writeObjectBegin("java.lang.Class");
                }

                out.writeString(cl.getName());
            }
        }

    }
}
