/**
 * 
 */
package test.blockchain;

import static org.junit.Assert.*;

import java.security.Security;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import blockchain.core.Block;
import blockchain.core.SharedResource;
import blockchain.transaction.Transaction;
import blockchain.user.Wallet;
import blockchain.utils.StringUtil;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author pierre-jean.breton
 *
 */
public class TransactionTest {

	private Wallet walletA;
	private Wallet walletB;
	private Transaction genesisTransaction;
	private Block genesis;
	SharedResource sharedResource;
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        //Register Bouncy Castle JCE provider
        Security.addProvider(new BouncyCastleProvider());
        
		walletA = new Wallet();
		walletB = new Wallet();
				
		sharedResource = new SharedResource("observation", "45866484", new Date(), null, 4);
		
		genesisTransaction = new Transaction(walletA.publicKey,
				walletB.publicKey, sharedResource);
		genesisTransaction.generateSignature(walletA.privateKey); 
		genesisTransaction.transactionId = "0"; 

		System.out.println("Creating and Mining Genesis block... ");
		genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		genesis.mineBlock(difficulty);
	}

	@Test
	public void walletBDecodeTransactionTest() {
		//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		
		//Create a test transaction from WalletA to walletB 
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(genesisTransaction.verifiySignature());
	}

	// test access for walletA to an owned resource
	@Test
	public void walletA_check_access_resource_granted() {
		assertTrue(walletA.isAccessGranted("observation/45866484"));
	}
	
	// test access for a walletB to an authorized resource
	@Test
	public void walletB_check_access_resource_granted() {
		assertTrue(walletB.isAccessGranted("observation/45866484"));
	}
	
	// test access for a walletB to an unauthorized resource
	@Test
	public void walletB_check_access_resource_not_granted() {
		assertFalse(walletB.isAccessGranted("allergyintolerance/45866484"));
		assertFalse(walletB.isAccessGranted("observation/135"));
		assertFalse(walletB.isAccessGranted("patient/*"));		
	}
	
	// test d'acces a la ressource avec une mauvaise cle
//	@Test
//	public void walletB_wrong_private_key_then_no_access() {
//		assertFalse(walletB.isAccessGranted("p/*"));
//	}

}
