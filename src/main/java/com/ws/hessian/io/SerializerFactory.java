package com.ws.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerializerFactory extends AbstractSerializerFactory {
    private static final Logger log = Logger.getLogger(SerializerFactory.class.getName());
    private static final Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(14);
    private static final ClassLoader _systemClassLoader;
    private static final HashMap _staticTypeMap = new HashMap();
    private static final WeakHashMap<ClassLoader, SoftReference<SerializerFactory>> _defaultFactoryRefMap = new WeakHashMap();
    private ContextSerializerFactory _contextFactory;
    private WeakReference<ClassLoader> _loaderRef;
    protected Serializer _defaultSerializer;
    protected ArrayList _factories;
    protected CollectionSerializer _collectionSerializer;
    protected MapSerializer _mapSerializer;
    private Deserializer _hashMapDeserializer;
    private Deserializer _arrayListDeserializer;
    private ConcurrentHashMap _cachedSerializerMap;
    private ConcurrentHashMap _cachedDeserializerMap;
    private HashMap _cachedTypeDeserializerMap;
    private boolean _isAllowNonSerializable;
    private boolean _isEnableUnsafeSerializer;

    public SerializerFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public SerializerFactory(ClassLoader loader) {
        this._factories = new ArrayList();
        this._isEnableUnsafeSerializer = UnsafeSerializer.isEnabled() && UnsafeDeserializer.isEnabled();
        this._loaderRef = new WeakReference(loader);
        this._contextFactory = ContextSerializerFactory.create(loader);
    }

    public static SerializerFactory createDefault() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        WeakHashMap var1 = _defaultFactoryRefMap;
        synchronized(_defaultFactoryRefMap) {
            SoftReference factoryRef = (SoftReference)_defaultFactoryRefMap.get(loader);
            SerializerFactory factory = null;
            if(factoryRef != null) {
                factory = (SerializerFactory)factoryRef.get();
            }

            if(factory == null) {
                factory = new SerializerFactory();
                factoryRef = new SoftReference(factory);
                _defaultFactoryRefMap.put(loader, factoryRef);
            }

            return factory;
        }
    }

    public ClassLoader getClassLoader() {
        return (ClassLoader)this._loaderRef.get();
    }

    public void setSendCollectionType(boolean isSendType) {
        if(this._collectionSerializer == null) {
            this._collectionSerializer = new CollectionSerializer();
        }

        this._collectionSerializer.setSendJavaType(isSendType);
        if(this._mapSerializer == null) {
            this._mapSerializer = new MapSerializer();
        }

        this._mapSerializer.setSendJavaType(isSendType);
    }

    public void addFactory(AbstractSerializerFactory factory) {
        this._factories.add(factory);
    }

    public void setAllowNonSerializable(boolean allow) {
        this._isAllowNonSerializable = allow;
    }

    public boolean isAllowNonSerializable() {
        return this._isAllowNonSerializable;
    }

    public Serializer getObjectSerializer(Class<?> cl) throws HessianProtocolException {
        Serializer serializer = this.getSerializer(cl);
        return serializer instanceof ObjectSerializer?((ObjectSerializer)serializer).getObjectSerializer():serializer;
    }

    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        Serializer serializer;
        if(this._cachedSerializerMap != null) {
            serializer = (Serializer)this._cachedSerializerMap.get(cl);
            if(serializer != null) {
                return serializer;
            }
        }

        serializer = this.loadSerializer(cl);
        if(this._cachedSerializerMap == null) {
            this._cachedSerializerMap = new ConcurrentHashMap(8);
        }

        this._cachedSerializerMap.put(cl, serializer);
        return serializer;
    }

    protected Serializer loadSerializer(Class<?> cl) throws HessianProtocolException {
        Serializer serializer = null;

        AbstractSerializerFactory factory;
        for(int loader = 0; this._factories != null && loader < this._factories.size(); ++loader) {
            factory = (AbstractSerializerFactory)this._factories.get(loader);
            serializer = factory.getSerializer(cl);
            if(serializer != null) {
                return serializer;
            }
        }

        serializer = this._contextFactory.getSerializer(cl.getName());
        if(serializer != null) {
            return serializer;
        } else {
            ClassLoader var6 = cl.getClassLoader();
            if(var6 == null) {
                var6 = _systemClassLoader;
            }

            factory = null;
            ContextSerializerFactory var7 = ContextSerializerFactory.create(var6);
            serializer = var7.getCustomSerializer(cl);
            if(serializer != null) {
                return serializer;
            } else if(HessianRemoteObject.class.isAssignableFrom(cl)) {
                return new RemoteSerializer();
            } else if(BurlapRemoteObject.class.isAssignableFrom(cl)) {
                return new RemoteSerializer();
            } else if(InetAddress.class.isAssignableFrom(cl)) {
                return InetAddressSerializer.create();
            } else if(JavaSerializer.getWriteReplace(cl) != null) {
                Serializer baseSerializer = this.getDefaultSerializer(cl);
                return new WriteReplaceSerializer(cl, this.getClassLoader(), baseSerializer);
            } else if(Map.class.isAssignableFrom(cl)) {
                if(this._mapSerializer == null) {
                    this._mapSerializer = new MapSerializer();
                }

                return this._mapSerializer;
            } else if(Collection.class.isAssignableFrom(cl)) {
                if(this._collectionSerializer == null) {
                    this._collectionSerializer = new CollectionSerializer();
                }

                return this._collectionSerializer;
            } else {
                return (Serializer)(cl.isArray()?new ArraySerializer():(Throwable.class.isAssignableFrom(cl)?new ThrowableSerializer(cl, this.getClassLoader()):(InputStream.class.isAssignableFrom(cl)?new InputStreamSerializer():(Iterator.class.isAssignableFrom(cl)?IteratorSerializer.create():(Calendar.class.isAssignableFrom(cl)?CalendarSerializer.SER:(Enumeration.class.isAssignableFrom(cl)?EnumerationSerializer.create():(Enum.class.isAssignableFrom(cl)?new EnumSerializer(cl):(Annotation.class.isAssignableFrom(cl)?new AnnotationSerializer(cl):this.getDefaultSerializer(cl)))))))));
            }
        }
    }

    protected Serializer getDefaultSerializer(Class cl) {
        if(this._defaultSerializer != null) {
            return this._defaultSerializer;
        } else if(!Serializable.class.isAssignableFrom(cl) && !this._isAllowNonSerializable) {
            throw new IllegalStateException("Serialized class " + cl.getName() + " must implement java.io.Serializable");
        } else {
            return (Serializer)(this._isEnableUnsafeSerializer && JavaSerializer.getWriteReplace(cl) == null?UnsafeSerializer.create(cl):JavaSerializer.create(cl));
        }
    }

    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer;
        if(this._cachedDeserializerMap != null) {
            deserializer = (Deserializer)this._cachedDeserializerMap.get(cl);
            if(deserializer != null) {
                return deserializer;
            }
        }

        deserializer = this.loadDeserializer(cl);
        if(this._cachedDeserializerMap == null) {
            this._cachedDeserializerMap = new ConcurrentHashMap(8);
        }

        this._cachedDeserializerMap.put(cl, deserializer);
        return deserializer;
    }

    protected Deserializer loadDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer = null;

        for(int factory = 0; deserializer == null && this._factories != null && factory < this._factories.size(); ++factory) {
            AbstractSerializerFactory factory1 = (AbstractSerializerFactory)this._factories.get(factory);
            deserializer = factory1.getDeserializer(cl);
        }

        if(deserializer != null) {
            return deserializer;
        } else {
            deserializer = this._contextFactory.getDeserializer(cl.getName());
            if(deserializer != null) {
                return deserializer;
            } else {
                ContextSerializerFactory var6 = null;
                if(cl.getClassLoader() != null) {
                    var6 = ContextSerializerFactory.create(cl.getClassLoader());
                } else {
                    var6 = ContextSerializerFactory.create(_systemClassLoader);
                }

                deserializer = var6.getCustomDeserializer(cl);
                if(deserializer != null) {
                    return deserializer;
                } else {
                    Object var5;
                    if(Collection.class.isAssignableFrom(cl)) {
                        var5 = new CollectionDeserializer(cl);
                    } else if(Map.class.isAssignableFrom(cl)) {
                        var5 = new MapDeserializer(cl);
                    } else if(Iterator.class.isAssignableFrom(cl)) {
                        var5 = IteratorDeserializer.create();
                    } else if(Annotation.class.isAssignableFrom(cl)) {
                        var5 = new AnnotationDeserializer(cl);
                    } else if(cl.isInterface()) {
                        var5 = new ObjectDeserializer(cl);
                    } else if(cl.isArray()) {
                        var5 = new ArrayDeserializer(cl.getComponentType());
                    } else if(Enumeration.class.isAssignableFrom(cl)) {
                        var5 = EnumerationDeserializer.create();
                    } else if(Enum.class.isAssignableFrom(cl)) {
                        var5 = new EnumDeserializer(cl);
                    } else if(Class.class.equals(cl)) {
                        var5 = new ClassDeserializer(this.getClassLoader());
                    } else {
                        var5 = this.getDefaultDeserializer(cl);
                    }

                    return (Deserializer)var5;
                }
            }
        }
    }

    protected Deserializer getCustomDeserializer(Class cl) {
        try {
            Class e = Class.forName(cl.getName() + "HessianDeserializer", false, cl.getClassLoader());
            Deserializer ser = (Deserializer)e.newInstance();
            return ser;
        } catch (ClassNotFoundException var4) {
            log.log(Level.FINEST, var4.toString(), var4);
            return null;
        } catch (Exception var5) {
            log.log(Level.FINE, var5.toString(), var5);
            return null;
        }
    }

    protected Deserializer getDefaultDeserializer(Class cl) {
        return (Deserializer)(InputStream.class.equals(cl)?InputStreamDeserializer.DESER:(this._isEnableUnsafeSerializer?new UnsafeDeserializer(cl):new JavaDeserializer(cl)));
    }

    public Object readList(AbstractHessianInput in, int length, String type) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        return deserializer != null?deserializer.readList(in, length):(new CollectionDeserializer(ArrayList.class)).readList(in, length);
    }

    public Object readMap(AbstractHessianInput in, String type) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        if(deserializer != null) {
            return deserializer.readMap(in);
        } else if(this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readMap(in);
        } else {
            this._hashMapDeserializer = new MapDeserializer(HashMap.class);
            return this._hashMapDeserializer.readMap(in);
        }
    }

    public Object readObject(AbstractHessianInput in, String type, String[] fieldNames) throws HessianProtocolException, IOException {
        Deserializer deserializer = this.getDeserializer(type);
        if(deserializer != null) {
            return deserializer.readObject(in, fieldNames);
        } else if(this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readObject(in, fieldNames);
        } else {
            this._hashMapDeserializer = new MapDeserializer(HashMap.class);
            return this._hashMapDeserializer.readObject(in, fieldNames);
        }
    }

    public Deserializer getObjectDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = this.getObjectDeserializer(type);
        if(cl != null && !cl.equals(reader.getType()) && !cl.isAssignableFrom(reader.getType()) && !reader.isReadResolve() && !HessianHandle.class.isAssignableFrom(reader.getType())) {
            if(log.isLoggable(Level.FINE)) {
                log.fine("hessian: expected deserializer \'" + cl.getName() + "\' at \'" + type + "\' (" + reader.getType().getName() + ")");
            }

            return this.getDeserializer(cl);
        } else {
            return reader;
        }
    }

    public Deserializer getObjectDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = this.getDeserializer(type);
        if(deserializer != null) {
            return deserializer;
        } else if(this._hashMapDeserializer != null) {
            return this._hashMapDeserializer;
        } else {
            this._hashMapDeserializer = new MapDeserializer(HashMap.class);
            return this._hashMapDeserializer;
        }
    }

    public Deserializer getListDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = this.getListDeserializer(type);
        if(cl != null && !cl.equals(reader.getType()) && !cl.isAssignableFrom(reader.getType())) {
            if(log.isLoggable(Level.FINE)) {
                log.fine("hessian: expected \'" + cl.getName() + "\' at \'" + type + "\' (" + reader.getType().getName() + ")");
            }

            return this.getDeserializer(cl);
        } else {
            return reader;
        }
    }

    public Deserializer getListDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = this.getDeserializer(type);
        if(deserializer != null) {
            return deserializer;
        } else if(this._arrayListDeserializer != null) {
            return this._arrayListDeserializer;
        } else {
            this._arrayListDeserializer = new CollectionDeserializer(ArrayList.class);
            return this._arrayListDeserializer;
        }
    }

    public Deserializer getDeserializer(String type) throws HessianProtocolException {
        if(type != null && !type.equals("")) {
            HashMap e;
            if(this._cachedTypeDeserializerMap != null) {
                e = this._cachedTypeDeserializerMap;
                Deserializer deserializer;
                synchronized(this._cachedTypeDeserializerMap) {
                    deserializer = (Deserializer)this._cachedTypeDeserializerMap.get(type);
                }

                if(deserializer != null) {
                    return deserializer;
                }
            }

            Object deserializer1 = (Deserializer)_staticTypeMap.get(type);
            if(deserializer1 != null) {
                return (Deserializer)deserializer1;
            } else {
                if(type.startsWith("[")) {
                    Deserializer e1 = this.getDeserializer(type.substring(1));
                    if(e1 != null) {
                        deserializer1 = new ArrayDeserializer(e1.getType());
                    } else {
                        deserializer1 = new ArrayDeserializer(Object.class);
                    }
                } else {
                    try {
                        Class e2 = Class.forName(type, false, this.getClassLoader());
                        deserializer1 = this.getDeserializer(e2);
                    } catch (Exception var7) {
                        log.warning("Hessian/Burlap: \'" + type + "\' is an unknown class in " + this.getClassLoader() + ":\n" + var7);
                        log.log(Level.FINER, var7.toString(), var7);
                    }
                }

                if(deserializer1 != null) {
                    if(this._cachedTypeDeserializerMap == null) {
                        this._cachedTypeDeserializerMap = new HashMap(8);
                    }

                    e = this._cachedTypeDeserializerMap;
                    synchronized(this._cachedTypeDeserializerMap) {
                        this._cachedTypeDeserializerMap.put(type, deserializer1);
                    }
                }

                return (Deserializer)deserializer1;
            }
        } else {
            return null;
        }
    }

    private static void addBasic(Class<?> cl, String typeName, int type) {
        BasicDeserializer deserializer = new BasicDeserializer(type);
        _staticTypeMap.put(typeName, deserializer);
    }

    static {
        addBasic(Void.TYPE, "void", 0);
        addBasic(Boolean.class, "boolean", 1);
        addBasic(Byte.class, "byte", 2);
        addBasic(Short.class, "short", 3);
        addBasic(Integer.class, "int", 4);
        addBasic(Long.class, "long", 5);
        addBasic(Float.class, "float", 6);
        addBasic(Double.class, "double", 7);
        addBasic(Character.class, "char", 9);
        addBasic(String.class, "string", 10);
        addBasic(StringBuilder.class, "string", 11);
        addBasic(Object.class, "object", 14);
        addBasic(Date.class, "date", 12);
        addBasic(Boolean.TYPE, "boolean", 1);
        addBasic(Byte.TYPE, "byte", 2);
        addBasic(Short.TYPE, "short", 3);
        addBasic(Integer.TYPE, "int", 4);
        addBasic(Long.TYPE, "long", 5);
        addBasic(Float.TYPE, "float", 6);
        addBasic(Double.TYPE, "double", 7);
        addBasic(Character.TYPE, "char", 8);
        addBasic(boolean[].class, "[boolean", 15);
        addBasic(byte[].class, "[byte", 16);
        addBasic(short[].class, "[short", 17);
        addBasic(int[].class, "[int", 18);
        addBasic(long[].class, "[long", 19);
        addBasic(float[].class, "[float", 20);
        addBasic(double[].class, "[double", 21);
        addBasic(char[].class, "[char", 22);
        addBasic(String[].class, "[string", 23);
        addBasic(Object[].class, "[object", 24);
        JavaDeserializer objectDeserializer = new JavaDeserializer(Object.class);
        _staticTypeMap.put("object", objectDeserializer);
        _staticTypeMap.put(HessianRemote.class.getName(), RemoteDeserializer.DESER);
        ClassLoader systemClassLoader = null;

        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (Exception var3) {
            ;
        }

        _systemClassLoader = systemClassLoader;
    }
}
