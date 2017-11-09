/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;
import java.util.ArrayList;

public class IteratorDeserializer extends AbstractListDeserializer {
    private static IteratorDeserializer _deserializer;

    public IteratorDeserializer() {
    }

    public static IteratorDeserializer create() {
        if(_deserializer == null) {
            _deserializer = new IteratorDeserializer();
        }

        return _deserializer;
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        ArrayList list = new ArrayList();
        in.addRef(list);

        while(!in.isEnd()) {
            list.add(in.readObject());
        }

        in.readEnd();
        return list.iterator();
    }
}