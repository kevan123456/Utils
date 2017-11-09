/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;

public class InputStreamDeserializer extends AbstractDeserializer {
    public static final InputStreamDeserializer DESER = new InputStreamDeserializer();

    public InputStreamDeserializer() {
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        return in.readInputStream();
    }
}
