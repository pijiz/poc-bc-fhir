package blockchain.main;

import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import blockchain.core.Block;
import blockchain.core.SharedResource;
import blockchain.transaction.Transaction;
import blockchain.transaction.TransactionOutput;
import blockchain.user.Wallet;

import com.google.gson.GsonBuilder;

/**
 * 
 */

/**
 * @author pijiz
 *
 */
public class App {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	public static Wallet patientA;
	public static Wallet hopitalB;
	public static Wallet docteurC;
	
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //list of all permissions. 

	public static Transaction genesisTransaction;

	public static void main(String[] args) {

		// start of process time
		long lStartTime = System.currentTimeMillis();

		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create the new wallets
		patientA = new Wallet(); // patient
		hopitalB = new Wallet(); // organisme de santé
		docteurC = new Wallet(); // praticien

		// create genesis transaction, in which userA allow patientA to access
		// all his data to hopitalB:
		genesisTransaction = new Transaction(patientA.publicKey,
				hopitalB.publicKey, new SharedResource("Patient", "*", new Date(), null, 4));
		genesisTransaction.generateSignature(patientA.privateKey); // manually
																	// sign the
																	// genesis
																	// transaction
		genesisTransaction.transactionId = "0"; // manually set the transaction
												// id

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// testing
		System.out.println("Add block 1... ");
		Block block1 = new Block(genesis.hash);

		block1.addTransaction(patientA.addConsent(hopitalB.publicKey,
				new SharedResource("Observation", "1234", null, null, 7)));
		addBlock(block1);

		System.out.println("Add block 2... ");
		Block block2 = new Block(block1.hash);
		block2.addTransaction(patientA.addConsent(hopitalB.publicKey,
				new SharedResource("AllergyIntolerance", "1578", null, null, 0)));
		addBlock(block2);

		System.out.println("Add block 3... ");
		Block block3 = new Block(block2.hash);
		block3.addTransaction(patientA.addConsent(hopitalB.publicKey, 
				new SharedResource("*",	null, new Date(), null, 0)));

		// verification de la validité de la blockchain
		isChainValid();

		// affichage de la blockchain
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create()
				.toJson(blockchain);
		System.out.println("\nThe blockchain: ");
		System.out.println(blockchainJson);

		// end of process time
		long lEndTime = System.currentTimeMillis();
		long output = lEndTime - lStartTime;
		System.out.println("Elapsed time in milliseconds: " + output);
	}

	// adding a block to the blockchain
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	// check blockchain validity
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		// loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// loop thru blockchains transactions:
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions
						.get(t);

				if (!currentTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction(" + t
							+ ") is Invalid");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}

}
