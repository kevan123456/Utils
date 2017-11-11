package com.ws.hessian.io;

public class StackTraceElementDeserializer extends JavaDeserializer {
    public StackTraceElementDeserializer() {
        super(StackTraceElement.class);
    }

    protected Object instantiate() throws Exception {
        return new StackTraceElement("", "", "", 0);
    }
}
