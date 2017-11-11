package com.ws.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip 压缩 解压缩
 * Created by <a href="liao670223382@163.com">shengchou</a> on 2015/7/31.
 */
public class GzipUtil {
    private static final int buffLen = 1024;
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

    public static byte[] decompress(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(buffLen);
        GZIPInputStream in = new GZIPInputStream(inputStream);
        byte[] buffer = new byte[buffLen];

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
