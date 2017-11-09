/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.util.Vector;

public class EnumerationDeserializer extends AbstractListDeserializer {
    private static EnumerationDeserializer _deserializer;

    public EnumerationDeserializer() {
    }

    public static EnumerationDeserializer create() {
        if(_deserializer == null) {
            _deserializer = new EnumerationDeserializer();
        }

        return _deserializer;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        Vector list = new Vector();
        in.addRef(list);

        while(!in.isEnd()) {
            list.add(in.readObject());
        }

        in.readEnd();
        return list.elements();
    }
}