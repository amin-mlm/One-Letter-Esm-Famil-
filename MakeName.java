package com.example.newesmfamil2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MakeName {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/main/java/com/example/newesmfamil2/name.txt"));
        PrintWriter printWriter = new PrintWriter("src/main/java/com/example/newesmfamil2/dictionary/name1.txt");


        while (scanner.hasNextLine()){
            String wholeName = scanner.nextLine();
            Scanner scannerName = new Scanner(wholeName);

            try{
                String name1 = scannerName.next();
                Integer.parseInt(scannerName.next());
                String name2 = scannerName.next();
                if(name2.equals("*")){
                    name2 = scannerName.next();
                }
                Integer.parseInt(scannerName.next());

//                System.out.println(name1 + ", " + name2);

                printWriter.println(name1);
                printWriter.println(name2);

            }catch(NumberFormatException e){
                continue;
            }catch (NoSuchElementException e){
                continue;
            }

        }
        printWriter.close();

    }
}
