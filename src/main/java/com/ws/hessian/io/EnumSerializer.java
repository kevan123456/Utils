/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;
import java.lang.reflect.Method;

public class EnumSerializer extends AbstractSerializer {
    private Method _name;

    public EnumSerializer(Class cl) {
        if(!cl.isEnum() && cl.getSuperclass().isEnum()) {
            cl = cl.getSuperclass();
        }

        try {
            this._name = cl.getMethod("name", new Class[0]);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Class cl = obj.getClass();
            if(!cl.isEnum() && cl.getSuperclass().isEnum()) {
                cl = cl.getSuperclass();
            }

            String name = null;

            try {
                name = (String)this._name.invoke(obj, (Object[])null);
            } catch (Exception var6) {
                throw new RuntimeException(var6);
            }

            int ref = out.writeObjectBegin(cl.getName());
            if(ref < -1) {
                out.writeString("name");
                out.writeString(name);
                out.writeMapEnd();
            } else {
                if(ref == -1) {
                    out.writeClassFieldLength(1);
                    out.writeString("name");
                    out.writeObjectBegin(cl.getName());
                }

                out.writeString(name);
            }

        }
    }
}

