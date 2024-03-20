package com.blockchain;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


//Wallet gives a user a public/private keypair
public class Wallet {
	public final String publicKey;
    private final String privateKey;
    private double value;
    private final List<Transaction> transactions;

    public Wallet() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            this.publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating key pair");
        }
        this.value = 100;
        this.transactions = new ArrayList<>();
    }

	// Method to receive money
	public void receiveMoney(Transaction transaction) {
		this.transactions.add(transaction);
		// Update balance
		this.updateBalance();
	}

	private void updateBalance() {
		for (Transaction transaction : this.transactions) {
			if (transaction.payee.equals(this.publicKey)) {
				// If the wallet is the recipient, add the transaction amount
				this.value += transaction.amount;
			}
			if (transaction.payer.equals(this.publicKey)) {
				// If the wallet is the payer, subtract the transaction amount
				this.value -= transaction.amount;
			}
		}
	}

	// Method to check balance
	public void checkBalance() {
		this.updateBalance();
		System.out.println("Current balance: " + this.value);
	}

	// Method to send money
	public void sendMoney(Double amount, String payeePublicKey) {
		// Check if balance is sufficient
		if (this.value >= amount) {
			Transaction transaction = new Transaction(amount, this.publicKey, payeePublicKey);

			try {
				Signature sign = Signature.getInstance("SHA256withRSA");
				sign.initSign(KeyFactory.getInstance("RSA")
						.generatePrivate(new PKCS8EncodedKeySpec(this.privateKey.getBytes())));
				sign.update(transaction.toString().getBytes());
				byte[] signature = sign.sign();

				// Add outgoing transaction to the list
				this.transactions.add(transaction);
				// Update balance
				this.updateBalance();

				// Add block to the chain
				Chain.instance.addBlock(transaction, this.publicKey, signature);

				System.out.println("Transaction of " + amount + " sent to " + payeePublicKey);
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Insufficient balance to perform transaction.");
		}
	}
}