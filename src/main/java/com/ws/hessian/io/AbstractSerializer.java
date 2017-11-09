/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class AbstractSerializer implements Serializer {
    public static final AbstractSerializer.NullSerializer NULL = new AbstractSerializer.NullSerializer();
    protected static final Logger log = Logger.getLogger(AbstractSerializer.class.getName());

    public AbstractSerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            try {
                Object cl = this.writeReplace(obj);
                if(cl != null) {
                    out.writeObject(cl);
                    out.replaceRef(cl, obj);
                    return;
                }
            } catch (RuntimeException var5) {
                throw var5;
            } catch (Exception var6) {
                throw new HessianException(var6);
            }

            Class cl1 = this.getClass(obj);
            int ref = out.writeObjectBegin(cl1.getName());
            if(ref < -1) {
                this.writeObject10(obj, out);
            } else {
                if(ref == -1) {
                    this.writeDefinition20(cl1, out);
                    out.writeObjectBegin(cl1.getName());
                }

                this.writeInstance(obj, out);
            }

        }
    }

    protected Object writeReplace(Object obj) {
        return null;
    }

    protected Class<?> getClass(Object obj) {
        return obj.getClass();
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    protected void writeDefinition20(Class<?> cl, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    protected void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    static final class NullSerializer extends AbstractSerializer {
        NullSerializer() {
        }

        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            throw new IllegalStateException(this.getClass().getName());
        }
    }
}
