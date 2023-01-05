package com.example.newesmfamil2;

public class TestThread {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start");

        new Thread(()->{
            for (int i = 1; i < 200; i++) {
                System.out.print(i+", ");
            }
        }).start();

        Thread.sleep(5);

        for (int i = 97; i < 97+200; i++) {
            System.out.print((char) i+", ");
        }
    }
}
