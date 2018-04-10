/**
 * 
 */
package blockchain.validator;

import java.util.ArrayList;

import blockchain.core.Block;

/**
 * @author pijiz
 *
 */
public class ChainValidator {

        public static Boolean isChainValid(ArrayList<Block> blockchain, int difficulty) {
            Block currentBlock;
            Block previousBlock;
            String hashTarget = new String(new char[difficulty]).replace( '\0', '0' );
            
            // loop through blockchain to check hashes
            for (int i = 1; i < blockchain.size(); i++) {
                currentBlock = blockchain.get( i );
                previousBlock = blockchain.get( i-1 );
                
                // compare registered hash and calculated hash
                if (!currentBlock.hash.equals( currentBlock.calculateHash() )) {
                    System.out.println("Current hashes not equal");
                    return false;
                }
                
                // compare previous hash and registered previous hash
                if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                    System.out.println("Previous hashes not equal");
                    return false;
                }
                
                // check if hash is solved
                if (!currentBlock.hash.substring( 0, difficulty ).equals( hashTarget )) {
                    System.out.println("This block hasn't been mined");
                    return false;  
                }
            }
            
            return true;
        }
    
}
