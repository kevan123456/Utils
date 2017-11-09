/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

public class JavaDeserializer extends AbstractMapDeserializer {
    private Class<?> _type;
    private HashMap<?, JavaDeserializer.FieldDeserializer> _fieldMap;
    private Method _readResolve;
    private Constructor<?> _constructor;
    private Object[] _constructorArgs;

    public JavaDeserializer(Class<?> cl) {
        this._type = cl;
        this._fieldMap = this.getFieldMap(cl);
        this._readResolve = this.getReadResolve(cl);
        if(this._readResolve != null) {
            this._readResolve.setAccessible(true);
        }

        Constructor[] constructors = cl.getDeclaredConstructors();
        long bestCost = 9223372036854775807L;

        for(int params = 0; params < constructors.length; ++params) {
            Class[] i = constructors[params].getParameterTypes();
            long cost = 0L;

            for(int j = 0; j < i.length; ++j) {
                cost = 4L * cost;
                if(Object.class.equals(i[j])) {
                    ++cost;
                } else if(String.class.equals(i[j])) {
                    cost += 2L;
                } else if(Integer.TYPE.equals(i[j])) {
                    cost += 3L;
                } else if(Long.TYPE.equals(i[j])) {
                    cost += 4L;
                } else if(i[j].isPrimitive()) {
                    cost += 5L;
                } else {
                    cost += 6L;
                }
            }

            if(cost < 0L || cost > 65536L) {
                cost = 65536L;
            }

            cost += (long)i.length << 48;
            if(cost < bestCost) {
                this._constructor = constructors[params];
                bestCost = cost;
            }
        }

        if(this._constructor != null) {
            this._constructor.setAccessible(true);
            Class[] var10 = this._constructor.getParameterTypes();
            this._constructorArgs = new Object[var10.length];

            for(int var11 = 0; var11 < var10.length; ++var11) {
                this._constructorArgs[var11] = getParamArg(var10[var11]);
            }
        }

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
        return new JavaDeserializer.FieldDeserializer[len];
    }

    public Object createField(String name) {
        Object reader = this._fieldMap.get(name);
        if(reader == null) {
            reader = JavaDeserializer.NullFieldDeserializer.DESER;
        }

        return reader;
    }

    public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        try {
            Object e = this.instantiate();
            return this.readObject(in, e, (JavaDeserializer.FieldDeserializer[])((JavaDeserializer.FieldDeserializer[])fields));
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
                JavaDeserializer.FieldDeserializer deser = (JavaDeserializer.FieldDeserializer)this._fieldMap.get(resolve);
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

    private Object readObject(AbstractHessianInput in, Object obj, JavaDeserializer.FieldDeserializer[] fields) throws IOException {
        try {
            int e = in.addRef(obj);
            JavaDeserializer.FieldDeserializer[] resolve = fields;
            int len$ = fields.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                JavaDeserializer.FieldDeserializer reader = resolve[i$];
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
                JavaDeserializer.FieldDeserializer reader = (JavaDeserializer.FieldDeserializer)this._fieldMap.get(fieldName);
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
        try {
            return this._constructor != null?this._constructor.newInstance(this._constructorArgs):this._type.newInstance();
        } catch (Exception var2) {
            throw new HessianProtocolException("\'" + this._type.getName() + "\' could not be instantiated", var2);
        }
    }

    protected HashMap<String, JavaDeserializer.FieldDeserializer> getFieldMap(Class cl) {
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
                        deser = new JavaDeserializer.StringFieldDeserializer(field);
                    } else if(Byte.TYPE.equals(type)) {
                        deser = new JavaDeserializer.ByteFieldDeserializer(field);
                    } else if(Short.TYPE.equals(type)) {
                        deser = new JavaDeserializer.ShortFieldDeserializer(field);
                    } else if(Integer.TYPE.equals(type)) {
                        deser = new JavaDeserializer.IntFieldDeserializer(field);
                    } else if(Long.TYPE.equals(type)) {
                        deser = new JavaDeserializer.LongFieldDeserializer(field);
                    } else if(Float.TYPE.equals(type)) {
                        deser = new JavaDeserializer.FloatFieldDeserializer(field);
                    } else if(Double.TYPE.equals(type)) {
                        deser = new JavaDeserializer.DoubleFieldDeserializer(field);
                    } else if(Boolean.TYPE.equals(type)) {
                        deser = new JavaDeserializer.BooleanFieldDeserializer(field);
                    } else if(Date.class.equals(type)) {
                        deser = new JavaDeserializer.SqlDateFieldDeserializer(field);
                    } else if(Timestamp.class.equals(type)) {
                        deser = new JavaDeserializer.SqlTimestampFieldDeserializer(field);
                    } else if(Time.class.equals(type)) {
                        deser = new JavaDeserializer.SqlTimeFieldDeserializer(field);
                    } else {
                        deser = new JavaDeserializer.ObjectFieldDeserializer(field);
                    }

                    fieldMap.put(field.getName(), deser);
                }
            }
        }

        return fieldMap;
    }

    protected static Object getParamArg(Class<?> cl) {
        if(!cl.isPrimitive()) {
            return null;
        } else if(Boolean.TYPE.equals(cl)) {
            return Boolean.FALSE;
        } else if(Byte.TYPE.equals(cl)) {
            return new Byte("0");
        } else if(Short.TYPE.equals(cl)) {
            return new Short("0");
        } else if(Character.TYPE.equals(cl)) {
            return new Character('\u0000');
        } else if(Integer.TYPE.equals(cl)) {
            return Integer.valueOf(0);
        } else if(Long.TYPE.equals(cl)) {
            return Long.valueOf(0L);
        } else if(Float.TYPE.equals(cl)) {
            return Float.valueOf(0.0F);
        } else if(Double.TYPE.equals(cl)) {
            return Double.valueOf(0.0D);
        } else {
            throw new UnsupportedOperationException();
        }
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

    static class SqlTimeFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        SqlTimeFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Time value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Time(e.getTime());
                    this._field.set(obj, value);
                } else {
                    this._field.set(obj, (Object)null);
                }
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class SqlTimestampFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        SqlTimestampFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Timestamp value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Timestamp(e.getTime());
                    this._field.set(obj, value);
                } else {
                    this._field.set(obj, (Object)null);
                }
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class SqlDateFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        SqlDateFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Date value = null;

            try {
                java.util.Date e = (java.util.Date)in.readObject();
                if(e != null) {
                    value = new Date(e.getTime());
                    this._field.set(obj, value);
                } else {
                    this._field.set(obj, (Object)null);
                }
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class StringFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        StringFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;

            try {
                value = in.readString();
                this._field.set(obj, value);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class DoubleFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        DoubleFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0D;

            try {
                value = in.readDouble();
                this._field.setDouble(obj, value);
            } catch (Exception var6) {
                JavaDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), var6);
            }

        }
    }

    static class FloatFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        FloatFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0D;

            try {
                value = in.readDouble();
                this._field.setFloat(obj, (float)value);
            } catch (Exception var6) {
                JavaDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), var6);
            }

        }
    }

    static class LongFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        LongFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            long value = 0L;

            try {
                value = in.readLong();
                this._field.setLong(obj, value);
            } catch (Exception var6) {
                JavaDeserializer.logDeserializeError(this._field, obj, Long.valueOf(value), var6);
            }

        }
    }

    static class IntFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        IntFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                this._field.setInt(obj, value1);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class ShortFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        ShortFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                this._field.setShort(obj, (short)value1);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class ByteFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        ByteFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            byte value = 0;

            try {
                int value1 = in.readInt();
                this._field.setByte(obj, (byte)value1);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), var5);
            }

        }
    }

    static class BooleanFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        BooleanFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            boolean value = false;

            try {
                value = in.readBoolean();
                this._field.setBoolean(obj, value);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, Boolean.valueOf(value), var5);
            }

        }
    }

    static class ObjectFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        private final Field _field;

        ObjectFieldDeserializer(Field field) {
            this._field = field;
        }

        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;

            try {
                value = in.readObject(this._field.getType());
                this._field.set(obj, value);
            } catch (Exception var5) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, var5);
            }

        }
    }

    static class NullFieldDeserializer extends JavaDeserializer.FieldDeserializer {
        static JavaDeserializer.NullFieldDeserializer DESER = new JavaDeserializer.NullFieldDeserializer();

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
