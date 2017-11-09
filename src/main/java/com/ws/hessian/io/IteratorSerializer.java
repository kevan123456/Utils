/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.util.Iterator;

public class IteratorSerializer extends AbstractSerializer {
    private static IteratorSerializer _serializer;

    public IteratorSerializer() {
    }

    public static IteratorSerializer create() {
        if(_serializer == null) {
            _serializer = new IteratorSerializer();
        }

        return _serializer;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Iterator iter = (Iterator)obj;
        boolean hasEnd = out.writeListBegin(-1, (String)null);

        while(iter.hasNext()) {
            Object value = iter.next();
            out.writeObject(value);
        }

        if(hasEnd) {
            out.writeListEnd();
        }

    }
}
