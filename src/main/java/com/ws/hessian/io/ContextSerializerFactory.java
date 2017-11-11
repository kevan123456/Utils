package com.ws.hessian.io;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextSerializerFactory {
    private static final Logger log = Logger.getLogger(ContextSerializerFactory.class.getName());
    private static Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(14);
    private static final WeakHashMap<ClassLoader, SoftReference<ContextSerializerFactory>> _contextRefMap = new WeakHashMap();
    private static final ClassLoader _systemClassLoader;
    private static HashMap<String, Serializer> _staticSerializerMap = new HashMap();
    private static HashMap<String, Deserializer> _staticDeserializerMap = new HashMap();
    private static HashMap _staticClassNameMap = new HashMap();
    private ContextSerializerFactory _parent;
    private WeakReference<ClassLoader> _loaderRef;
    private final HashSet<String> _serializerFiles = new HashSet();
    private final HashSet<String> _deserializerFiles = new HashSet();
    private final HashMap<String, Serializer> _serializerClassMap = new HashMap();
    private final ConcurrentHashMap<String, Serializer> _customSerializerMap = new ConcurrentHashMap();
    private final HashMap<Class<?>, Serializer> _serializerInterfaceMap = new HashMap();
    private final HashMap<String, Deserializer> _deserializerClassMap = new HashMap();
    private final HashMap<String, Deserializer> _deserializerClassNameMap = new HashMap();
    private final ConcurrentHashMap<String, Deserializer> _customDeserializerMap = new ConcurrentHashMap();
    private final HashMap<Class<?>, Deserializer> _deserializerInterfaceMap = new HashMap();

    public ContextSerializerFactory(ContextSerializerFactory parent, ClassLoader loader) {
        if(loader == null) {
            loader = _systemClassLoader;
        }

        this._loaderRef = new WeakReference(loader);
        this.init();
    }

    public static ContextSerializerFactory create() {
        return create(Thread.currentThread().getContextClassLoader());
    }

    public static ContextSerializerFactory create(ClassLoader loader) {
        WeakHashMap var1 = _contextRefMap;
        synchronized(_contextRefMap) {
            SoftReference factoryRef = (SoftReference)_contextRefMap.get(loader);
            ContextSerializerFactory factory = null;
            if(factoryRef != null) {
                factory = (ContextSerializerFactory)factoryRef.get();
            }

            if(factory == null) {
                ContextSerializerFactory parent = null;
                if(loader != null) {
                    parent = create(loader.getParent());
                }

                factory = new ContextSerializerFactory(parent, loader);
                factoryRef = new SoftReference(factory);
                _contextRefMap.put(loader, factoryRef);
            }

            return factory;
        }
    }

    public ClassLoader getClassLoader() {
        WeakReference loaderRef = this._loaderRef;
        return loaderRef != null?(ClassLoader)loaderRef.get():null;
    }

    public Serializer getSerializer(String className) {
        Serializer serializer = (Serializer)this._serializerClassMap.get(className);
        return serializer == AbstractSerializer.NULL?null:serializer;
    }

    public Serializer getCustomSerializer(Class cl) {
        Serializer serializer = (Serializer)this._customSerializerMap.get(cl.getName());
        if(serializer == AbstractSerializer.NULL) {
            return null;
        } else if(serializer != null) {
            return serializer;
        } else {
            try {
                Class e = Class.forName(cl.getName() + "HessianSerializer", false, cl.getClassLoader());
                Serializer ser = (Serializer)e.newInstance();
                this._customSerializerMap.put(cl.getName(), ser);
                return ser;
            } catch (ClassNotFoundException var5) {
                log.log(Level.ALL, var5.toString(), var5);
                this._customSerializerMap.put(cl.getName(), AbstractSerializer.NULL);
                return null;
            } catch (Exception var6) {
                throw new HessianException(var6);
            }
        }
    }

    public Deserializer getDeserializer(String className) {
        Deserializer deserializer = (Deserializer)this._deserializerClassMap.get(className);
        return deserializer == AbstractDeserializer.NULL?null:deserializer;
    }

    public Deserializer getCustomDeserializer(Class cl) {
        Deserializer deserializer = (Deserializer)this._customDeserializerMap.get(cl.getName());
        if(deserializer == AbstractDeserializer.NULL) {
            return null;
        } else if(deserializer != null) {
            return deserializer;
        } else {
            try {
                Class e = Class.forName(cl.getName() + "HessianDeserializer", false, cl.getClassLoader());
                Deserializer ser = (Deserializer)e.newInstance();
                this._customDeserializerMap.put(cl.getName(), ser);
                return ser;
            } catch (ClassNotFoundException var5) {
                log.log(Level.ALL, var5.toString(), var5);
                this._customDeserializerMap.put(cl.getName(), AbstractDeserializer.NULL);
                return null;
            } catch (Exception var6) {
                throw new HessianException(var6);
            }
        }
    }

    private void init() {
        if(this._parent != null) {
            this._serializerFiles.addAll(this._parent._serializerFiles);
            this._deserializerFiles.addAll(this._parent._deserializerFiles);
            this._serializerClassMap.putAll(this._parent._serializerClassMap);
            this._deserializerClassMap.putAll(this._parent._deserializerClassMap);
        }

        if(this._parent == null) {
            this._serializerClassMap.putAll(_staticSerializerMap);
            this._deserializerClassMap.putAll(_staticDeserializerMap);
            this._deserializerClassNameMap.putAll(_staticClassNameMap);
        }

        HashMap classMap = new HashMap();
        this.initSerializerFiles("META-INF/hessian/serializers", this._serializerFiles, classMap, Serializer.class);
        Iterator i$ = classMap.entrySet().iterator();

        Entry entry;
        while(i$.hasNext()) {
            entry = (Entry)i$.next();

            try {
                Serializer e = (Serializer)((Class)entry.getValue()).newInstance();
                if(((Class)entry.getKey()).isInterface()) {
                    this._serializerInterfaceMap.put((Class)entry.getKey(), e);
                } else {
                    this._serializerClassMap.put(((Class)entry.getKey()).getName(), e);
                }
            } catch (Exception var6) {
                throw new HessianException(var6);
            }
        }

        classMap = new HashMap();
        this.initSerializerFiles("META-INF/hessian/deserializers", this._deserializerFiles, classMap, Deserializer.class);
        i$ = classMap.entrySet().iterator();

        while(i$.hasNext()) {
            entry = (Entry)i$.next();

            try {
                Deserializer e1 = (Deserializer)((Class)entry.getValue()).newInstance();
                if(((Class)entry.getKey()).isInterface()) {
                    this._deserializerInterfaceMap.put((Class)entry.getKey(), e1);
                } else {
                    this._deserializerClassMap.put(((Class)entry.getKey()).getName(), e1);
                }
            } catch (Exception var5) {
                throw new HessianException(var5);
            }
        }

    }

    private void initSerializerFiles(String fileName, HashSet<String> fileList, HashMap<Class, Class> classMap, Class type) {
        try {
            ClassLoader e = this.getClassLoader();
            if(e != null) {
                Enumeration iter = e.getResources(fileName);

                while(true) {
                    URL url;
                    do {
                        if(!iter.hasMoreElements()) {
                            return;
                        }

                        url = (URL)iter.nextElement();
                    } while(fileList.contains(url.toString()));

                    fileList.add(url.toString());
                    InputStream is = null;

                    try {
                        is = url.openStream();
                        Properties props = new Properties();
                        props.load(is);
                        Iterator i$ = props.entrySet().iterator();

                        while(i$.hasNext()) {
                            Entry entry = (Entry)i$.next();
                            String apiName = (String)entry.getKey();
                            String serializerName = (String)entry.getValue();
                            Class apiClass = null;
                            Class serializerClass = null;

                            try {
                                apiClass = Class.forName(apiName, false, e);
                            } catch (ClassNotFoundException var24) {
                                log.fine(url + ": " + apiName + " is not available in this context: " + this.getClassLoader());
                                continue;
                            }

                            try {
                                serializerClass = Class.forName(serializerName, false, e);
                            } catch (ClassNotFoundException var23) {
                                log.fine(url + ": " + serializerName + " is not available in this context: " + this.getClassLoader());
                                continue;
                            }

                            if(!type.isAssignableFrom(serializerClass)) {
                                throw new HessianException(url + ": " + serializerClass.getName() + " is invalid because it does not implement " + type.getName());
                            }

                            classMap.put(apiClass, serializerClass);
                        }
                    } finally {
                        if(is != null) {
                            is.close();
                        }

                    }
                }
            }
        } catch (RuntimeException var26) {
            throw var26;
        } catch (Exception var27) {
            throw new HessianException(var27);
        }
    }

    private static void addBasic(Class cl, String typeName, int type) {
        _staticSerializerMap.put(cl.getName(), new BasicSerializer(type));
        BasicDeserializer deserializer = new BasicDeserializer(type);
        _staticDeserializerMap.put(cl.getName(), deserializer);
        _staticClassNameMap.put(typeName, deserializer);
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
        _staticSerializerMap.put(byte[].class.getName(), ByteArraySerializer.SER);
        addBasic(short[].class, "[short", 17);
        addBasic(int[].class, "[int", 18);
        addBasic(long[].class, "[long", 19);
        addBasic(float[].class, "[float", 20);
        addBasic(double[].class, "[double", 21);
        addBasic(char[].class, "[char", 22);
        addBasic(String[].class, "[string", 23);
        addBasic(Object[].class, "[object", 24);
        JavaDeserializer objectDeserializer = new JavaDeserializer(Object.class);
        _staticDeserializerMap.put("object", objectDeserializer);
        _staticClassNameMap.put("object", objectDeserializer);
        _staticSerializerMap.put(Class.class.getName(), new ClassSerializer());
        _staticDeserializerMap.put(Number.class.getName(), new BasicDeserializer(13));
        _staticSerializerMap.put(InetAddress.class.getName(), InetAddressSerializer.create());
        _staticSerializerMap.put(java.sql.Date.class.getName(), new SqlDateSerializer());
        _staticSerializerMap.put(Time.class.getName(), new SqlDateSerializer());
        _staticSerializerMap.put(Timestamp.class.getName(), new SqlDateSerializer());
        _staticDeserializerMap.put(java.sql.Date.class.getName(), new SqlDateDeserializer(java.sql.Date.class));
        _staticDeserializerMap.put(Time.class.getName(), new SqlDateDeserializer(Time.class));
        _staticDeserializerMap.put(Timestamp.class.getName(), new SqlDateDeserializer(Timestamp.class));
        _staticDeserializerMap.put(StackTraceElement.class.getName(), new StackTraceElementDeserializer());
        ClassLoader systemClassLoader = null;

        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (Exception var3) {
            ;
        }

        _systemClassLoader = systemClassLoader;
    }
}
