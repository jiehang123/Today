package com.jiehang.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 用NIO读取大文本（2G以上）
 *
 * @author landon
 */
public class ReadLargeTextWithNIO {

    public static void main(String args[]) throws IOException {
        FileInputStream fin = new FileInputStream("D:\\迅雷下载\\GhostWin7x64v13.9.iso");
        FileChannel fcin = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 50);

        FileOutputStream fout = new FileOutputStream("D:\\迅雷下载\\GhostWin7x64v13.9_" + System.currentTimeMillis() + ".iso");

        while (true) {
            buffer.clear();

            int flag = fcin.read(buffer);

            if (flag == -1) {
                break;
            }

            buffer.flip();

            FileChannel fcout = fout.getChannel();

            fcout.write(buffer);
        }
    }

}
