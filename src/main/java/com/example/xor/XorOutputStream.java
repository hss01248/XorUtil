package com.example.xor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/04/2023 09:55
 * @Version 1.0
 */
public class XorOutputStream extends OutputStream {

    OutputStream outputStream;
    int key;

    public XorOutputStream(OutputStream outputStream, int key) {
        this.outputStream = outputStream;
        this.key = key;
    }


    @Override
    public void write(int b) throws IOException {
        outputStream.write(b^key);
    }
}
