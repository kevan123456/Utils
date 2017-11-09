package com.ws.hessian.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.IdentityHashMap;

public class HessianOutput extends AbstractHessianOutput {
    protected OutputStream os;
    private IdentityHashMap _refs;
    private int _version = 1;

    public HessianOutput(OutputStream os) {
        this.init(os);
    }

    public HessianOutput() {
    }

    public void init(OutputStream os) {
        this.os = os;
        this._refs = null;
        if(this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }

    }

    public void setVersion(int version) {
        this._version = version;
    }

    public void call(String method, Object[] args) throws IOException {
        int length = args != null?args.length:0;
        this.startCall(method, length);

        for(int i = 0; i < length; ++i) {
            this.writeObject(args[i]);
        }

        this.completeCall();
    }

    public void startCall(String method, int length) throws IOException {
        this.os.write(99);
        this.os.write(this._version);
        this.os.write(0);
        this.os.write(109);
        int len = method.length();
        this.os.write(len >> 8);
        this.os.write(len);
        this.printString((String)method, 0, len);
    }

    public void startCall() throws IOException {
        this.os.write(99);
        this.os.write(0);
        this.os.write(1);
    }

    public void writeMethod(String method) throws IOException {
        this.os.write(109);
        int len = method.length();
        this.os.write(len >> 8);
        this.os.write(len);
        this.printString((String)method, 0, len);
    }

    public void completeCall() throws IOException {
        this.os.write(122);
    }

    public void startReply() throws IOException {
        this.os.write(114);
        this.os.write(1);
        this.os.write(0);
    }

    public void completeReply() throws IOException {
        this.os.write(122);
    }

    public void writeHeader(String name) throws IOException {
        int len = name.length();
        this.os.write(72);
        this.os.write(len >> 8);
        this.os.write(len);
        this.printString(name);
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
        this.os.write(114);
        this.os.write(1);
        this.os.write(0);
        this.os.write(102);
        this.writeString("code");
        this.writeString(code);
        this.writeString("message");
        this.writeString(message);
        if(detail != null) {
            this.writeString("detail");
            this.writeObject(detail);
        }

        this.os.write(122);
        this.os.write(122);
    }

    public void writeObject(Object object) throws IOException {
        if(object == null) {
            this.writeNull();
        } else {
            Serializer serializer = this._serializerFactory.getSerializer(object.getClass());
            serializer.writeObject(object, this);
        }
    }

    public boolean writeListBegin(int length, String type) throws IOException {
        this.os.write(86);
        if(type != null) {
            this.os.write(116);
            this.printLenString(type);
        }

        if(length >= 0) {
            this.os.write(108);
            this.os.write(length >> 24);
            this.os.write(length >> 16);
            this.os.write(length >> 8);
            this.os.write(length);
        }

        return true;
    }

    public void writeListEnd() throws IOException {
        this.os.write(122);
    }

    public void writeMapBegin(String type) throws IOException {
        this.os.write(77);
        this.os.write(116);
        this.printLenString(type);
    }

    public void writeMapEnd() throws IOException {
        this.os.write(122);
    }

    public void writeRemote(String type, String url) throws IOException {
        this.os.write(114);
        this.os.write(116);
        this.printLenString(type);
        this.os.write(83);
        this.printLenString(url);
    }

    public void writeBoolean(boolean value) throws IOException {
        if(value) {
            this.os.write(84);
        } else {
            this.os.write(70);
        }

    }

    public void writeInt(int value) throws IOException {
        this.os.write(73);
        this.os.write(value >> 24);
        this.os.write(value >> 16);
        this.os.write(value >> 8);
        this.os.write(value);
    }

    public void writeLong(long value) throws IOException {
        this.os.write(76);
        this.os.write((byte)((int)(value >> 56)));
        this.os.write((byte)((int)(value >> 48)));
        this.os.write((byte)((int)(value >> 40)));
        this.os.write((byte)((int)(value >> 32)));
        this.os.write((byte)((int)(value >> 24)));
        this.os.write((byte)((int)(value >> 16)));
        this.os.write((byte)((int)(value >> 8)));
        this.os.write((byte)((int)value));
    }

    public void writeDouble(double value) throws IOException {
        long bits = Double.doubleToLongBits(value);
        this.os.write(68);
        this.os.write((byte)((int)(bits >> 56)));
        this.os.write((byte)((int)(bits >> 48)));
        this.os.write((byte)((int)(bits >> 40)));
        this.os.write((byte)((int)(bits >> 32)));
        this.os.write((byte)((int)(bits >> 24)));
        this.os.write((byte)((int)(bits >> 16)));
        this.os.write((byte)((int)(bits >> 8)));
        this.os.write((byte)((int)bits));
    }

    public void writeUTCDate(long time) throws IOException {
        this.os.write(100);
        this.os.write((byte)((int)(time >> 56)));
        this.os.write((byte)((int)(time >> 48)));
        this.os.write((byte)((int)(time >> 40)));
        this.os.write((byte)((int)(time >> 32)));
        this.os.write((byte)((int)(time >> 24)));
        this.os.write((byte)((int)(time >> 16)));
        this.os.write((byte)((int)(time >> 8)));
        this.os.write((byte)((int)time));
    }

    public void writeNull() throws IOException {
        this.os.write(78);
    }

    public void writeString(String value) throws IOException {
        if(value == null) {
            this.os.write(78);
        } else {
            int length = value.length();

            int offset;
            int sublen;
            for(offset = 0; length > '耀'; offset += sublen) {
                sublen = '耀';
                char tail = value.charAt(offset + sublen - 1);
                if('\ud800' <= tail && tail <= '\udbff') {
                    --sublen;
                }

                this.os.write(115);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                this.printString(value, offset, sublen);
                length -= sublen;
            }

            this.os.write(83);
            this.os.write(length >> 8);
            this.os.write(length);
            this.printString(value, offset, length);
        }

    }

    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if(buffer == null) {
            this.os.write(78);
        } else {
            while(true) {
                if(length <= '耀') {
                    this.os.write(83);
                    this.os.write(length >> 8);
                    this.os.write(length);
                    this.printString(buffer, offset, length);
                    break;
                }

                int sublen = '耀';
                char tail = buffer[offset + sublen - 1];
                if('\ud800' <= tail && tail <= '\udbff') {
                    --sublen;
                }

                this.os.write(115);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                this.printString(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
        }

    }

    public void writeBytes(byte[] buffer) throws IOException {
        if(buffer == null) {
            this.os.write(78);
        } else {
            this.writeBytes(buffer, 0, buffer.length);
        }

    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if(buffer == null) {
            this.os.write(78);
        } else {
            while(true) {
                if(length <= '耀') {
                    this.os.write(66);
                    this.os.write(length >> 8);
                    this.os.write(length);
                    this.os.write(buffer, offset, length);
                    break;
                }

                char sublen = '耀';
                this.os.write(98);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                this.os.write(buffer, offset, sublen);
                length -= sublen;
                offset += sublen;
            }
        }

    }

    public void writeByteBufferStart() throws IOException {
    }

    public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
        while(length > 0) {
            int sublen = length;
            if('耀' < length) {
                sublen = '耀';
            }

            this.os.write(98);
            this.os.write(sublen >> 8);
            this.os.write(sublen);
            this.os.write(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }

    }

    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        this.writeBytes(buffer, offset, length);
    }

    public void writeRef(int value) throws IOException {
        this.os.write(82);
        this.os.write(value >> 24);
        this.os.write(value >> 16);
        this.os.write(value >> 8);
        this.os.write(value);
    }

    public void writePlaceholder() throws IOException {
        this.os.write(80);
    }

    public boolean addRef(Object object) throws IOException {
        if(this._refs == null) {
            this._refs = new IdentityHashMap();
        }

        Integer ref = (Integer)this._refs.get(object);
        if(ref != null) {
            int value = ref.intValue();
            this.writeRef(value);
            return true;
        } else {
            this._refs.put(object, new Integer(this._refs.size()));
            return false;
        }
    }

    public int getRef(Object obj) {
        if(this._refs == null) {
            return -1;
        } else {
            Integer value = (Integer)this._refs.get(obj);
            return value == null?-1:value.intValue();
        }
    }

    public void resetReferences() {
        if(this._refs != null) {
            this._refs.clear();
        }

    }

    public boolean removeRef(Object obj) throws IOException {
        if(this._refs != null) {
            this._refs.remove(obj);
            return true;
        } else {
            return false;
        }
    }

    public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
        Integer value = (Integer)this._refs.remove(oldRef);
        if(value != null) {
            this._refs.put(newRef, value);
            return true;
        } else {
            return false;
        }
    }

    public void printLenString(String v) throws IOException {
        if(v == null) {
            this.os.write(0);
            this.os.write(0);
        } else {
            int len = v.length();
            this.os.write(len >> 8);
            this.os.write(len);
            this.printString((String)v, 0, len);
        }

    }

    public void printString(String v) throws IOException {
        this.printString((String)v, 0, v.length());
    }

    public void printString(String v, int offset, int length) throws IOException {
        for(int i = 0; i < length; ++i) {
            char ch = v.charAt(i + offset);
            if(ch < 128) {
                this.os.write(ch);
            } else if(ch < 2048) {
                this.os.write(192 + (ch >> 6 & 31));
                this.os.write(128 + (ch & 63));
            } else {
                this.os.write(224 + (ch >> 12 & 15));
                this.os.write(128 + (ch >> 6 & 63));
                this.os.write(128 + (ch & 63));
            }
        }

    }

    public void printString(char[] v, int offset, int length) throws IOException {
        for(int i = 0; i < length; ++i) {
            char ch = v[i + offset];
            if(ch < 128) {
                this.os.write(ch);
            } else if(ch < 2048) {
                this.os.write(192 + (ch >> 6 & 31));
                this.os.write(128 + (ch & 63));
            } else {
                this.os.write(224 + (ch >> 12 & 15));
                this.os.write(128 + (ch >> 6 & 63));
                this.os.write(128 + (ch & 63));
            }
        }

    }

    public void flush() throws IOException {
        if(this.os != null) {
            this.os.flush();
        }

    }

    public void close() throws IOException {
        if(this.os != null) {
            this.os.flush();
        }

    }
}
