/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionSerializer extends AbstractSerializer {
    private boolean _sendJavaType = true;

    public CollectionSerializer() {
    }

    public void setSendJavaType(boolean sendJavaType) {
        this._sendJavaType = sendJavaType;
    }

    public boolean getSendJavaType() {
        return this._sendJavaType;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Collection list = (Collection)obj;
            Class cl = obj.getClass();
            boolean hasEnd;
            if(!cl.equals(ArrayList.class) && Serializable.class.isAssignableFrom(cl)) {
                if(!this._sendJavaType) {
                    for(hasEnd = false; cl != null; cl = cl.getSuperclass()) {
                        if(cl.getName().startsWith("java.")) {
                            hasEnd = out.writeListBegin(list.size(), cl.getName());
                            break;
                        }
                    }

                    if(cl == null) {
                        hasEnd = out.writeListBegin(list.size(), (String)null);
                    }
                } else {
                    hasEnd = out.writeListBegin(list.size(), obj.getClass().getName());
                }
            } else {
                hasEnd = out.writeListBegin(list.size(), (String)null);
            }

            Iterator iter = list.iterator();

            while(iter.hasNext()) {
                Object value = iter.next();
                out.writeObject(value);
            }

            if(hasEnd) {
                out.writeListEnd();
            }

        }
    }
}

