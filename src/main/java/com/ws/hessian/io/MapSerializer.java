package com.ws.hessian.io;


import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MapSerializer extends AbstractSerializer {
    private boolean _isSendJavaType = true;

    public MapSerializer() {
    }

    public void setSendJavaType(boolean sendJavaType) {
        this._isSendJavaType = sendJavaType;
    }

    public boolean getSendJavaType() {
        return this._isSendJavaType;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Map map = (Map)obj;
            Class cl = obj.getClass();
            if(!cl.equals(HashMap.class) && obj instanceof Serializable) {
                if(this._isSendJavaType) {
                    out.writeMapBegin(cl.getName());
                } else {
                    while(cl != null) {
                        if(cl.equals(HashMap.class)) {
                            out.writeMapBegin((String)null);
                            break;
                        }

                        if(cl.getName().startsWith("java.")) {
                            out.writeMapBegin(cl.getName());
                            break;
                        }

                        cl = cl.getSuperclass();
                    }

                    if(cl == null) {
                        out.writeMapBegin((String)null);
                    }
                }
            } else {
                out.writeMapBegin((String)null);
            }

            Iterator iter = map.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }

            out.writeMapEnd();
        }
    }
}
