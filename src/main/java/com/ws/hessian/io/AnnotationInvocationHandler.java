/*
 * Copyright (C) 2009-2016 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.ws.hessian.io;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class AnnotationInvocationHandler implements InvocationHandler {
    private Class _annType;
    private HashMap<String, Object> _valueMap;

    public AnnotationInvocationHandler(Class annType, HashMap<String, Object> valueMap) {
        this._annType = annType;
        this._valueMap = valueMap;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        boolean zeroArgs = args == null || args.length == 0;
        return name.equals("annotationType") && zeroArgs?this._annType:(name.equals("toString") && zeroArgs?this.toString():(name.equals("hashCode") && zeroArgs?Integer.valueOf(this.doHashCode()):(name.equals("equals") && !zeroArgs && args.length == 1?Boolean.valueOf(this.doEquals(args[0])):(!zeroArgs?null:this._valueMap.get(method.getName())))));
    }

    public int doHashCode() {
        return 13;
    }

    public boolean doEquals(Object value) {
        if(!(value instanceof Annotation)) {
            return false;
        } else {
            Annotation ann = (Annotation)value;
            return this._annType.equals(ann.annotationType());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(this._annType.getName());
        sb.append("[");
        boolean isFirst = true;
        Iterator i$ = this._valueMap.entrySet().iterator();

        while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();
            if(!isFirst) {
                sb.append(", ");
            }

            isFirst = false;
            sb.append(entry.getKey());
            sb.append("=");
            if(entry.getValue() instanceof String) {
                sb.append('\"').append(entry.getValue()).append('\"');
            } else {
                sb.append(entry.getValue());
            }
        }

        sb.append("]");
        return sb.toString();
    }
}

