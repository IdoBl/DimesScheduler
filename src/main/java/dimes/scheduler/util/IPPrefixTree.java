/**
 * 
 */
package dimes.scheduler.util;


/**
 * Class based on Yoni Gazith's (yonigazit@gmail.com) code for saving IP prefixes as bits for for keys.
 * Values are the ASN accirding to RV / WI.
 * 
 * 
 * @author idob
 */

public class IPPrefixTree {
	
	    private IPPrefixNode root;

	    public IPPrefixTree() {
	        root = new IPPrefixNode();
	    }
	    
	    /**
	     * 
	     * @param prefix Subnet base IP address
	     * @param prefixLength
	     * @param value to be associated with the prefix
	     * 
	     * Insert prefix to the tree
	     */
	    
	    public void insertIPprefix(int prefix, int prefixLength, int asNum) {
	    	
	    	IPPrefixNode currNode = root;
	    	
	    	for(int i = 0; i < prefixLength; i++) {
	    		
	    		int currBit = (prefix >> (31 - i)) & 0X01;
	    		
	    		if(currBit == 0) {
	    			if(currNode.getNextZero() == null)
	    				currNode.setNextZero(new IPPrefixNode());
	    			currNode = currNode.getNextZero();
	    		}
	    		
	    		else {
	    			if(currNode.getNextOne() == null)
	    				currNode.setNextOne(new IPPrefixNode());
	    			currNode = currNode.getNextOne();
	    			
	    		}
	    	}
	    	
	    	currNode.setAsNum(asNum);
	    }
	    
/**
 * 
 * @param IPprefix Ip prefix in the format of X.X.X.X/X
 * @param value to be associated with the prefix
 * 
 */
//	    public void insertIPprefix(String IPprefix, int value) {
//			String[] tokens = IPprefix.split("/");
//			
//			if(tokens.length < 2) {
//				System.out.println("Error insertIPprefix: " + IPprefix + " is not a valid prefix\n");
//				return;
//			}	
//			
//	    	 insertIPprefix( .stringIPtoInt(tokens[0]), Integer.valueOf(tokens[1]).intValue(), value);
//
//	    }
//	    
//	    public void insertIPprefixes(List<AsPrefixTblEntity> entities) {
//	    	for(AsPrefixTblEntity ent : entities) {
//	    		insertIPprefix(ASUtil.stringIPtoInt(ent.getAsPrefix()), ent.getAsPrefixLength(), ent.getASN());
//	    	}
//	    }
	    
	    /*
	     * @param ip in a string format of X.X.X.X
	     * @return The longest prefix match or 0 if no match was found 
	     */
	    
//	    public int getLPM(String ip) {
//	    	return getLPM(ASUtil.stringIPtoInt(ip));
//	    }
	    
	    
	    /*
	     * @param ip address
	     * @return The longest prefix match or 0 if no match was found 
	     */
	    
	    public int getLPM(int ip) {
	    	if( ((ip >> 31) & 0X01) == 0)
	    			return getLPMhelper(ip, this.root.getNextZero(), 1);
	    	else
	    		return getLPMhelper(ip, this.root.getNextOne(), 1);
	    }
	    
	    private int getLPMhelper(int ip, IPPrefixNode currNode, int deg) {
	    	
	    	if(currNode == null)
	    		return 0;
	    	
	    	int currResult = currNode.getAsNum();
	    	
	    	int nextResult;
	    	
	    	if( ((ip >> (31 - deg)) & 0X01) == 0)
	    		nextResult = getLPMhelper(ip, currNode.getNextZero(), deg + 1);
	    	
	    	else
	    		nextResult = getLPMhelper(ip, currNode.getNextOne(), deg + 1);
	    	
	    	if(nextResult != 0)
	    		return nextResult;
	    	
	    	return currResult;
	    }
	    
//	    public int getSPM(String ip) {
//	    	return getSPM(ASUtil.stringIPtoInt(ip));
//	    }
	    
	    /*
	     * @param ip address
	     * @ret The shortest prefix match or 0 if no match found
	     */
	    
//	    public int getSPM(int ip) {
//	    	IPPrefixNode currNode = root;
//	    	
//	    	for(int i = 0; currNode != null; i++) {
//	    		
//	    		int currBit = (ip >> (31 - i)) & 0X01;
//	    		if(currBit == 0) {
//	    			currNode = currNode.getNextZero();
//	    		}
//	    		
//	    		else {
//	    			currNode = currNode.getNextOne();
//	    		}
//	    		
//	    		if(currNode != null && currNode.getValue() != 0)
//		    		return currNode.getValue();
//	    	} 	
//	    	return 0;
//	    }

	    /*
	     * @param ixpPrefixes a list of ixp prefixes 
	     * 
	     * Insert the IXP prefixes to the tree
	     */
	    
//		public void insertIXPprefixes(List<String> ixpPrefixes) {
//			for(String prefix : ixpPrefixes) {
//				insertIPprefix(prefix, 1);
//			}	
//		}   
}
