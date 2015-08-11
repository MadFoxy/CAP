package org.pin.cap;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by lee5hx on 15-6-11.
 */
public class BaseTest {



    public static void main(String agrs[]) throws Exception{

        RandomAccessFile aFile = new RandomAccessFile("/home/lee5hx/java_works/lee5hx/CAP/target/test-classes/org/pin/cap/nio-data.txt", "rw");
        FileChannel inChannel = aFile.getChannel();

        //create buffer with capacity of 48 bytes
        ByteBuffer buf = ByteBuffer.allocate(25);

        int bytesRead = inChannel.read(buf); //read into buffer.


        while (bytesRead != -1) {
            System.out.println("@"+bytesRead);
            buf.flip();  //make buffer ready for read

            while(buf.hasRemaining()){

                System.out.print((char) buf.get()); // read 1 byte at a time
            }

            buf.clear(); //make buffer ready for writing
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }
}
