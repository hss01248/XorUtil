package com.example.xor;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/07/2023 19:59
 * @Version 1.0
 */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFileAndRemoveByte {
    public static void main(String[] args) {
        String sourceFilePath = "/Users/hss/1download/flipper使用2021-06-24 19.17.43.mov";
        String destinationFilePath = "/Users/hss/1download/tmp-flipper使用2021-06-24 19.17.43.mov";
        long startTime = System.currentTimeMillis();
        try (RandomAccessFile sourceFile = new RandomAccessFile(sourceFilePath, "r");
             RandomAccessFile destinationFile = new RandomAccessFile(destinationFilePath, "rw")) {

            // 获取源文件和目标文件的FileChannel
            FileChannel sourceChannel = sourceFile.getChannel();
            FileChannel destinationChannel = destinationFile.getChannel();

            // 复制文件（不包括开始的字节）
            long sourceFileSize = sourceChannel.size();
            sourceChannel.position(1);
            // 跳过开始的字节
            destinationChannel.transferFrom(sourceChannel, 0, sourceFileSize - 1);

            System.out.println("文件已成功复制并移除了开始的字节,cost: "+(System.currentTimeMillis() - startTime)+"ms, filesize: "+ sourceFileSize/1024+"kB");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

