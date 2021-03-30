package com.example.kontur_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KonturTaskApplication {

    private static String[] args;

    public static void main(String[] args) {
        KonturTaskApplication.args = args;
        SpringApplication.run(KonturTaskApplication.class, args);
    }

    public static String[] getArgs(){
        return args;
    }

}
