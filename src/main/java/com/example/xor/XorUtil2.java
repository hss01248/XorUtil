package com.example.xor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/07/2023 19:42
 * @Version 1.0
 */
public class XorUtil2 {

    public static File endecrypt(int seed, File file, boolean encryptFileName) {//seed为加密种子，str为加密/解密对象
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            String sourceFileUrl = file.getAbsolutePath();
            String fileName = file.getName();

            String targetFileName = "";
            if (encryptFileName) {
                targetFileName = XorUtil.endecrypt(seed,
                        fileName.substring(0, fileName.lastIndexOf(".")))
                        + fileName.substring(fileName.lastIndexOf("."));
            } else {
                if (fileName.startsWith(XorUtil.filePrefix)) {
                    targetFileName = fileName.substring(XorUtil.filePrefix.length());
                } else {
                    targetFileName = XorUtil.filePrefix + fileName;
                }
            }

            String targetFileUrl = new File(file.getParentFile(), targetFileName).getAbsolutePath();
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


    public void fileChannelCopy(File sourceF, File targetF) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(sourceF);
            fo = new FileOutputStream(targetF);
            in = fi.getChannel();
            //得到对应的文件通道
            out = fo.getChannel();
            //out.write()
            //得到对应的文件通道
            in.transferTo(0, in.size(), out);
            //连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
