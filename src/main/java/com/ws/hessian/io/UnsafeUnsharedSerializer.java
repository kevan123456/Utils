package com.ws.hessian.io;

import java.io.IOException;
import java.util.logging.Logger;

public class UnsafeUnsharedSerializer extends UnsafeSerializer {
    private static final Logger log = Logger.getLogger(UnsafeUnsharedSerializer.class.getName());

    public UnsafeUnsharedSerializer(Class<?> cl) {
        super(cl);
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        boolean oldUnshared = out.setUnshared(true);

        try {
            super.writeObject(obj, out);
        } finally {
            out.setUnshared(oldUnshared);
        }

    }
}
