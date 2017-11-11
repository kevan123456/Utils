package com.ws.hessian.io;


import java.io.IOException;

public class AbstractListDeserializer extends AbstractDeserializer {
    public AbstractListDeserializer() {
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        if(obj != null) {
            throw this.error("expected list at " + obj.getClass().getName() + " (" + obj + ")");
        } else {
            throw this.error("expected list at null");
        }
    }
}
