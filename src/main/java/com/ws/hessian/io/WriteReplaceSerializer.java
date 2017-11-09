/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteReplaceSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(WriteReplaceSerializer.class.getName());
    private Object _writeReplaceFactory;
    private Method _writeReplace;
    private Serializer _baseSerializer;

    public WriteReplaceSerializer(Class<?> cl, ClassLoader loader, Serializer baseSerializer) {
        this.introspectWriteReplace(cl, loader);
        this._baseSerializer = baseSerializer;
    }

    private void introspectWriteReplace(Class<?> cl, ClassLoader loader) {
        try {
            String e = cl.getName() + "HessianSerializer";
            Class serializerClass = Class.forName(e, false, loader);
            Object serializerObject = serializerClass.newInstance();
            Method writeReplace = getWriteReplace(serializerClass, cl);
            if(writeReplace != null) {
                this._writeReplaceFactory = serializerObject;
                this._writeReplace = writeReplace;
            }
        } catch (ClassNotFoundException var7) {
            ;
        } catch (Exception var8) {
            log.log(Level.FINER, var8.toString(), var8);
        }

        this._writeReplace = getWriteReplace(cl);
        if(this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }

    }

    protected static Method getWriteReplace(Class<?> cl, Class<?> param) {
        while(cl != null) {
            Method[] arr$ = cl.getDeclaredMethods();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Method method = arr$[i$];
                if(method.getName().equals("writeReplace") && method.getParameterTypes().length == 1 && param.equals(method.getParameterTypes()[0])) {
                    return method;
                }
            }

            cl = cl.getSuperclass();
        }

        return null;
    }

    protected static Method getWriteReplace(Class<?> cl) {
        while(cl != null) {
            Method[] methods = cl.getDeclaredMethods();

            for(int i = 0; i < methods.length; ++i) {
                Method method = methods[i];
                if(method.getName().equals("writeReplace") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }

            cl = cl.getSuperclass();
        }

        return null;
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        int ref = out.getRef(obj);
        if(ref >= 0) {
            out.writeRef(ref);
        } else {
            try {
                Object e = this.writeReplace(obj);
                if(obj == e) {
                    if(log.isLoggable(Level.FINE)) {
                        log.fine(this + ": Hessian writeReplace error.  The writeReplace method (" + this._writeReplace + ") must not return the same object: " + obj);
                    }

                    this._baseSerializer.writeObject(obj, out);
                } else {
                    out.writeObject(e);
                    out.replaceRef(e, obj);
                }
            } catch (RuntimeException var5) {
                throw var5;
            } catch (Exception var6) {
                throw new RuntimeException(var6);
            }
        }
    }

    protected Object writeReplace(Object obj) {
        try {
            return this._writeReplaceFactory != null?this._writeReplace.invoke(this._writeReplaceFactory, new Object[]{obj}):this._writeReplace.invoke(obj, new Object[0]);
        } catch (RuntimeException var3) {
            throw var3;
        } catch (InvocationTargetException var4) {
            throw new RuntimeException(var4.getCause());
        } catch (Exception var5) {
            throw new RuntimeException(var5);
        }
    }
}

