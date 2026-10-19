package com.example.onboarding;

import java.util.Scanner;
import java.util.Random;

public class NumberGuess {
    public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();

    int secret = rand.nextInt(100)+1;
    int guess = 0;
    System.out.println("Enter a number between 1 to 100: ");

    while(guess != 100){
        if (!sc.hasNextInt()) {
            System.out.println("Enter a valid digits: ");
            sc.next();
            continue;

        }
        guess = sc.nextInt();
        if(guess < secret){
            System.out.println("Too Low!!, Try again");
        } else if (guess > secret) {
            System.out.println("Too high!!, Try again");
        }else{
            System.out.println("You won");
        }
    }
    sc.close();

    }
}
