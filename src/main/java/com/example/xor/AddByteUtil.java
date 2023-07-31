package com.example.xor;

import cn.hutool.log.StaticLog;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Despciption todo
 * @Author hss
 * @Date 25/07/2023 09:28
 * @Version 1.0
 */
public class AddByteUtil {
    // 要添加的字节数据
    static  byte dataToAdd = 0x66;
    static  boolean useFileChannel = false;
    //useFileChannel = true: 2011ms  false:1317ms

    public static String addByte(String filePath){

        long startTime = System.currentTimeMillis();

        try {
            // 打开文件
            File file = new File(filePath);
            long originalFileSize = file.length();
            long newFileSize = 0 ;
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            // 读取文件的第一个字节
            int firstByte = raf.read();
            if(dataToAdd == firstByte){
                raf.close();
                System.out.println("已经是加密文件了: "+ file.getAbsolutePath());
                return filePath;
            }
            if(useFileChannel){
                raf.seek(0);
                FileChannel channel = raf.getChannel();
                // 读取原始数据
                ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
                channel.read(buffer);
                buffer.flip();
                // 创建新的ByteBuffer来添加数据
                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + 1);
                // 在开头添加一个字节
                newBuffer.put(dataToAdd);
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
            }else {
                raf.close();
                byte[] bytes = new byte[]{(byte) dataToAdd};
                File file1 = new File(file.getParentFile(),file.getName()+".3");
                filePath = file1.getAbsolutePath();
                if(!file1.exists()){
                    file1.createNewFile();
                }
                InputStream inputStream = new FileInputStream(file);
                ByteArrayInputStream inputStream1 = new ByteArrayInputStream(bytes);
                SequenceInputStream newInputStream = new SequenceInputStream(inputStream1,inputStream);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file1));

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = newInputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();

                newInputStream.close();
                bos.close();
                file.delete();
                newFileSize = file1.length();
            }

            System.out.println("添加字节成功, cost: "+(System.currentTimeMillis() - startTime)
                    +"ms, original file size : "+originalFileSize+",new size: "+newFileSize+", path: "+filePath);
            return filePath;
        } catch (Throwable e) {
            e.printStackTrace();
            return filePath;
        }
    }

    public static File createTmpOriginalFile(File tmpDir,String sourceFilePath){

        File file = new File(sourceFilePath);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int read = inputStream.read();
            inputStream.close();
            if(read != dataToAdd){
                System.out.println("不是加密文件 " +"sourceFilePath : "+ file.getAbsolutePath());
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
        if(tmpDir == null){
            tmpDir = file.getParentFile();
        }
        File destinationFile0 = new File(tmpDir,"tmp-"+file.getName());
        System.out.println("文件信息 " +"sourceFilePath filesize: "+ file.length()+",destinationFile size: "+destinationFile0.length());
        if(destinationFile0.exists()  && destinationFile0.length() == file.length()-1){
            System.out.println("临时解密文件已经存在: "+ destinationFile0.getAbsolutePath());
            return destinationFile0;
        }
        long startTime = System.currentTimeMillis();
        try (RandomAccessFile sourceFile = new RandomAccessFile(sourceFilePath, "r");
             RandomAccessFile destinationFile = new RandomAccessFile(destinationFile0, "rw")) {

            // 获取源文件和目标文件的FileChannel
            FileChannel sourceChannel = sourceFile.getChannel();
            FileChannel destinationChannel = destinationFile.getChannel();

            // 复制文件（不包括开始的字节）
            long sourceFileSize = sourceChannel.size();
            sourceChannel.position(1);
            // 跳过开始的字节
            destinationChannel.transferFrom(sourceChannel, 0, sourceFileSize - 1);

            System.out.println("文件已成功复制并移除了开始的字节,cost: "+(System.currentTimeMillis() - startTime)
                    +"ms, filesize: "+ sourceFileSize+"B");
            return destinationFile0;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static  void clearTmpDir(File tmpDir) {
        if(tmpDir == null){
            return;
        }
        if(!tmpDir.exists()){
            return;
        }
        if(tmpDir.isFile()){
            return;
        }
        File[] files = tmpDir.listFiles();
        if(files == null || files.length <=0){
            return;
        }
        for (File file : files) {
            file.delete();
        }
        tmpDir.delete();
    }

    /**
     * 单线程执行,避免多线程损坏机械硬盘.
     * @param dir
     */
    public static void hideDirAndInnerFiles(File dir) {
        if(dir == null){
            return;
        }
        if(!dir.exists()){
            return;
        }
        if(dir.isFile()){
            addByte(dir.getAbsolutePath());
            return;
        }
        long start = System.currentTimeMillis();
        //广度优先遍历: 先遍历当前文件夹的文件
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });
        if(files == null || files.length ==0){
            files = new File[]{};
            StaticLog.debug("当前文件夹没有子文件,准备遍历子文件夹: "+dir.getAbsolutePath());
        } else  {
            for (File file : files) {
                hideDirAndInnerFiles(file);
            }
            StaticLog.debug(files.length+"-当前文件夹所有子文件已经遍历完,开始遍历其内文件夹: "+dir.getAbsolutePath());
        }

        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if(dirs == null || dirs.length ==0){
            dirs = new File[]{};
            StaticLog.debug("当前文件夹没有子文件夹,遍历完成: "+dir.getAbsolutePath());
        } else  {
            for (File file : dirs) {
                addByte(file.getAbsolutePath());
            }
            StaticLog.debug(dirs.length+"-当前文件夹所有子文件夹已经遍历完,最终遍历完成: "+dir.getAbsolutePath());
        }
        runCmdToHideDirAndSetSystemDir(dir);
        StaticLog.warn("文件夹遍历完成: "+dir.getAbsolutePath()+"\n耗时: "+(System.currentTimeMillis() - start)
                +"ms, 文件夹数: "+dirs.length+", 文件数: "+files.length);


    }

    public static void runCmdToHideDirAndSetSystemDir(File dir){
        String os = System.getProperty("os.name").toLowerCase();
        String hidden = "";
        if (os.contains("win")) {
            // Windows 系统
            hidden = "attrib +s +h ";
        } else {
            // Mac/Linux 系统
            hidden = "chflags hidden";
        }
        runCmdToHideDir(hidden,dir);
        //runCmdToHideDir("attrib +s ",dir);

        /* if (os.contains("win")) {
                // Windows 系统
                process = Runtime.getRuntime().exec("attrib +s +h " + folderPath);
            } else {
                // Mac/Linux 系统
                process = Runtime.getRuntime().exec("chflags hidden +v " + folderPath);
            }*/
    }


    public static boolean runCmdToHideDir(String cmd,File dir){
        try {
            // 构建隐藏文件夹的命令
            String command = cmd + dir.getAbsolutePath();

            // 执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 等待命令执行结束
            int exitCode = process.waitFor();

            // 检查命令是否成功执行
            if (exitCode == 0) {
                StaticLog.info(cmd+ " 文件夹已成功设置为隐藏/系统文件: "+dir.getAbsolutePath());
                return true;
            } else {
                StaticLog.warn(cmd+" 隐藏文件夹/设置为系统文件夹失败: "+dir.getAbsolutePath());
                return false;
            }
        } catch (Throwable e) {
           StaticLog.warn(e.toString());
            return false;
        }
    }
}
