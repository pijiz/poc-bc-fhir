/**
 * 
 */
package blockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import blockchain.core.SharedResource;
import blockchain.main.App;
import blockchain.utils.StringUtil;

/**
 * @author pierre-jean.breton
 *
 */
public class Transaction {

	public String transactionId; // hash of the transaction

	public PublicKey sender; // sender address/public key

	public PublicKey recipient; // recipient address/public key

	public SharedResource sharedResource; // the resource to be shared

	public byte[] signature; // to prevent anybody else from spending funds in
	// our wallet
	
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // count how many transactions have been

	// generated

	// constructor
	public Transaction(PublicKey from, PublicKey to, SharedResource sharedResource) {
		this.sender = from;
		this.recipient = to;
		this.sharedResource = sharedResource;
	}

	// calculates the transaction hash (that will be used as its id)
	private String calculateHash() {
		sequence++; // increase the sequence to avoid 2 identical transactions
					// having the same hash
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(recipient) + sharedResource.toString() + sequence);
	}

	// Signs all the data we dont wish to be tampered with.
	public byte[] generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ sharedResource.toString();
		return signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ sharedResource.toString();
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	// Returns true if new transaction could be created.
	public boolean processTransaction() {
		if (verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// generate transaction outputs: permissions to recipient
		outputs.add(new TransactionOutput(this.recipient, sharedResource, transactionId)); 

		return true;
	}

}
