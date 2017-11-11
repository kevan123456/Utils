package com.ws.hessian.io;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

public class UnsafeSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(UnsafeSerializer.class.getName());
    private static boolean _isEnabled;
    private static final Unsafe _unsafe;
    private static final WeakHashMap<Class<?>, SoftReference<UnsafeSerializer>> _serializerMap = new WeakHashMap();
    private Field[] _fields;
    private UnsafeSerializer.FieldSerializer[] _fieldSerializers;

    public static boolean isEnabled() {
        return _isEnabled;
    }

    public UnsafeSerializer(Class<?> cl) {
        this.introspect(cl);
    }

    public static UnsafeSerializer create(Class<?> cl) {
        WeakHashMap var1 = _serializerMap;
        synchronized(_serializerMap) {
            SoftReference baseRef = (SoftReference)_serializerMap.get(cl);
            Object base = baseRef != null?(UnsafeSerializer)baseRef.get():null;
            if(base == null) {
                if(cl.isAnnotationPresent(HessianUnshared.class)) {
                    base = new UnsafeUnsharedSerializer(cl);
                } else {
                    base = new UnsafeSerializer(cl);
                }

                baseRef = new SoftReference(base);
                _serializerMap.put(cl, baseRef);
            }

            return (UnsafeSerializer)base;
        }
    }

    protected void introspect(Class<?> cl) {
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
        this._fieldSerializers = new UnsafeSerializer.FieldSerializer[this._fields.length];

        for(i = 0; i < this._fields.length; ++i) {
            this._fieldSerializers[i] = getFieldSerializer(this._fields[i]);
        }

    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if(!out.addRef(obj)) {
            Class cl = obj.getClass();
            int ref = out.writeObjectBegin(cl.getName());
            if(ref >= 0) {
                this.writeInstance(obj, out);
            } else if(ref == -1) {
                this.writeDefinition20(out);
                out.writeObjectBegin(cl.getName());
                this.writeInstance(obj, out);
            } else {
                this.writeObject10(obj, out);
            }

        }
    }

    protected void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for(int i = 0; i < this._fields.length; ++i) {
            Field field = this._fields[i];
            out.writeString(field.getName());
            this._fieldSerializers[i].serialize(out, obj);
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

    public final void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        try {
            UnsafeSerializer.FieldSerializer[] e = this._fieldSerializers;
            int length = e.length;

            for(int i = 0; i < length; ++i) {
                e[i].serialize(out, obj);
            }

        } catch (RuntimeException var6) {
            throw new RuntimeException(var6.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", var6);
        } catch (IOException var7) {
            throw new IOExceptionWrapper(var7.getMessage() + "\n class: " + obj.getClass().getName() + " (object=" + obj + ")", var7);
        }
    }

    private static UnsafeSerializer.FieldSerializer getFieldSerializer(Field field) {
        Class type = field.getType();
        return (UnsafeSerializer.FieldSerializer)(Boolean.TYPE.equals(type)?new UnsafeSerializer.BooleanFieldSerializer(field):(Byte.TYPE.equals(type)?new UnsafeSerializer.ByteFieldSerializer(field):(Character.TYPE.equals(type)?new UnsafeSerializer.CharFieldSerializer(field):(Short.TYPE.equals(type)?new UnsafeSerializer.ShortFieldSerializer(field):(Integer.TYPE.equals(type)?new UnsafeSerializer.IntFieldSerializer(field):(Long.TYPE.equals(type)?new UnsafeSerializer.LongFieldSerializer(field):(Double.TYPE.equals(type)?new UnsafeSerializer.DoubleFieldSerializer(field):(Float.TYPE.equals(type)?new UnsafeSerializer.FloatFieldSerializer(field):(String.class.equals(type)?new UnsafeSerializer.StringFieldSerializer(field):(!Date.class.equals(type) && !java.sql.Date.class.equals(type) && !Timestamp.class.equals(type) && !Time.class.equals(type)?new UnsafeSerializer.ObjectFieldSerializer(field):new UnsafeSerializer.DateFieldSerializer(field)))))))))));
    }

    static {
        boolean isEnabled = false;
        Unsafe unsafe = null;

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
                unsafe = (Unsafe)theUnsafe.get((Object)null);
            }

            isEnabled = unsafe != null;
            String var9 = System.getProperty("com.caucho.hessian.unsafe");
            if("false".equals(var9)) {
                isEnabled = false;
            }
        } catch (Throwable var8) {
            log.log(Level.ALL, var8.toString(), var8);
        }

        _unsafe = unsafe;
        _isEnabled = isEnabled;
    }

    static final class DateFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        DateFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            Date value = (Date)UnsafeSerializer._unsafe.getObject(obj, this._offset);
            if(value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }

        }
    }

    static final class StringFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        StringFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            String value = (String)UnsafeSerializer._unsafe.getObject(obj, this._offset);
            out.writeString(value);
        }
    }

    static final class DoubleFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        DoubleFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            double value = UnsafeSerializer._unsafe.getDouble(obj, this._offset);
            out.writeDouble(value);
        }
    }

    static final class FloatFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        FloatFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            double value = (double)UnsafeSerializer._unsafe.getFloat(obj, this._offset);
            out.writeDouble(value);
        }
    }

    static final class LongFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        LongFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            long value = UnsafeSerializer._unsafe.getLong(obj, this._offset);
            out.writeLong(value);
        }
    }

    static final class IntFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        IntFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            int value = UnsafeSerializer._unsafe.getInt(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class ShortFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        ShortFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            short value = UnsafeSerializer._unsafe.getShort(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class CharFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        CharFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            char value = UnsafeSerializer._unsafe.getChar(obj, this._offset);
            out.writeString(String.valueOf(value));
        }
    }

    static final class ByteFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        ByteFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            byte value = UnsafeSerializer._unsafe.getByte(obj, this._offset);
            out.writeInt(value);
        }
    }

    static final class BooleanFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        BooleanFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            boolean value = UnsafeSerializer._unsafe.getBoolean(obj, this._offset);
            out.writeBoolean(value);
        }
    }

    static final class ObjectFieldSerializer extends UnsafeSerializer.FieldSerializer {
        private final Field _field;
        private final long _offset;

        ObjectFieldSerializer(Field field) {
            this._field = field;
            this._offset = UnsafeSerializer._unsafe.objectFieldOffset(field);
            if(this._offset == -1L) {
                throw new IllegalStateException();
            }
        }

        final void serialize(AbstractHessianOutput out, Object obj) throws IOException {
            try {
                Object e = UnsafeSerializer._unsafe.getObject(obj, this._offset);
                out.writeObject(e);
            } catch (RuntimeException var4) {
                throw new RuntimeException(var4.getMessage() + "\n field: " + this._field.getDeclaringClass().getName() + '.' + this._field.getName(), var4);
            } catch (IOException var5) {
                throw new IOExceptionWrapper(var5.getMessage() + "\n field: " + this._field.getDeclaringClass().getName() + '.' + this._field.getName(), var5);
            }
        }
    }

    abstract static class FieldSerializer {
        FieldSerializer() {
        }

        abstract void serialize(AbstractHessianOutput var1, Object var2) throws IOException;
    }
}

