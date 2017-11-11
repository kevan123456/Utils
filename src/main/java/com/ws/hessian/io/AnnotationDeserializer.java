package com.ws.hessian.io;


import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.logging.Logger;

public class AnnotationDeserializer extends AbstractMapDeserializer {
    private static final Logger log = Logger.getLogger(AnnotationDeserializer.class.getName());
    private Class _annType;

    public AnnotationDeserializer(Class annType) {
        this._annType = annType;
    }

    public Class getType() {
        return this._annType;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            int e = in.addRef((Object)null);
            HashMap valueMap = new HashMap(8);

            while(!in.isEnd()) {
                String key = in.readString();
                Object value = in.readObject();
                valueMap.put(key, value);
            }

            in.readMapEnd();
            return Proxy.newProxyInstance(this._annType.getClassLoader(), new Class[]{this._annType}, new AnnotationInvocationHandler(this._annType, valueMap));
        } catch (IOException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new IOExceptionWrapper(var7);
        }
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])((String[])fields);

        try {
            in.addRef((Object)null);
            HashMap e = new HashMap(8);

            for(int i = 0; i < fieldNames.length; ++i) {
                String name = fieldNames[i];
                e.put(name, in.readObject());
            }

            return Proxy.newProxyInstance(this._annType.getClassLoader(), new Class[]{this._annType}, new AnnotationInvocationHandler(this._annType, e));
        } catch (IOException var7) {
            throw var7;
        } catch (Exception var8) {
            throw new HessianException(this._annType.getName() + ":" + var8, var8);
        }
    }
}
