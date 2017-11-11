package com.ws.hessian.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class BasicDeserializer extends AbstractDeserializer {
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
    private int _code;

    public BasicDeserializer(int code) {
        this._code = code;
    }

    public Class getType() {
        switch(this._code) {
            case 0:
                return Void.TYPE;
            case 1:
                return Boolean.class;
            case 2:
                return Byte.class;
            case 3:
                return Short.class;
            case 4:
                return Integer.class;
            case 5:
                return Long.class;
            case 6:
                return Float.class;
            case 7:
                return Double.class;
            case 8:
                return Character.class;
            case 9:
                return Character.class;
            case 10:
                return String.class;
            case 11:
            default:
                throw new UnsupportedOperationException();
            case 12:
                return Date.class;
            case 13:
                return Number.class;
            case 14:
                return Object.class;
            case 15:
                return boolean[].class;
            case 16:
                return byte[].class;
            case 17:
                return short[].class;
            case 18:
                return int[].class;
            case 19:
                return long[].class;
            case 20:
                return float[].class;
            case 21:
                return double[].class;
            case 22:
                return char[].class;
            case 23:
                return String[].class;
            case 24:
                return Object[].class;
        }
    }

    public Object readObject(AbstractHessianInput in) throws IOException {
        String code;
        int length;
        switch(this._code) {
            case 0:
                in.readObject();
                return null;
            case 1:
                return Boolean.valueOf(in.readBoolean());
            case 2:
                return Byte.valueOf((byte)in.readInt());
            case 3:
                return Short.valueOf((short)in.readInt());
            case 4:
                return Integer.valueOf(in.readInt());
            case 5:
                return Long.valueOf(in.readLong());
            case 6:
                return Float.valueOf((float)in.readDouble());
            case 7:
                return Double.valueOf(in.readDouble());
            case 8:
                code = in.readString();
                if(code != null && !code.equals("")) {
                    return Character.valueOf(code.charAt(0));
                }

                return Character.valueOf('\u0000');
            case 9:
                code = in.readString();
                if(code != null && !code.equals("")) {
                    return Character.valueOf(code.charAt(0));
                }

                return null;
            case 10:
                return in.readString();
            case 11:
            default:
                throw new UnsupportedOperationException();
            case 12:
                return new Date(in.readUTCDate());
            case 13:
                return in.readObject();
            case 14:
                return in.readObject();
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 23:
                int code1 = in.readListStart();
                switch(code1) {
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                        length = code1 - 16;
                        in.readInt();
                        return this.readLengthList(in, length);
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    default:
                        String type1 = in.readType();
                        length = in.readLength();
                        return this.readList(in, length);
                    case 78:
                        return null;
                }
            case 16:
                return in.readBytes();
            case 22:
                code = in.readString();
                if(code == null) {
                    return null;
                } else {
                    length = code.length();
                    char[] type = new char[length];
                    code.getChars(0, length, type, 0);
                    return type;
                }
        }
    }

    public Object readList(AbstractHessianInput in, int length) throws IOException {
        ArrayList list;
        int i;
        int var8;
        switch(this._code) {
            case 15:
                if(length >= 0) {
                    boolean[] var17 = new boolean[length];
                    in.addRef(var17);

                    for(var8 = 0; var8 < var17.length; ++var8) {
                        var17[var8] = in.readBoolean();
                    }

                    in.readEnd();
                    return var17;
                } else {
                    list = new ArrayList();

                    while(!in.isEnd()) {
                        list.add(Boolean.valueOf(in.readBoolean()));
                    }

                    in.readEnd();
                    boolean[] var21 = new boolean[list.size()];
                    in.addRef(var21);

                    for(i = 0; i < var21.length; ++i) {
                        var21[i] = ((Boolean)list.get(i)).booleanValue();
                    }

                    return var21;
                }
            case 16:
            case 22:
            default:
                throw new UnsupportedOperationException(String.valueOf(this));
            case 17:
                if(length >= 0) {
                    short[] var16 = new short[length];
                    in.addRef(var16);

                    for(var8 = 0; var8 < var16.length; ++var8) {
                        var16[var8] = (short)in.readInt();
                    }

                    in.readEnd();
                    return var16;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(Short.valueOf((short)in.readInt()));
                }

                in.readEnd();
                short[] var20 = new short[list.size()];

                for(i = 0; i < var20.length; ++i) {
                    var20[i] = ((Short)list.get(i)).shortValue();
                }

                in.addRef(var20);
                return var20;
            case 18:
                if(length >= 0) {
                    int[] var14 = new int[length];
                    in.addRef(var14);

                    for(var8 = 0; var8 < var14.length; ++var8) {
                        var14[var8] = in.readInt();
                    }

                    in.readEnd();
                    return var14;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(Integer.valueOf(in.readInt()));
                }

                in.readEnd();
                int[] var19 = new int[list.size()];

                for(i = 0; i < var19.length; ++i) {
                    var19[i] = ((Integer)list.get(i)).intValue();
                }

                in.addRef(var19);
                return var19;
            case 19:
                if(length >= 0) {
                    long[] var12 = new long[length];
                    in.addRef(var12);

                    for(var8 = 0; var8 < var12.length; ++var8) {
                        var12[var8] = in.readLong();
                    }

                    in.readEnd();
                    return var12;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(Long.valueOf(in.readLong()));
                }

                in.readEnd();
                long[] var18 = new long[list.size()];

                for(i = 0; i < var18.length; ++i) {
                    var18[i] = ((Long)list.get(i)).longValue();
                }

                in.addRef(var18);
                return var18;
            case 20:
                if(length >= 0) {
                    float[] var11 = new float[length];
                    in.addRef(var11);

                    for(var8 = 0; var8 < var11.length; ++var8) {
                        var11[var8] = (float)in.readDouble();
                    }

                    in.readEnd();
                    return var11;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(new Float(in.readDouble()));
                }

                in.readEnd();
                float[] var15 = new float[list.size()];

                for(i = 0; i < var15.length; ++i) {
                    var15[i] = ((Float)list.get(i)).floatValue();
                }

                in.addRef(var15);
                return var15;
            case 21:
                if(length >= 0) {
                    double[] var9 = new double[length];
                    in.addRef(var9);

                    for(var8 = 0; var8 < var9.length; ++var8) {
                        var9[var8] = in.readDouble();
                    }

                    in.readEnd();
                    return var9;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(new Double(in.readDouble()));
                }

                in.readEnd();
                double[] var13 = new double[list.size()];
                in.addRef(var13);

                for(i = 0; i < var13.length; ++i) {
                    var13[i] = ((Double)list.get(i)).doubleValue();
                }

                return var13;
            case 23:
                if(length >= 0) {
                    String[] var7 = new String[length];
                    in.addRef(var7);

                    for(var8 = 0; var8 < var7.length; ++var8) {
                        var7[var8] = in.readString();
                    }

                    in.readEnd();
                    return var7;
                }

                list = new ArrayList();

                while(!in.isEnd()) {
                    list.add(in.readString());
                }

                in.readEnd();
                String[] var10 = new String[list.size()];
                in.addRef(var10);

                for(i = 0; i < var10.length; ++i) {
                    var10[i] = (String)list.get(i);
                }

                return var10;
            case 24:
                if(length >= 0) {
                    Object[] var6 = new Object[length];
                    in.addRef(var6);

                    for(var8 = 0; var8 < var6.length; ++var8) {
                        var6[var8] = in.readObject();
                    }

                    in.readEnd();
                    return var6;
                } else {
                    list = new ArrayList();
                    in.addRef(list);

                    while(!in.isEnd()) {
                        list.add(in.readObject());
                    }

                    in.readEnd();
                    Object[] data = new Object[list.size()];

                    for(i = 0; i < data.length; ++i) {
                        data[i] = list.get(i);
                    }

                    return data;
                }
        }
    }

    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        int i;
        switch(this._code) {
            case 15:
                boolean[] var11 = new boolean[length];
                in.addRef(var11);

                for(i = 0; i < var11.length; ++i) {
                    var11[i] = in.readBoolean();
                }

                return var11;
            case 16:
            case 22:
            default:
                throw new UnsupportedOperationException(String.valueOf(this));
            case 17:
                short[] var10 = new short[length];
                in.addRef(var10);

                for(i = 0; i < var10.length; ++i) {
                    var10[i] = (short)in.readInt();
                }

                return var10;
            case 18:
                int[] var9 = new int[length];
                in.addRef(var9);

                for(i = 0; i < var9.length; ++i) {
                    var9[i] = in.readInt();
                }

                return var9;
            case 19:
                long[] var8 = new long[length];
                in.addRef(var8);

                for(i = 0; i < var8.length; ++i) {
                    var8[i] = in.readLong();
                }

                return var8;
            case 20:
                float[] var7 = new float[length];
                in.addRef(var7);

                for(i = 0; i < var7.length; ++i) {
                    var7[i] = (float)in.readDouble();
                }

                return var7;
            case 21:
                double[] var6 = new double[length];
                in.addRef(var6);

                for(i = 0; i < var6.length; ++i) {
                    var6[i] = in.readDouble();
                }

                return var6;
            case 23:
                String[] var5 = new String[length];
                in.addRef(var5);

                for(i = 0; i < var5.length; ++i) {
                    var5[i] = in.readString();
                }

                return var5;
            case 24:
                Object[] data = new Object[length];
                in.addRef(data);

                for(i = 0; i < data.length; ++i) {
                    data[i] = in.readObject();
                }

                return data;
        }
    }
}
