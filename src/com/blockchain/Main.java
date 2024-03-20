package com.blockchain;


public class Main {
    public static void main(String[] args) {
        Wallet satoshi = new Wallet();
        Wallet bob = new Wallet();

        satoshi.checkBalance();
        bob.checkBalance();

        satoshi.sendMoney(50.0, bob.publicKey);
        bob.sendMoney(23.0, satoshi.publicKey);

        System.out.println(Chain.instance);

        satoshi.checkBalance();
        bob.checkBalance();
    }
}