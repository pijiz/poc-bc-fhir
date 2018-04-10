/**
 * 
 */
package blockchain.user;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
//import blockchain.transaction.Transaction;
import java.util.HashMap;
import java.util.Map;

import blockchain.core.SharedResource;
import blockchain.main.App;
import blockchain.transaction.Transaction;
import blockchain.transaction.TransactionOutput;

/**
 * @author pijiz
 *
 */
public class Wallet {

	public PrivateKey privateKey;

	public PublicKey publicKey;
	
    public HashMap<String,Integer> permissions = new HashMap<String,Integer>(); //only UTXOs owned by this wallet.	

	public Wallet() {
		generateKeyPair();
	}

	// generate private/public key pair
	private void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA",
					"BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// set the public and private keys from the KeyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	//
	public Transaction addConsent(PublicKey recipient, SharedResource sharedResource) {
		Transaction newTransaction = new Transaction(publicKey, recipient,
				sharedResource);
		newTransaction.generateSignature(privateKey);

//  		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
//   			TransactionOutput UTXO = item.getValue();
//   			total += UTXO.value;
//   			inputs.add(new TransactionInput(UTXO.id));
//   			if(total > value) break;
//   		}
//		
		return newTransaction;
	}
	
	// 
	public boolean isAccessGranted(String url) {
		boolean isGranted = true;
		// 2 ways to be granted:
		// - to own the resource (TODO)	
		// - to be given the access by the owner
		// TODO: manage wildcard (*)
		
		// check the resource URL 
		if (permissions.containsKey(url)) {
			// check permission is > 0 (unix-like coding)
			// TODO matching octal values and permissions (use ENUM)
			if (permissions.get(url) > 0) {
				return true;
			}	
		}
		return false;
	}
	
	public void getPermissions() {
		for (Map.Entry<String, TransactionOutput> item: App.UTXOs.entrySet()){
           	TransactionOutput UTXO = item.getValue();
               if(UTXO.isMine(publicKey)) { //if permission is given to me
            	   SharedResource sharedResource = UTXO.getSharedResource();
            	   permissions.put(sharedResource.getUri(), sharedResource.octal); //add it to our list of permissions.
               }
           }  
	}	
}
