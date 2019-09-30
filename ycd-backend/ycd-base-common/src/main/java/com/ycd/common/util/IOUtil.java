package com.ycd.common.util;

import java.io.*;

/**
 * 描述:IO工具
 * 作者:杨川东
 * 时间:1:14
 */
public class IOUtil {
    /**
     * 将输入流写入到输出流
     *
     * @param in          输入流
     * @param out         输出流
     * @param closeStream 是否关闭流
     * @throws IOException 异常
     */
    public static void writeInputStream2OutputStream(InputStream in, OutputStream out, boolean closeStream) throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, len);
        }
        if (closeStream) {
            in.close();
            out.close();
        }
    }

    /**
     * 将输入流写入到输出流不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @throws IOException 异常
     */
    public static void writeInputStream2OutputStream(InputStream in, OutputStream out) throws IOException {
        writeInputStream2OutputStream(in, out, false);
    }

    /**
     * 将输入流转换成字节
     *
     * @param inputStream 输入流
     * @return 转换后的字节
     * @throws IOException 异常
     */
    public static byte[] writeInputStream2Bytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeInputStream2OutputStream(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public static void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            if (SimpleUtil.isEmpty(c)) {
                continue;
            }
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}