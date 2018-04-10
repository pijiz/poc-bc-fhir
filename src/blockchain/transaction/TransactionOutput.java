/**
 * 
 */
package blockchain.transaction;

import java.security.PublicKey;

import blockchain.core.SharedResource;
import blockchain.utils.StringUtil;

/**
 * @author pijiz
 *
 */
public class TransactionOutput {

	public String id;
	public PublicKey recipient; //also known as the new owner of these coins.
	public String parentTransactionId; //the id of the transaction this output was created in	
	public SharedResource sharedResource;
	
	// TODO - create a Resource object with all data above
	
	//Constructor
	public TransactionOutput(PublicKey recipient, SharedResource sharedResource, String parentTransactionId) {
		this.recipient = recipient;
		this.sharedResource = sharedResource;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient)+sharedResource.toString()+parentTransactionId);
	}
	
	//Check if coin belongs to you
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
	
	// return the shared resource
	public SharedResource getSharedResource() {
		return this.sharedResource;
	}
	
}
