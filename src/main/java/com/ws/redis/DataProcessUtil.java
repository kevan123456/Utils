package com.ws.redis;

import com.ws.hessian.io.HessianInput;
import com.ws.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataProcessUtil {

    /**
     * hessian process
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws IOException {
        if(obj==null) throw new NullPointerException();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(obj);
        return os.toByteArray();
    }

    public static Object deserialize(byte[] by) throws IOException{
        if(by==null) throw new NullPointerException();

        ByteArrayInputStream is = new ByteArrayInputStream(by);
        HessianInput hi = new HessianInput(is);
        return hi.readObject();
    }

    /**
     * 压缩数据
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compress( byte[] data) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(bytes);
        out.write(data);
        out.finish();
        out.close();
        return bytes.toByteArray();
    }

    /**
     * 解压数据
     * @param compressedData
     * @return
     * @throws Exception
     */
    public static byte[] decompress( byte[] compressedData) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(compressedData.length);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(compressedData));
        byte[] buffer = new byte[compressedData.length];

        while(true) {
            int bytesRead = in.read(buffer, 0, buffer.length);
            if(bytesRead < 0) {
                break;
            }

            bytes.write(buffer, 0, bytesRead);
        }

        in.close();
        return bytes.toByteArray();
    }
}