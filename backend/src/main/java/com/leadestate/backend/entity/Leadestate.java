package com.leadestate.backend.entity;

import java.util.Scanner;
public class Leadestate {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String user1;
        String user2;
        String user3;
        String password;
        String in1;
        String in2;

        user1 = "kepin@gmail.com";
        user2 = "rafi@gmail.com";
        user3 = "john";
        password = "1234";
        
                
        System.out.println("Masukkan username: ");
        in1 = input.nextLine();
        
        while(!in1.equals(user1) && !in1.equals(user2) && !in1.equals(user3)){
            System.out.println("Username salah");
            in1 = input.nextLine();
        }
        
        System.out.println("Masukkan password: ");
        in2 = input.nextLine();
        
        while(!in2.equals(password)){
            System.out.println("password salah");
            in2 = input.nextLine();
        }
        
        System.out.println("selamat udah masuk ke aplikasi");
        
    }
}
