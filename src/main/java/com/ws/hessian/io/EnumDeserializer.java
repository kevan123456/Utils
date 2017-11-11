package com.ws.hessian.io;


import java.io.IOException;
import java.lang.reflect.Method;

public class EnumDeserializer extends AbstractDeserializer {
    private Class _enumType;
    private Method _valueOf;

    public EnumDeserializer(Class cl) {
        if(cl.isEnum()) {
            this._enumType = cl;
        } else {
            if(!cl.getSuperclass().isEnum()) {
                throw new RuntimeException("Class " + cl.getName() + " is not an enum");
            }

            this._enumType = cl.getSuperclass();
        }

        try {
            this._valueOf = this._enumType.getMethod("valueOf", new Class[]{Class.class, String.class});
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public Class getType() {
        return this._enumType;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;

        while(!in.isEnd()) {
            String obj = in.readString();
            if(obj.equals("name")) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }

        in.readMapEnd();
        Object obj1 = this.create(name);
        in.addRef(obj1);
        return obj1;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])((String[])fields);
        String name = null;

        for(int obj = 0; obj < fieldNames.length; ++obj) {
            if("name".equals(fieldNames[obj])) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }

        Object var6 = this.create(name);
        in.addRef(var6);
        return var6;
    }

    private Object create(String name) throws IOException {
        if(name == null) {
            throw new IOException(this._enumType.getName() + " expects name.");
        } else {
            try {
                return this._valueOf.invoke((Object)null, new Object[]{this._enumType, name});
            } catch (Exception var3) {
                throw new IOExceptionWrapper(var3);
            }
        }
    }
}

