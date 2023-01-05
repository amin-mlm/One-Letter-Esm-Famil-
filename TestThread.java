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

//        ArrayList<Integer> aa = new ArrayList<>();
//        aa.add(10);
//        aa.add(11);
//        aa.add(12);
//        System.out.println(aa);
//
////        int temp = aa.get(1);
////        aa.remove(1);
////        aa.add(1, 5+temp);
//        aa.set(1, 16);
//        System.out.println(aa);
        ArrayList<Integer> aa = new ArrayList<>();
        aa.add(11);
        aa.add(10);
        aa.add(12);
        aa.add(13);
        aa.add(14);
        aa.add(12);
        aa.add(15);
        aa.add(9);
        aa.add(10);
        aa.add(8);
        System.out.println(aa);

outer:  for (int i = 0; i < aa.size(); i++) {
            boolean should = false;
            for (int j = 0; j < aa.size() - 1; j++) {
                if(aa.get(j) > aa.get(j+1)){
                    int tempScore = aa.get(j);
                    aa.set(j, aa.get(j+1));
                    aa.set(j+1, tempScore);
                    should = true;
                }
                else if( j==aa.size() - 2 && !should){
                    break outer;
                }
            }
        }

        System.out.println(aa);


//        while (aa.size()!=0){
//            aa.remove(0);
//        }
//        System.out.println(aa);


    }
}
