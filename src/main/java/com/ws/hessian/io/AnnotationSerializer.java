/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(AnnotationSerializer.class.getName());
    private static Object[] NULL_ARGS = new Object[0];
    private Class _annType;
    private Method[] _methods;
    private AnnotationSerializer.MethodSerializer[] _methodSerializers;

    public AnnotationSerializer(Class annType) {
        if(!Annotation.class.isAssignableFrom(annType)) {
            throw new IllegalStateException(annType.getName() + " is invalid because it is not a java.lang.annotation.Annotation");
        }
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            this.init(((Annotation)obj).annotationType());
            int ref = out.writeObjectBegin(this._annType.getName());
            if(ref < -1) {
                this.writeObject10(obj, out);
            } else {
                if(ref == -1) {
                    this.writeDefinition20(out);
                    out.writeObjectBegin(this._annType.getName());
                }

                this.writeInstance(obj, out);
            }

        }
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for(int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            out.writeString(method.getName());
            this._methodSerializers[i].serialize(out, obj, method);
        }

        out.writeMapEnd();
    }

    private void writeDefinition20(AbstractHessianOutput out) throws IOException {
        out.writeClassFieldLength(this._methods.length);

        for(int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            out.writeString(method.getName());
        }

    }

    public void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        for(int i = 0; i < this._methods.length; ++i) {
            Method method = this._methods[i];
            this._methodSerializers[i].serialize(out, obj, method);
        }

    }

    private void init(Class cl) {
        synchronized(this) {
            if(this._annType == null) {
                this._annType = cl;
                ArrayList methods = new ArrayList();
                Method[] i = this._annType.getDeclaredMethods();
                int len$ = i.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Method method = i[i$];
                    if(!method.getName().equals("hashCode") && !method.getName().equals("toString") && !method.getName().equals("annotationType") && method.getParameterTypes().length == 0) {
                        methods.add(method);
                        method.setAccessible(true);
                    }
                }

                if(this._annType == null) {
                    throw new IllegalStateException(cl.getName() + " is invalid because it does not have a valid annotationType()");
                } else {
                    this._methods = new Method[methods.size()];
                    methods.toArray(this._methods);
                    this._methodSerializers = new AnnotationSerializer.MethodSerializer[this._methods.length];

                    for(int var10 = 0; var10 < this._methods.length; ++var10) {
                        this._methodSerializers[var10] = getMethodSerializer(this._methods[var10].getReturnType());
                    }

                }
            }
        }
    }

    private Class getAnnotationType(Class cl) {
        if(cl == null) {
            return null;
        } else if(Annotation.class.equals(cl.getSuperclass())) {
            return cl;
        } else {
            Class[] ifaces = cl.getInterfaces();
            if(ifaces != null) {
                Class[] arr$ = ifaces;
                int len$ = ifaces.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Class iface = arr$[i$];
                    if(iface.equals(Annotation.class)) {
                        return cl;
                    }

                    Class annType = this.getAnnotationType(iface);
                    if(annType != null) {
                        return annType;
                    }
                }
            }

            return this.getAnnotationType(cl.getSuperclass());
        }
    }

    private static AnnotationSerializer.MethodSerializer getMethodSerializer(Class type) {
        return !Integer.TYPE.equals(type) && !Byte.TYPE.equals(type) && !Short.TYPE.equals(type) && !Integer.TYPE.equals(type)?(Long.TYPE.equals(type)?AnnotationSerializer.LongMethodSerializer.SER:(!Double.TYPE.equals(type) && !Float.TYPE.equals(type)?(Boolean.TYPE.equals(type)?AnnotationSerializer.BooleanMethodSerializer.SER:(String.class.equals(type)?AnnotationSerializer.StringMethodSerializer.SER:(!Date.class.equals(type) && !java.sql.Date.class.equals(type) && !Timestamp.class.equals(type) && !Time.class.equals(type)?AnnotationSerializer.MethodSerializer.SER:AnnotationSerializer.DateMethodSerializer.SER))):AnnotationSerializer.DoubleMethodSerializer.SER)):AnnotationSerializer.IntMethodSerializer.SER;
    }

    static HessianException error(Method method, Throwable cause) {
        String msg = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(): " + cause;
        throw new HessianMethodSerializationException(msg, cause);
    }

    static class DateMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.DateMethodSerializer();

        DateMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            Date value = null;

            try {
                value = (Date)method.invoke(obj, new Object[0]);
            } catch (InvocationTargetException var6) {
                throw AnnotationSerializer.error(method, var6.getCause());
            } catch (IllegalAccessException var7) {
                AnnotationSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            if(value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }

        }
    }

    static class StringMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.StringMethodSerializer();

        StringMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            String value = null;

            try {
                value = (String)method.invoke(obj, new Object[0]);
            } catch (InvocationTargetException var6) {
                throw AnnotationSerializer.error(method, var6.getCause());
            } catch (IllegalAccessException var7) {
                AnnotationSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            out.writeString(value);
        }
    }

    static class DoubleMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.DoubleMethodSerializer();

        DoubleMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            double value = 0.0D;

            try {
                value = ((Double)method.invoke(obj, new Object[0])).doubleValue();
            } catch (InvocationTargetException var7) {
                throw AnnotationSerializer.error(method, var7.getCause());
            } catch (IllegalAccessException var8) {
                AnnotationSerializer.log.log(Level.FINE, var8.toString(), var8);
            }

            out.writeDouble(value);
        }
    }

    static class LongMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.LongMethodSerializer();

        LongMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            long value = 0L;

            try {
                value = ((Long)method.invoke(obj, new Object[0])).longValue();
            } catch (InvocationTargetException var7) {
                throw AnnotationSerializer.error(method, var7.getCause());
            } catch (IllegalAccessException var8) {
                AnnotationSerializer.log.log(Level.FINE, var8.toString(), var8);
            }

            out.writeLong(value);
        }
    }

    static class IntMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.IntMethodSerializer();

        IntMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            int value = 0;

            try {
                value = ((Integer)method.invoke(obj, new Object[0])).intValue();
            } catch (InvocationTargetException var6) {
                throw AnnotationSerializer.error(method, var6.getCause());
            } catch (IllegalAccessException var7) {
                AnnotationSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            out.writeInt(value);
        }
    }

    static class BooleanMethodSerializer extends AnnotationSerializer.MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.BooleanMethodSerializer();

        BooleanMethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            boolean value = false;

            try {
                value = ((Boolean)method.invoke(obj, new Object[0])).booleanValue();
            } catch (InvocationTargetException var6) {
                throw AnnotationSerializer.error(method, var6.getCause());
            } catch (IllegalAccessException var7) {
                AnnotationSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            out.writeBoolean(value);
        }
    }

    static class MethodSerializer {
        static final AnnotationSerializer.MethodSerializer SER = new AnnotationSerializer.MethodSerializer();

        MethodSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Method method) throws IOException {
            Object value = null;

            try {
                value = method.invoke(obj, new Object[0]);
            } catch (InvocationTargetException var7) {
                throw AnnotationSerializer.error(method, var7.getCause());
            } catch (IllegalAccessException var8) {
                AnnotationSerializer.log.log(Level.FINE, var8.toString(), var8);
            }

            try {
                out.writeObject(value);
            } catch (Exception var6) {
                throw AnnotationSerializer.error(method, var6);
            }
        }
    }
}

