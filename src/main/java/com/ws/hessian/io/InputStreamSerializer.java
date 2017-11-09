/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSerializer extends AbstractSerializer {
    public InputStreamSerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        InputStream is = (InputStream)obj;
        if(is == null) {
            out.writeNull();
        } else {
            out.writeByteStream(is);
        }

    }
}
