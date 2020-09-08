package com.example.got_saint_who;

import java.io.IOException;
import java.net.Socket;

public class ServerPortTest {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8087);
            System.out.println(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
