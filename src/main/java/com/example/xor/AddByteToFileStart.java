package com.example.xor;

import java.io.File;

/**
 * https://zhuanlan.zhihu.com/p/571208394
 * https://www.cnblogs.com/zhaijiahui/p/9643599.html
 * 各种文件头,没有以66开头的
 */
public class AddByteToFileStart {
    public static void main(String[] args) {
       /* String filePath = "/Users/hss/1download/flipper使用2021-06-24 19.17.43.mov";
        String path2 = AddByteUtil.addByte(filePath);
        AddByteUtil.createTmpOriginalFile(null,path2);*/

        DirOperationUtil.hideDirAndInnerFiles(new File("/Users/hss/1download/"));
    }
}

