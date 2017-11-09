/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class SqlDateDeserializer extends AbstractDeserializer {
    private Class _cl;
    private Constructor _constructor;

    public SqlDateDeserializer(Class cl) {
        try {
            this._cl = cl;
            this._constructor = cl.getConstructor(new Class[]{Long.TYPE});
        } catch (NoSuchMethodException var3) {
            throw new HessianException(var3);
        }
    }

    public Class getType() {
        return this._cl;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        int ref = in.addRef((Object)null);
        long initValue = -9223372036854775808L;

        while(!in.isEnd()) {
            String value = in.readString();
            if(value.equals("value")) {
                initValue = in.readUTCDate();
            } else {
                in.readString();
            }
        }

        in.readMapEnd();
        Object value1 = this.create(initValue);
        in.setRef(ref, value1);
        return value1;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])((String[])fields);
        int ref = in.addRef((Object)null);
        long initValue = -9223372036854775808L;

        for(int value = 0; value < fieldNames.length; ++value) {
            String key = fieldNames[value];
            if(key.equals("value")) {
                initValue = in.readUTCDate();
            } else {
                in.readObject();
            }
        }

        Object var9 = this.create(initValue);
        in.setRef(ref, var9);
        return var9;
    }

    private Object create(long initValue) throws IOException {
        if(initValue == -9223372036854775808L) {
            throw new IOException(this._cl.getName() + " expects name.");
        } else {
            try {
                return this._constructor.newInstance(new Object[]{new Long(initValue)});
            } catch (Exception var4) {
                throw new IOExceptionWrapper(var4);
            }
        }
    }
}
