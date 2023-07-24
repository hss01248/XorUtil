package com.example.xor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * https://zhuanlan.zhihu.com/p/571208394
 * https://www.cnblogs.com/zhaijiahui/p/9643599.html
 * 各种文件头,很少以1开头的
 */
public class AddByteToFileStart {
    public static void main(String[] args) {
        String filePath = "/Users/hss/1download/flipper使用2021-06-24 19.17.43.mov";
        byte dataToAdd = 0x50;
        long startTime = System.currentTimeMillis();
        // 要添加的字节数据

        try {
            // 打开文件
            File file = new File(filePath);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();

            // 读取原始数据
            ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
            channel.read(buffer);
            buffer.flip();

            // 创建新的ByteBuffer来添加数据
            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + 1);

            // 在开头添加一个字节
            newBuffer.put((byte) 1);

            // 将原始数据写入新的ByteBuffer
            newBuffer.put(buffer);

            // 切换新的ByteBuffer为读模式
            newBuffer.flip();

            // 清空文件内容
            channel.truncate(0);

            // 将新的ByteBuffer写入文件
            channel.write(newBuffer);

            // 关闭通道和文件
            channel.close();
            raf.close();

            System.out.println("添加字节成功, cost: "+(System.currentTimeMillis() - startTime)+"ms, filesize: "+ file.length()/1024+"kB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

