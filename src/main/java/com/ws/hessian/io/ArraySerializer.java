package com.ws.hessian.io;

import java.io.IOException;

public class ArraySerializer extends AbstractSerializer {
    public ArraySerializer() {
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Object[] array = (Object[])((Object[])obj);
            boolean hasEnd = out.writeListBegin(array.length, this.getArrayType(obj.getClass()));

            for(int i = 0; i < array.length; ++i) {
                out.writeObject(array[i]);
            }

            if(hasEnd) {
                out.writeListEnd();
            }

        }
    }

    private String getArrayType(Class cl) {
        if(cl.isArray()) {
            return '[' + this.getArrayType(cl.getComponentType());
        } else {
            String name = cl.getName();
            return name.equals("java.lang.String")?"string":(name.equals("java.lang.Object")?"object":(name.equals("java.util.Date")?"date":name));
        }
    }
}
