package com.example.xor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/04/2023 09:55
 * @Version 1.0
 */
public class XorInputStream extends InputStream{

    InputStream outputStream;
    int key;

    public XorInputStream(InputStream outputStream, int key) {
        this.outputStream = outputStream;
        this.key = key;
    }

    @Override
    public int read() throws IOException {
        return outputStream.read()^key;
    }
}
