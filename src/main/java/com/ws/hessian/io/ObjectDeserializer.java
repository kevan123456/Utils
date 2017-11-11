package com.ws.hessian.io;

import java.io.IOException;

public class ObjectDeserializer extends AbstractDeserializer {
    private Class<?> _cl;

    public ObjectDeserializer(Class<?> cl) {
        this._cl = cl;
    }

    public Class<?> getType() {
        return this._cl;
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        return in.readObject();
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + this._cl + "]";
    }
}
