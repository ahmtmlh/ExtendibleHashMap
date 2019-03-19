import java.util.ArrayList;

public class ExtendibleHashMap<V> extends AbstractHashMap<V> {
    private  int globalDepth;
	private UnsortedTableMap<V>[] table;
	
	/**
	 * Constructor for the extendible has map. Initial depth of the map is determined as 256
	 */
	public ExtendibleHashMap() {
		super(256);
		globalDepth = 8;
	}

	/**
	 * This function creates the table with the capacity.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void createTable() {
		table = (UnsortedTableMap<V>[]) new UnsortedTableMap[capacity];
	}
	/**
	 * For debug purposes ONLY.
	 */
	/*
	public void printAll(){
		int c = 0;
		for (int i = 0; i < table.length; i++) {
			if(table[i] != null && !table[i].isVisited()){
				c = table[i].printAll(c);
			}
		}
	}
	*/
	/**
	 * This function finds the correct bucket for the given string, from its key
	 * After determining the bucket of that string, a put oparation in that bucket
	 * is initiated. This function also checks if the given string is already in the table,
	 * if the table is full, if the given bucket is full and splitting buckets would require
	 * a table rebuild.
	 */
	@Override
	protected void bucketPut(int key, V v) {
		// Position of the entry is calculated by changing binary numbers to smaller ones
		int hash = hashValue(binaryValue(key));									    
		UnsortedTableMap<V> bucket = table[hash];
		// If the bucket is not created yet 
		if(bucket == null){                                                  		
			bucket = table[hash] = new UnsortedTableMap<>(globalDepth);
			bucket.put(key, v);
			n++; 								 
		}
		else if(!bucket.isFull()){
			//Put the key with the given value. Check if the word is presented in the table
			//Increment size if the word is a new entry
			if(bucket.put(key, v) == null)
				n++; 											
		}
		// IF THE BUCKET IS FULL
		else{									
			// If the entry can be found in the bucket, no need to extend the map
			if(bucket.search(key,v) != null){													
				bucket.put(key,v);
			}
			// If the bucket is full and localdepth smaller than globaldepth, new buckets will be created
			else if(bucket.localDepth < globalDepth){       									
				arrangeBuckets(bucket);
				bucketPut(key, v);
			}
			// Resize the table
			else if(bucket.localDepth == globalDepth){
				resize();																		
				bucketPut(key,v);
			}
		}
	}
	
	/**
	 * This function splits the given bucket into new buckets according to the new localdepth
	 * of the buckets. This function requires a string to be converted into binary in each iteration.
	 * This CAN run recursively.
	 * @param bucket
	 */
	private void arrangeBuckets(UnsortedTableMap<V> bucket){
		ArrayList<MapEntry<V>> temp = new ArrayList<>(bucket.getTable());		
		for (int i = 0; i < table.length; i++) {									
			//Removing old list(s) from the table
			if(table[i] == bucket)
				table[i] = null;
		}
		bucket = null;
		int n = temp.size();
		for(int i = 0; i < n; i++){
			// All other entries are added to map with new lists.
			int hash = hashValue(binaryValue(temp.get(i).getKey()));
			bucket = table[hash];
			if(bucket == null){                                                  		
				bucket = table[hash] = new UnsortedTableMap<>(globalDepth);
			}
			//If the bucket is full call this function again
			while(bucket.isFull()){												  
				//If chosen bucket is at the same depth as the global depth and is still full
				//resize the table.
				if(bucket.localDepth == globalDepth)
					resize();
				hash = hashValue(binaryValue(temp.get(i).getKey()));
				bucket = table[hash];
				arrangeBuckets(bucket);
			}
			bucket.putEntry(temp.get(i));
		}
		temp = null;
	}
	/**
	 * Resize the table and arrange the pointers of the old buckets from the old table,
	 * to the new buckets from the new table. This operation increases the number of buckets
	 * and total depth of the table.
	 */
	@Override
	public void resize() {
		int oldSize = capacity;
		ArrayList<UnsortedTableMap<V>> temp = new ArrayList<>();
		for (int i = 0; i < table.length; i++) {
			temp.add(table[i]);
		}
		globalDepth++;
		capacity = (int)Math.pow(2,globalDepth);
		createTable();
		int n = temp.size();
		for (int i = 0; i < n; i++) {
			table[i] = temp.get(i);
		}
		temp = null;
		for (int i = 0; i < oldSize; i++) {									// Arrange the pointers for the new slots by localdepth values.
			if(table[i] != null)
				table[oldSize + i] = table[((oldSize + i) % ((int)Math.pow(2 , table[i].localDepth)))];
		}
	}
	/**
	 * DEBUG PURPOSES ONLY
	 * This function looks for the given string, with the generated key if the string
	 * exist in the table or not.
	 */

	@Override
	protected MapEntry<V> bucketSearch(int key, V v) {
		UnsortedTableMap<V> bucket = table[hashValue(binaryValue(key))];
		MapEntry<V> answer = null;
		System.out.println("+--------------------------------+");
		try{
			answer = bucket.search(key, v);
			System.out.println("Key: " + answer.getKey() + 
							 "\nCount: " + answer.getCounter() + 
							 "\nIndex: " + binaryValue(key).substring(32-globalDepth,32) + 
							 "\nGlobal Depth: " + globalDepth + 
							 "\nLocal Depth: " + bucket.localDepth + "\nTOTAL: " + n);
				
		}catch(NullPointerException e){
			System.out.println("Entry Not Found!");
		}
		System.out.println("+--------------------------------+");
		return answer;
	}
	/**
	 * TO-DO
	 * This function removes the string from the table. However, it wont change the depth of the
	 * hash table.
	 */
	@Override
	protected V bucketRemove(int key, V v){
		try {
			throw new UnsupportedOperationException("Remove is not supported!");
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Custom made hashValue for the given binary string. Given binary-string is the binary form
	 * of the key of given string.
	 * @param binary
	 * @return
	 */
	private int hashValue(String binary){
		int hash = 0;
		//Get only the part of the binary string. Part is determined by the globaldepth.
		char[] arr = binary.substring(32-globalDepth, 32).toCharArray();
		for (int i = arr.length-1; i >= 0; i--) {
			hash += ((arr[i] - '0') * Math.pow(2, arr.length-1-i));
		}
		return hash;
	}
	/* String based solution
	private String binaryValue(int k){
    	int key = k;
    	char[] binary = new char[32];
    	boolean flag = true;
    	for(int i = 31; i >= 0; i--){
    		if(key > 1){
    			binary[i] = (char)((key % 2) + '0');	//to match the ascii values of digits , '0' must be added since its ascii value is 48
    			key /= 2;
    		}
    		else if (flag){
    			binary[i] = ((char)(key + '0'));
    			flag = false;							//to stop with the decimal number and complete the binary string to 32 bit.
    		}
    		else{
    			binary[i] = '0';						//complete the binary string to 32 bits
    		}
    	}
    	return new String(binary);
    */
	/**
	 * To find binary values more quickly than iterating on a string,
	 * this opreration shitfs and masks bits in order to find the real binary value
	 * of the key, which was generated from the given string.
	 */
	private String binaryValue(int k){
		int key = k;
		char[] bits = new char[32];
		for (int i = 0; i < bits.length; i++) {
			//48 is the ASCII Code for the char '0'. This is added no mather what,
			//in order to get the binary value in string form.
			bits[31-i] = (char)((key & (1 << i) >> i) + 48);
		}
		return new String(bits);
	}
	
}