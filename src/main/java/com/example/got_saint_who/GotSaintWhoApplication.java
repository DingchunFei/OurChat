package com.example.got_saint_who;

import com.example.got_saint_who.dialogue.DialogueServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GotSaintWhoApplication {

    public static void main(String[] args) {

        //开启服务器
        new Thread(new DialogueServer()).start();

        SpringApplication.run(GotSaintWhoApplication.class, args);
    }

}
