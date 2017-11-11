package com.ws.hessian.io;

import java.io.IOException;
import java.util.Date;

public class BasicSerializer extends AbstractSerializer implements ObjectSerializer {
    public static final int NULL = 0;
    public static final int BOOLEAN = 1;
    public static final int BYTE = 2;
    public static final int SHORT = 3;
    public static final int INTEGER = 4;
    public static final int LONG = 5;
    public static final int FLOAT = 6;
    public static final int DOUBLE = 7;
    public static final int CHARACTER = 8;
    public static final int CHARACTER_OBJECT = 9;
    public static final int STRING = 10;
    public static final int STRING_BUILDER = 11;
    public static final int DATE = 12;
    public static final int NUMBER = 13;
    public static final int OBJECT = 14;
    public static final int BOOLEAN_ARRAY = 15;
    public static final int BYTE_ARRAY = 16;
    public static final int SHORT_ARRAY = 17;
    public static final int INTEGER_ARRAY = 18;
    public static final int LONG_ARRAY = 19;
    public static final int FLOAT_ARRAY = 20;
    public static final int DOUBLE_ARRAY = 21;
    public static final int CHARACTER_ARRAY = 22;
    public static final int STRING_ARRAY = 23;
    public static final int OBJECT_ARRAY = 24;
    public static final int BYTE_HANDLE = 25;
    public static final int SHORT_HANDLE = 26;
    public static final int FLOAT_HANDLE = 27;
    private static final BasicSerializer BYTE_HANDLE_SERIALIZER = new BasicSerializer(25);
    private static final BasicSerializer SHORT_HANDLE_SERIALIZER = new BasicSerializer(26);
    private static final BasicSerializer FLOAT_HANDLE_SERIALIZER = new BasicSerializer(27);
    private int _code;

    public BasicSerializer(int code) {
        this._code = code;
    }

    public Serializer getObjectSerializer() {
        switch(this._code) {
            case 2:
                return BYTE_HANDLE_SERIALIZER;
            case 3:
                return SHORT_HANDLE_SERIALIZER;
            case 4:
            case 5:
            default:
                return this;
            case 6:
                return FLOAT_HANDLE_SERIALIZER;
        }
    }

    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        boolean hasEnd;
        int i;
        switch(this._code) {
            case 0:
                out.writeNull();
                break;
            case 1:
                out.writeBoolean(((Boolean)obj).booleanValue());
                break;
            case 2:
            case 3:
            case 4:
                out.writeInt(((Number)obj).intValue());
                break;
            case 5:
                out.writeLong(((Number)obj).longValue());
                break;
            case 6:
            case 7:
                out.writeDouble(((Number)obj).doubleValue());
                break;
            case 8:
            case 9:
                out.writeString(String.valueOf(obj));
                break;
            case 10:
                out.writeString((String)obj);
                break;
            case 11:
                out.writeString(((StringBuilder)obj).toString());
                break;
            case 12:
                out.writeUTCDate(((Date)obj).getTime());
                break;
            case 13:
            default:
                throw new RuntimeException(this._code + " unknown code for " + obj.getClass());
            case 14:
                ObjectHandleSerializer.SER.writeObject(obj, out);
                break;
            case 15:
                if(out.addRef(obj)) {
                    return;
                }

                boolean[] var14 = (boolean[])((boolean[])obj);
                hasEnd = out.writeListBegin(var14.length, "[boolean");

                for(i = 0; i < var14.length; ++i) {
                    out.writeBoolean(var14[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 16:
                byte[] var13 = (byte[])((byte[])obj);
                out.writeBytes(var13, 0, var13.length);
                break;
            case 17:
                if(out.addRef(obj)) {
                    return;
                }

                short[] var12 = (short[])((short[])obj);
                hasEnd = out.writeListBegin(var12.length, "[short");

                for(i = 0; i < var12.length; ++i) {
                    out.writeInt(var12[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 18:
                if(out.addRef(obj)) {
                    return;
                }

                int[] var11 = (int[])((int[])obj);
                hasEnd = out.writeListBegin(var11.length, "[int");

                for(i = 0; i < var11.length; ++i) {
                    out.writeInt(var11[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 19:
                if(out.addRef(obj)) {
                    return;
                }

                long[] var10 = (long[])((long[])obj);
                hasEnd = out.writeListBegin(var10.length, "[long");

                for(i = 0; i < var10.length; ++i) {
                    out.writeLong(var10[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 20:
                if(out.addRef(obj)) {
                    return;
                }

                float[] var9 = (float[])((float[])obj);
                hasEnd = out.writeListBegin(var9.length, "[float");

                for(i = 0; i < var9.length; ++i) {
                    out.writeDouble((double)var9[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 21:
                if(out.addRef(obj)) {
                    return;
                }

                double[] var8 = (double[])((double[])obj);
                hasEnd = out.writeListBegin(var8.length, "[double");

                for(i = 0; i < var8.length; ++i) {
                    out.writeDouble(var8[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 22:
                char[] var7 = (char[])((char[])obj);
                out.writeString(var7, 0, var7.length);
                break;
            case 23:
                if(out.addRef(obj)) {
                    return;
                }

                String[] var6 = (String[])((String[])obj);
                hasEnd = out.writeListBegin(var6.length, "[string");

                for(i = 0; i < var6.length; ++i) {
                    out.writeString(var6[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 24:
                if(out.addRef(obj)) {
                    return;
                }

                Object[] data = (Object[])((Object[])obj);
                hasEnd = out.writeListBegin(data.length, "[object");

                for(i = 0; i < data.length; ++i) {
                    out.writeObject(data[i]);
                }

                if(hasEnd) {
                    out.writeListEnd();
                }
                break;
            case 25:
                out.writeObject(new ByteHandle(((Byte)obj).byteValue()));
                break;
            case 26:
                out.writeObject(new ShortHandle(((Short)obj).shortValue()));
                break;
            case 27:
                out.writeObject(new FloatHandle(((Float)obj).floatValue()));
        }

    }
}