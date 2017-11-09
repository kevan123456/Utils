package com.ws.hessian.io;

import java.io.IOException;

public interface Serializer {
    void writeObject(Object var1, AbstractHessianOutput var2) throws IOException;
}