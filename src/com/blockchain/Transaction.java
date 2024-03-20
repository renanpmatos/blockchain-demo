package com.blockchain;

public class Transaction {
    public double amount;
    public String payer; // public key
    public String payee; // public key

    public Transaction(double amount, String payer, String payee) {
        this.amount = amount;
        this.payer = payer;
        this.payee = payee;
    }

    public String toString() {
        return "{\"amount\":" + amount + ",\"payer\":\"" + payer + "\",\"payee\":\"" + payee + "\"}";
    }
}