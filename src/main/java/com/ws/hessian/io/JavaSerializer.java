/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(JavaSerializer.class.getName());
    private static final WeakHashMap<Class<?>, SoftReference<JavaSerializer>> _serializerMap = new WeakHashMap();
    private Field[] _fields;
    private JavaSerializer.FieldSerializer[] _fieldSerializers;
    private Object _writeReplaceFactory;
    private Method _writeReplace;

    public JavaSerializer(Class<?> cl) {
        this.introspect(cl);
        this._writeReplace = getWriteReplace(cl);
        if(this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }

    }

    public static Serializer create(Class<?> cl) {
        WeakHashMap var1 = _serializerMap;
        synchronized(_serializerMap) {
            SoftReference baseRef = (SoftReference)_serializerMap.get(cl);
            Object base = baseRef != null?(JavaSerializer)baseRef.get():null;
            if(base == null) {
                if(cl.isAnnotationPresent(HessianUnshared.class)) {
                    base = new JavaUnsharedSerializer(cl);
                } else {
                    base = new JavaSerializer(cl);
                }

                baseRef = new SoftReference(base);
                _serializerMap.put(cl, baseRef);
            }

            return (Serializer)base;
        }
    }

    protected void introspect(Class<?> cl) {
        if(this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }

        ArrayList primitiveFields = new ArrayList();

        ArrayList compoundFields;
        int i;
        for(compoundFields = new ArrayList(); cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();

            for(i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    if(!field.getType().isPrimitive() && (!field.getType().getName().startsWith("java.lang.") || field.getType().equals(Object.class))) {
                        compoundFields.add(field);
                    } else {
                        primitiveFields.add(field);
                    }
                }
            }
        }

        ArrayList var7 = new ArrayList();
        var7.addAll(primitiveFields);
        var7.addAll(compoundFields);
        this._fields = new Field[var7.size()];
        var7.toArray(this._fields);
        this._fieldSerializers = new JavaSerializer.FieldSerializer[this._fields.length];

        for(i = 0; i < this._fields.length; ++i) {
            this._fieldSerializers[i] = getFieldSerializer(this._fields[i].getType());
        }

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

    protected Method getWriteReplace(Class<?> cl, Class<?> param) {
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

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Class cl = obj.getClass();

            try {
                if(this._writeReplace != null) {
                    Object ref2;
                    if(this._writeReplaceFactory != null) {
                        ref2 = this._writeReplace.invoke(this._writeReplaceFactory, new Object[]{obj});
                    } else {
                        ref2 = this._writeReplace.invoke(obj, new Object[0]);
                    }

                    int ref1 = out.writeObjectBegin(cl.getName());
                    if(ref1 < -1) {
                        this.writeObject10(ref2, out);
                    } else {
                        if(ref1 == -1) {
                            this.writeDefinition20(out);
                            out.writeObjectBegin(cl.getName());
                        }

                        this.writeInstance(ref2, out);
                    }

                    return;
                }
            } catch (RuntimeException var6) {
                throw var6;
            } catch (Exception var7) {
                throw new RuntimeException(var7);
            }

            int ref = out.writeObjectBegin(cl.getName());
            if(ref < -1) {
                this.writeObject10(obj, out);
            } else {
                if(ref == -1) {
                    this.writeDefinition20(out);
                    out.writeObjectBegin(cl.getName());
                }

                this.writeInstance(obj, out);
            }

        }
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for(int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
            this._fieldSerializers[i].serialize(out, obj, field);
        }

        out.writeMapEnd();
    }

    private void writeDefinition20(AbstractHessianOutput out) throws IOException {
        out.writeClassFieldLength(this._fields.length);

        for(int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
        }

    }

    public void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        try {
            for(int e = 0; e < this._fields.length; ++e) {
                Field field = this._fields[e];
                this._fieldSerializers[e].serialize(out, obj, field);
            }

        } catch (RuntimeException var5) {
            throw new RuntimeException(var5.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", var5);
        } catch (IOException var6) {
            throw new IOExceptionWrapper(var6.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", var6);
        }
    }

    private static JavaSerializer.FieldSerializer getFieldSerializer(Class<?> type) {
        return !Integer.TYPE.equals(type) && !Byte.TYPE.equals(type) && !Short.TYPE.equals(type) && !Integer.TYPE.equals(type)?(Long.TYPE.equals(type)?JavaSerializer.LongFieldSerializer.SER:(!Double.TYPE.equals(type) && !Float.TYPE.equals(type)?(Boolean.TYPE.equals(type)?JavaSerializer.BooleanFieldSerializer.SER:(String.class.equals(type)?JavaSerializer.StringFieldSerializer.SER:(!Date.class.equals(type) && !java.sql.Date.class.equals(type) && !Timestamp.class.equals(type) && !Time.class.equals(type)?JavaSerializer.FieldSerializer.SER:JavaSerializer.DateFieldSerializer.SER))):JavaSerializer.DoubleFieldSerializer.SER)):JavaSerializer.IntFieldSerializer.SER;
    }

    static class DateFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.DateFieldSerializer();

        DateFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            Date value = null;

            try {
                value = (Date)field.get(obj);
            } catch (IllegalAccessException var6) {
                JavaSerializer.log.log(Level.FINE, var6.toString(), var6);
            }

            if(value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }

        }
    }

    static class StringFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.StringFieldSerializer();

        StringFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            String value = null;

            try {
                value = (String)field.get(obj);
            } catch (IllegalAccessException var6) {
                JavaSerializer.log.log(Level.FINE, var6.toString(), var6);
            }

            out.writeString(value);
        }
    }

    static class DoubleFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.DoubleFieldSerializer();

        DoubleFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            double value = 0.0D;

            try {
                value = field.getDouble(obj);
            } catch (IllegalAccessException var7) {
                JavaSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            out.writeDouble(value);
        }
    }

    static class LongFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.LongFieldSerializer();

        LongFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            long value = 0L;

            try {
                value = field.getLong(obj);
            } catch (IllegalAccessException var7) {
                JavaSerializer.log.log(Level.FINE, var7.toString(), var7);
            }

            out.writeLong(value);
        }
    }

    static class IntFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.IntFieldSerializer();

        IntFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            int value = 0;

            try {
                value = field.getInt(obj);
            } catch (IllegalAccessException var6) {
                JavaSerializer.log.log(Level.FINE, var6.toString(), var6);
            }

            out.writeInt(value);
        }
    }

    static class BooleanFieldSerializer extends JavaSerializer.FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.BooleanFieldSerializer();

        BooleanFieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            boolean value = false;

            try {
                value = field.getBoolean(obj);
            } catch (IllegalAccessException var6) {
                JavaSerializer.log.log(Level.FINE, var6.toString(), var6);
            }

            out.writeBoolean(value);
        }
    }

    static class FieldSerializer {
        static final JavaSerializer.FieldSerializer SER = new JavaSerializer.FieldSerializer();

        FieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            Object value = null;

            try {
                value = field.get(obj);
            } catch (IllegalAccessException var8) {
                JavaSerializer.log.log(Level.FINE, var8.toString(), var8);
            }

            try {
                out.writeObject(value);
            } catch (RuntimeException var6) {
                throw new RuntimeException(var6.getMessage() + "\n field: " + field.getDeclaringClass().getName() + '.' + field.getName(), var6);
            } catch (IOException var7) {
                throw new IOExceptionWrapper(var7.getMessage() + "\n field: " + field.getDeclaringClass().getName() + '.' + field.getName(), var7);
            }
        }
    }
}
