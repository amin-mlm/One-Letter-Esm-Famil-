package com.example.newesmfamil2;

import java.util.ArrayList;

public class TestThread {
    static int i;
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start");
//
//        for (i = 0; i < 100; i++) {
//            new Thread(()->{
//                int num = i;
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                System.out.println(num);
//            }).start();
//            Thread.sleep(1);
//        }

        ArrayList<Integer> aa = new ArrayList<>();
        aa.add(10);
        aa.add(11);
        aa.add(12);
        System.out.println(aa);

//        int temp = aa.get(1);
//        aa.remove(1);
//        aa.add(1, 5+temp);
        aa.set(1, 16);
        System.out.println(aa);
    }
}
