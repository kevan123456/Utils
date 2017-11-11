package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArrayDeserializer extends AbstractListDeserializer {
    private Class _componentType;
    private Class _type;

    public ArrayDeserializer(Class componentType) {
        this._componentType = componentType;
        if(this._componentType != null) {
            try {
                this._type = Array.newInstance(this._componentType, 0).getClass();
            } catch (Exception var3) {
                ;
            }
        }

        if(this._type == null) {
            this._type = Object[].class;
        }

    }

    public Class getType() {
        return this._type;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        if(length >= 0) {
            Object[] var6 = this.createArray(length);
            in.addRef(var6);
            int var7;
            if(this._componentType != null) {
                for(var7 = 0; var7 < var6.length; ++var7) {
                    var6[var7] = in.readObject(this._componentType);
                }
            } else {
                for(var7 = 0; var7 < var6.length; ++var7) {
                    var6[var7] = in.readObject();
                }
            }

            in.readListEnd();
            return var6;
        } else {
            ArrayList list = new ArrayList();
            in.addRef(list);
            if(this._componentType != null) {
                while(!in.isEnd()) {
                    list.add(in.readObject(this._componentType));
                }
            } else {
                while(!in.isEnd()) {
                    list.add(in.readObject());
                }
            }

            in.readListEnd();
            Object[] data = this.createArray(list.size());

            for(int i = 0; i < data.length; ++i) {
                data[i] = list.get(i);
            }

            return data;
        }
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        Object[] data = this.createArray(length);
        in.addRef(data);
        int i;
        if(this._componentType != null) {
            for(i = 0; i < data.length; ++i) {
                data[i] = in.readObject(this._componentType);
            }
        } else {
            for(i = 0; i < data.length; ++i) {
                data[i] = in.readObject();
            }
        }

        return data;
    }

    protected Object[] createArray(int length) {
        return this._componentType != null?(Object[])((Object[])Array.newInstance(this._componentType, length)):new Object[length];
    }

    public String toString() {
        return "ArrayDeserializer[" + this._componentType + "]";
    }
}
