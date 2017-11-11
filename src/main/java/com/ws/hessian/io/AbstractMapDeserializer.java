package com.ws.hessian.io;

import java.io.IOException;
import java.util.HashMap;

public class AbstractMapDeserializer extends AbstractDeserializer {
    public AbstractMapDeserializer() {
    }

    public Class getType() {
        return HashMap.class;
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        if(obj != null) {
            throw this.error("expected map/object at " + obj.getClass().getName() + " (" + obj + ")");
        } else {
            throw this.error("expected map/object at null");
        }
    }
}