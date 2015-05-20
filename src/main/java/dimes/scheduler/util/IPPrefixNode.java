/**
 * 
 */
package dimes.scheduler.util;


/**
 * A class that implements an IP prefix node. Used by
 * the IP Prefix Tree class as values for the leafs.
 * Based on Yoni Gazit (yonigazit@gmail.com) code.
 * 
 * @author idob
 */
public class IPPrefixNode {
        private int asNum;
        private long asPrefix;
        private IPPrefixNode nextOne;
        private IPPrefixNode nextZero;
		
        
        public IPPrefixNode() {
			asNum = 0;
			nextOne = null;
			nextZero = null;
		}
        
		public int getAsNum() {
			return asNum;
		}

		public void setAsNum(int asNum) {
			this.asNum = asNum;
		}

		/**
		 * @return the nextOne
		 */
		public IPPrefixNode getNextOne() {
			return nextOne;
		}
		/**
		 * @param nextOne the nextOne to set
		 */
		public void setNextOne(IPPrefixNode nextOne) {
			this.nextOne = nextOne;
		}
		/**
		 * @return the nextZero
		 */
		public IPPrefixNode getNextZero() {
			return nextZero;
		}
		/**
		 * @param nextZero the nextZero to set
		 */
		public void setNextZero(IPPrefixNode nextZero) {
			this.nextZero = nextZero;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + asNum;
			result = prime * result
					+ ((nextOne == null) ? 0 : nextOne.hashCode());
			result = prime * result
					+ ((nextZero == null) ? 0 : nextZero.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IPPrefixNode other = (IPPrefixNode) obj;
			if (asNum != other.asNum)
				return false;
			if (nextOne == null) {
				if (other.nextOne != null)
					return false;
			} else if (!nextOne.equals(other.nextOne))
				return false;
			if (nextZero == null) {
				if (other.nextZero != null)
					return false;
			} else if (!nextZero.equals(other.nextZero))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return String.format(
					"IPPrefixNode [asNum=%s, nextOne=%s, nextZero=%s]", asNum,
					nextOne, nextZero);
		}
		
}
