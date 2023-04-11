package com.example.xor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/04/2023 09:54
 * @Version 1.0
 */
public class XorUtil {

    public static byte[] endecrypt(int seed, byte[] bytes) {//seed为加密种子，str为加密/解密对象
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= seed;
        }
        return bytes;
    }

    public static String endecrypt(int seed, String str) {//seed为加密种子，str为加密/解密对象
        byte[] bytes = null;
        boolean isBase64 = false;
        if(str.startsWith("b64-")){
            isBase64 = true;
            str = str.substring("b64-".length());
            bytes = Base64.getDecoder().decode(str);
        }else {
            bytes = str.getBytes(StandardCharsets.UTF_8);
        }

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= seed;
        }

        if(!isBase64){
            return   "b64-"+Base64.getEncoder().encodeToString(bytes);
        }
        return new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
    }

    public static File endecrypt(int seed, File file, boolean dealFileName) {//seed为加密种子，str为加密/解密对象
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            String sourceFileUrl = file.getAbsolutePath();
            String targetFileUrl = new File(file.getParentFile(),
                    dealFileName ? endecrypt(seed,
                            file.getName().substring(0,file.getName().lastIndexOf(".")))
                            +file.getName().substring(file.getName().lastIndexOf("."))
                            : "enx-" + file.getName())
                    .getAbsolutePath();
            in = new FileInputStream(sourceFileUrl);
            out = new FileOutputStream(targetFileUrl);

            int data = 0;
            while ((data = in.read()) != -1) {
//将读取到的字节异或上一个数，加密输出
                out.write(data ^ seed);
            }
            return new File(targetFileUrl);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
//在finally中关闭开启的流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
