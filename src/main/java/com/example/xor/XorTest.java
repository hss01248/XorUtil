package com.example.xor;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/04/2023 10:15
 * @Version 1.0
 */
public class XorTest {
    public static void main(String[] args) {
        String xxyy = XorUtil.endecrypt(793, "Base64 编码字符串 (URL):=0");
        System.out.println("result: "+xxyy);
        System.out.println("de: "+XorUtil.endecrypt(793, xxyy));


        File endecrypt = XorUtil.endecrypt(793,
                new File("/Users/hss/1download/tmp-flipper使用2021-06-24 19.17.43.mov"),
                true);
        File file2  = XorUtil.endecrypt(793,endecrypt,true);
        System.out.println("result: "+endecrypt.getName());
        System.out.println("result2: "+file2.getName());

    }
}
