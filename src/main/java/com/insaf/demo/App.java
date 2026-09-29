package com.insaf.demo;

public class App {

    public static String hello(String name) {
        return "Hello " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println(hello("CI/CD"));
    }
}
