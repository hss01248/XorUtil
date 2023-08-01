package com.example.xor;

import cn.hutool.log.StaticLog;

import java.io.File;
import java.io.FileFilter;

public class DirOperationUtil {


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
            AddByteUtil.addByte(dir.getAbsolutePath());
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
                if(!file.getName().startsWith(".")){
                    AddByteUtil.addByte(file.getAbsolutePath());
                }
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
