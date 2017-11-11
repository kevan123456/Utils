package com.ws.hessian.io;

import java.io.IOException;

public class AbstractDeserializer implements Deserializer {
    public static final AbstractDeserializer.NullDeserializer NULL = new AbstractDeserializer.NullDeserializer();

    public AbstractDeserializer() {
    }

    public Class<?> getType() {
        return Object.class;
    }

    public boolean isReadResolve() {
        return false;
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = this.getClass().getName();
        if(obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        } else {
            throw this.error(className + ": unexpected null value");
        }
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = this.getClass().getName();
        if(obj != null) {
            throw this.error(className + ": unexpected object " + obj.getClass().getName() + " (" + obj + ")");
        } else {
            throw this.error(className + ": unexpected null value");
        }
    }

    public Object[] createFields(int len) {
        return new String[len];
    }

    public Object createField(String name) {
        return name;
    }

    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        return this.readObject(in, (Object[])fieldNames);
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        throw new UnsupportedOperationException(this.toString());
    }

    protected HessianProtocolException error(String msg) {
        return new HessianProtocolException(msg);
    }

    protected String codeName(int ch) {
        return ch < 0?"end of file":"0x" + Integer.toHexString(ch & 255);
    }

    static final class NullDeserializer extends AbstractDeserializer {
        NullDeserializer() {
        }
    }
}
