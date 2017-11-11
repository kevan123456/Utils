package com.ws.hessian.io;


import java.io.IOException;
import java.util.HashMap;

public class ClassDeserializer extends AbstractMapDeserializer {
    private static final HashMap<String, Class> _primClasses = new HashMap();
    private ClassLoader _loader;

    public ClassDeserializer(ClassLoader loader) {
        this._loader = loader;
    }

    public Class getType() {
        return Class.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        int ref = in.addRef((Object)null);
        String name = null;

        while(!in.isEnd()) {
            String value = in.readString();
            if(value.equals("name")) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }

        in.readMapEnd();
        Object value1 = this.create(name);
        in.setRef(ref, value1);
        return value1;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])((String[])fields);
        int ref = in.addRef((Object)null);
        String name = null;

        for(int value = 0; value < fieldNames.length; ++value) {
            if("name".equals(fieldNames[value])) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }

        Object var7 = this.create(name);
        in.setRef(ref, var7);
        return var7;
    }

    Object create(String name) throws IOException {
        if(name == null) {
            throw new IOException("Serialized Class expects name.");
        } else {
            Class cl = (Class)_primClasses.get(name);
            if(cl != null) {
                return cl;
            } else {
                try {
                    return this._loader != null?Class.forName(name, false, this._loader):Class.forName(name);
                } catch (Exception var4) {
                    throw new IOExceptionWrapper(var4);
                }
            }
        }
    }

    static {
        _primClasses.put("void", Void.TYPE);
        _primClasses.put("boolean", Boolean.TYPE);
        _primClasses.put("java.lang.Boolean", Boolean.class);
        _primClasses.put("byte", Byte.TYPE);
        _primClasses.put("java.lang.Byte", Byte.class);
        _primClasses.put("char", Character.TYPE);
        _primClasses.put("java.lang.Character", Character.class);
        _primClasses.put("short", Short.TYPE);
        _primClasses.put("java.lang.Short", Short.class);
        _primClasses.put("int", Integer.TYPE);
        _primClasses.put("java.lang.Integer", Integer.class);
        _primClasses.put("long", Long.TYPE);
        _primClasses.put("java.lang.Long", Long.class);
        _primClasses.put("float", Float.TYPE);
        _primClasses.put("java.lang.Float", Float.class);
        _primClasses.put("double", Double.TYPE);
        _primClasses.put("java.lang.Double", Double.class);
        _primClasses.put("java.lang.String", String.class);
    }
}

