package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapDeserializer extends AbstractMapDeserializer {
    private Class<?> _type;
    private Constructor<?> _ctor;

    public MapDeserializer(Class<?> type) {
        if(type == null) {
            type = HashMap.class;
        }

        this._type = type;
        Constructor[] ctors = type.getConstructors();

        for(int e = 0; e < ctors.length; ++e) {
            if(ctors[e].getParameterTypes().length == 0) {
                this._ctor = ctors[e];
            }
        }

        if(this._ctor == null) {
            try {
                this._ctor = HashMap.class.getConstructor(new Class[0]);
            } catch (Exception var4) {
                throw new IllegalStateException(var4);
            }
        }

    }

    public Class<?> getType() {
        return this._type != null?this._type:HashMap.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        Object map;
        if(this._type == null) {
            map = new HashMap();
        } else if(this._type.equals(Map.class)) {
            map = new HashMap();
        } else if(this._type.equals(SortedMap.class)) {
            map = new TreeMap();
        } else {
            try {
                map = (Map)this._ctor.newInstance(new Object[0]);
            } catch (Exception var4) {
                throw new IOExceptionWrapper(var4);
            }
        }

        in.addRef(map);

        while(!in.isEnd()) {
            ((Map)map).put(in.readObject(), in.readObject());
        }

        in.readEnd();
        return map;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        String[] fieldNames = (String[])((String[])fields);
        Map map = this.createMap();
        in.addRef(map);

        for(int i = 0; i < fieldNames.length; ++i) {
            String name = fieldNames[i];
            map.put(name, in.readObject());
        }

        return map;
    }

    private Map createMap() throws IOException {
        if(this._type == null) {
            return new HashMap();
        } else if(this._type.equals(Map.class)) {
            return new HashMap();
        } else if(this._type.equals(SortedMap.class)) {
            return new TreeMap();
        } else {
            try {
                return (Map)this._ctor.newInstance(new Object[0]);
            } catch (Exception var2) {
                throw new IOExceptionWrapper(var2);
            }
        }
    }
}

