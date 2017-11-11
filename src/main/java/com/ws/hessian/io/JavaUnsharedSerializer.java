package com.ws.hessian.io;


import java.io.IOException;
import java.util.logging.Logger;

public class JavaUnsharedSerializer extends JavaSerializer {
    private static final Logger log = Logger.getLogger(JavaUnsharedSerializer.class.getName());

    public JavaUnsharedSerializer(Class<?> cl) {
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
