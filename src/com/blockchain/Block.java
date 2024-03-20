package com.blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Block {

    public final int nonce;
    private final String prevHash;
    private final Transaction transaction;
    private final long ts;

    public Block(String prevHash, Transaction transaction) {
        this.prevHash = prevHash;
        this.transaction = transaction;
        this.ts = System.currentTimeMillis();
        this.nonce = (int) (Math.random() * 999999999);
    }

    public String hash() {
        String data = prevHash + transaction.toString() + Long.toString(ts) + Integer.toString(nonce);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "{\"nonce\":" + nonce + ",\"prevHash\":\"" + prevHash + "\",\"transaction\":" + transaction.toString() + ",\"ts\":" + ts + "}";
    }
}