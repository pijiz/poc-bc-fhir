/**
 * 
 */
package blockchain.utils;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import blockchain.transaction.Transaction;
//import blockchain.transaction.Transaction;


/**
 * @author pijiz
 *
 */
public class StringUtil {

	// apply SHA-256 hash
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            // applies Sha-256 to the input string
            byte[] hash = digest.digest(input.getBytes( "UTF-8" ));
            StringBuffer hexString = new StringBuffer();
            // stringbuffer to contain the hash as hexadecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString( 0xff & hash[i] );
                if(hex.length() == 1) hexString.append( '0' );
                hexString.append( hex );
               
            }
            return hexString.toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // apply ECDSA signature and returns the results (as bytes)
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance( "ECDSA", "BC");
            dsa.initSign( privateKey );
            byte[] strByte = input.getBytes();
            dsa.update( strByte );
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        return output;
    }
    
    // verifies a string signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance( "ECDSA", "BC" );
            ecdsaVerify.initVerify( publicKey );
            ecdsaVerify.update( data.getBytes() );
            return ecdsaVerify.verify( signature );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString( key.getEncoded() );
    }
    
    //Tacks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

    // creating a string with difficulty * "0"
	public static String getDifficultyString(int difficulty) {
		return new String(new char[difficulty]).replace( '\0', '0' );
	}   
	
	//Formats a date/time into a specific pattern
	public static String getDateString(Date date) {
		if (date == null) {
			return "0";
		}
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	
}
