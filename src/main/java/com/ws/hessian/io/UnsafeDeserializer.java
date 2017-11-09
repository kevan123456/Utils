/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

public class UnsafeDeserializer extends AbstractMapDeserializer {
    private static final Logger log = Logger.getLogger(JavaDeserializer.class.getName());
    private static boolean _isEnabled;
    private static Unsafe _unsafe;
    private Class<?> _type;
    private HashMap<String, UnsafeDeserializer.FieldDeserializer> _fieldMap;
    private Method _readResolve;

    public UnsafeDeserializer(Class<?> cl) {
        this._type = cl;
        this._fieldMap = this.getFieldMap(cl);
        this._readResolve = this.getReadResolve(cl);
        if(this._readResolve != null) {
            this._readResolve.setAccessible(true);
        }

    }

    public static boolean isEnabled() {
        return _isEnabled;
    }

    public Class<?> getType() {
        return this._type;
    }

    public boolean isReadResolve() {
        return this._readResolve != null;
    }

    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            Object e = this.instantiate();
            return this.readMap(in, e);
        } catch (IOException var3) {
            throw var3;
        } catch (RuntimeException var4) {
            throw var4;
        } catch (Exception var5) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + var5.getMessage(), var5);
        }
    }

    public Object[] createFields(int len) {
        return new UnsafeDeserializer.FieldDeserializer[len];
    }

    public Object createField(String name) {
        Object reader = this._fieldMap.get(name);
        if(reader == null) {
            reader = UnsafeDeserializer.NullFieldDeserializer.DESER;
        }

        return reader;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        try {
            Object e = this.instantiate();
            return this.readObject(in, e, (UnsafeDeserializer.FieldDeserializer[])((UnsafeDeserializer.FieldDeserializer[])fields));
        } catch (IOException var4) {
            throw var4;
        } catch (RuntimeException var5) {
            throw var5;
        } catch (Exception var6) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + var6.getMessage(), var6);
        }
    }

    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        try {
            Object e = this.instantiate();
            return this.readObject(in, e, fieldNames);
        } catch (IOException var4) {
            throw var4;
        } catch (RuntimeException var5) {
            throw var5;
        } catch (Exception var6) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + var6.getMessage(), var6);
        }
    }

    protected Method getReadResolve(Class<?> cl) {
        while(cl != null) {
            Method[] methods = cl.getDeclaredMethods();

            for(int i = 0; i < methods.length; ++i) {
                Method method = methods[i];
                if(method.getName().equals("readResolve") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }

            cl = cl.getSuperclass();
        }

        return null;
    }

    public Object readMap(AbstractHessianInput in, Object obj) throws IOException {
        try {
            int e = in.addRef(obj);

            Object resolve;
            while(!in.isEnd()) {
                resolve = in.readObject();
                UnsafeDeserializer.FieldDeserializer deser = (UnsafeDeserializer.FieldDeserializer)this._fieldMap.get(resolve);
                if(deser != null) {
                    deser.deserialize(in, obj);
                } else {
                    in.readObject();
                }
            }

            in.readMapEnd();
            resolve = this.resolve(in, obj);
            if(obj != resolve) {
                in.setRef(e, resolve);
            }

            return resolve;
        } catch (IOException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new IOExceptionWrapper(var7);
        }
    }

    public Object readObject(AbstractHessianInput in, Object obj, UnsafeDeserializer.FieldDeserializer[] fields) throws IOException {
        try {
            int e = in.addRef(obj);
            UnsafeDeserializer.FieldDeserializer[] resolve = fields;
            int len$ = fields.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                UnsafeDeserializer.FieldDeserializer reader = resolve[i$];
                reader.deserialize(in, obj);
            }

            Object var11 = this.resolve(in, obj);
            if(obj != var11) {
                in.setRef(e, var11);
            }

            return var11;
        } catch (IOException var9) {
            throw var9;
        } catch (Exception var10) {
            throw new IOExceptionWrapper(obj.getClass().getName() + ":" + var10, var10);
        }
    }

    public Object readObject(AbstractHessianInput in, Object obj, String[] fieldNames) throws IOException {
        try {
            int e = in.addRef(obj);
            String[] resolve = fieldNames;
            int len$ = fieldNames.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String fieldName = resolve[i$];
                UnsafeDeserializer.FieldDeserializer reader = (UnsafeDeserializer.FieldDeserializer)this._fieldMap.get(fieldName);
                if(reader != null) {
                    reader.deserialize(in, obj);
                } else {
                    in.readObject();
                }
            }

            Object var12 = this.resolve(in, obj);
            if(obj != var12) {
                in.setRef(e, var12);
            }

            return var12;
        } catch (IOException var10) {
            throw var10;
        } catch (Exception var11) {
            throw new IOExceptionWrapper(obj.getClass().getName() + ":" + var11, var11);
        }
    }

    protected Object resolve(AbstractHessianInput in, Object obj) throws Exception {
        try {
            return this._readResolve != null?this._readResolve.invoke(obj, new Object[0]):obj;
        } catch (InvocationTargetException var4) {
            if(var4.getCause() instanceof Exception) {
                throw (Exception)var4.getCause();
            } else {
                throw var4;
            }
        }
    }

    protected Object instantiate() throws Exception {
        return _unsafe.allocateInstance(this._type);
    }

    protected HashMap<String, UnsafeDeserializer.FieldDeserializer> getFieldMap(Class<?> cl) {
        HashMap fieldMap;
        for(fieldMap = new HashMap(); cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();

            for(int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && fieldMap.get(field.getName()) == null) {
                    try {
                        field.setAccessible(true);
                    } catch (Throwable var8) {
                        var8.printStackTrace();
                    }

                    Class type = field.getType();
                    Object deser;
                    if(String.class.equals(type)) {
                        deser = new UnsafeDeserializer.StringFieldDeserializer(field);
                    } else if(Byte.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.ByteFieldDeserializer(field);
                    } else if(Character.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.CharFieldDeserializer(field);
                    } else if(Short.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.ShortFieldDeserializer(field);
                    } else if(Integer.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.IntFieldDeserializer(field);
                    } else if(Long.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.LongFieldDeserializer(field);
                    } else if(Float.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.FloatFieldDeserializer(field);
                    } else if(Double.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.DoubleFieldDeserializer(field);
                    } else if(Boolean.TYPE.equals(type)) {
                        deser = new UnsafeDeserializer.BooleanFieldDeserializer(field);
                    } else if(Date.class.equals(type)) {
                        deser = new UnsafeDeserializer.SqlDateFieldDeserializer(field);
                    } else if(Timestamp.class.equals(type)) {
                        deser = new UnsafeDeserializer.SqlTimestampFieldDeserializer(field);
                    } else if(Time.class.equals(type)) {
                        deser = new UnsafeDeserializer.SqlTimeFieldDeserializer(field);
                    } else {
                        deser = new UnsafeDeserializer.ObjectFieldDeserializer(field);
                    }

                    fieldMap.put(field.getName(), deser);
                }
            }
        }

        return fieldMap;
    }

    static void logDeserializeError(Field field, Object obj, Object value, Throwable e) throws IOException {
        String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
        if(e instanceof HessianFieldException) {
            throw (HessianFieldException)e;
        } else if(e instanceof IOException) {
            throw new HessianFieldException(fieldName + ": " + e.getMessage(), e);
        } else if(value != null) {
            throw new HessianFieldException(fieldName + ": " + value.getClass().getName() + " (" + value + ")" + " cannot be assigned to \'" + field.getType().getName() + "\'", e);
        } else {
            throw new HessianFieldException(fieldName + ": " + field.getType().getName() + " cannot be assigned from null", e);
        }
    }

    static {
        boolean isEnabled = false;

        try {
            Class e = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = null;
            Field[] unsafeProp = e.getDeclaredFields();
            int len$ = unsafeProp.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Field field = unsafeProp[i$];
                if(field.getName().equals("theUnsafe")) {
                    theUnsafe = field;
                }
            }

            if(theUnsafe != null) {
                theUnsafe.setAccessible(true);
                _unsafe = (Unsafe)theUnsafe.get((Object)null);
            }

            isEnabled = _unsafe != null;
            String var8 = System.getProperty("com.caucho.hessian.unsafe");
            if("false".equals(var8)) {
                isEnabled = false;
            }
        } catch (Throwable var7) {
            log.log(Level.FINER, var7.toString(), var7);
        }

        _isEnabled = isEnabled;
    }

    static class SqlTimeFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlTimeFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Time value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Time(e.getTime());
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, value);
                } else {
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, (Object)null);
                }
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class SqlTimestampFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlTimestampFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Timestamp value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Timestamp(e.getTime());
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, value);
                } else {
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, (Object)null);
                }
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class SqlDateFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        SqlDateFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Date value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Date(e.getTime());
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, value);
                } else {
                    UnsafeDeserializer._unsafe.putObject(obj, this._offset, (Object)null);
                }
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class StringFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        StringFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;

            try {
                value = in.readString();
                UnsafeDeserializer._unsafe.putObject(obj, this._offset, value);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class DoubleFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        DoubleFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0D;

            try {
                value = in.readDouble();
                UnsafeDeserializer._unsafe.putDouble(obj, this._offset, value);
            } catch (Exception var6) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), var6);
            }

        }
    }

    static class FloatFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        FloatFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0D;

            try {
                value = in.readDouble();
                UnsafeDeserializer._unsafe.putFloat(obj, this._offset, (float)value);
            } catch (Exception var6) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), var6);
            }

        }
    }

    static class LongFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        LongFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            long value = 0L;

            try {
                value = in.readLong();
                UnsafeDeserializer._unsafe.putLong(obj, this._offset, value);
            } catch (Exception var6) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Long.valueOf(value), var6);
            }

        }
    }

    static class IntFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        IntFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                UnsafeDeserializer._unsafe.putInt(obj, this._offset, value1);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class ShortFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ShortFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                UnsafeDeserializer._unsafe.putShort(obj, this._offset, (short)value1);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class CharFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        CharFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;

            try {
                value = in.readString();
                char e;
                if(value != null && value.length() > 0) {
                    e = value.charAt(0);
                } else {
                    e = 0;
                }

                UnsafeDeserializer._unsafe.putChar(obj, this._offset, e);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class ByteFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ByteFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                UnsafeDeserializer._unsafe.putByte(obj, this._offset, (byte)value1);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class BooleanFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        BooleanFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            boolean value = false;

            try {
                value = in.readBoolean();
                UnsafeDeserializer._unsafe.putBoolean(obj, this._offset, value);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, Boolean.valueOf(value), var5);
            }

        }
    }

    static class ObjectFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        private final Field _field;
        private final long _offset;

        ObjectFieldDeserializer(Field field) {
            this._field = field;
            this._offset = UnsafeDeserializer._unsafe.objectFieldOffset(this._field);
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;

            try {
                value = in.readObject(this._field.getType());
                UnsafeDeserializer._unsafe.putObject(obj, this._offset, value);
            } catch (Exception var5) {
                UnsafeDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class NullFieldDeserializer extends UnsafeDeserializer.FieldDeserializer {
        static UnsafeDeserializer.NullFieldDeserializer DESER = new UnsafeDeserializer.NullFieldDeserializer();

        NullFieldDeserializer() {
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            in.readObject();
        }
    }

    abstract static class FieldDeserializer {
        FieldDeserializer() {
        }

        abstract void deserialize(AbstractHessianInput var1, Object var2) throws IOException;
    }
}
