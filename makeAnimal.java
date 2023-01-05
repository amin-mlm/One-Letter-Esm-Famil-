package com.example.newesmfamil2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class makeAnimal {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/main/java/com/example/newesmfamil2/food1.txt"));
        PrintWriter printWriter = new PrintWriter("src/main/java/com/example/newesmfamil2/dictionary/food.txt");
        Scanner lineScan;

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.equals(""))
                continue;

            lineScan = new Scanner(line);

            String firstWord = lineScan.next();
            String secondWord ;

            if(firstWord.equals("List")){
                secondWord = lineScan.next();
                if(secondWord.equals("of")){
                    continue;
                }
            }
            else if(firstWord.equalsIgnoreCase("Food")){
                secondWord = lineScan.next();
                if(secondWord.equals("that"))
                    continue;
            }

            printWriter.println(line);

        }

        printWriter.close();

    }
}
