package com.blockchain;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

//The Blockchain
public class Chain {
	// Singleton instance
	public static final Chain instance = new Chain();
    private final int difficulty = 4;
    private final List<Block> chain;

    private Chain() {
        // Genesis Block
        chain = new ArrayList<>();
        chain.add(new Block("", new Transaction(100, "genesis", "satoshi")));
    }

    public static Chain getInstance() {
        return instance;
    }

    public Block lastBlock() {
        return chain.get(chain.size() - 1);
    }

    // Proof of Work system
    public int mine(int nonce) {
        int solution = 1;
        System.out.println("⛏️  mining...");

        String prefix = "0".repeat(difficulty);

        while (true) {
            String data = nonce + Integer.toString(solution);
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] hashBytes = digest.digest(data.getBytes());
                String attempt = Base64.getEncoder().encodeToString(hashBytes);
                if (attempt.startsWith(prefix)) {
                    System.out.println("Solved: " + solution);
                    return solution;
                }
                solution++;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

	// Add a new block to the chain if valid signature & proof of work is complete
	public void addBlock(Transaction transaction, String senderPublicKey, byte[] signature) {
		try {
			Signature verifier = Signature.getInstance("SHA256withRSA");
			verifier.initVerify(
					KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(senderPublicKey.getBytes())));
			verifier.update(transaction.toString().getBytes());
			boolean isValid = verifier.verify(signature);

			if (isValid) {
				Block newBlock = new Block(this.lastBlock().hash(), transaction);
				this.mine(newBlock.nonce);
				this.chain.add(newBlock);
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
}