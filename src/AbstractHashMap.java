public abstract class AbstractHashMap<V> extends AbstractMap<V> {
    protected int n = 0;                 // number of entries in the dictionary
    protected int capacity;              // length of the table
    private int prime;					 // prime factor for 
    
    /**
     * Constructor for all the tables that will derivate from this.
     * @param cap
     */
    public AbstractHashMap(int cap) {
    	capacity = cap;
    	createTable();
    	prime = 750294863;
    }
    
    // Public Methods.

    /**
     * Return the total count of unique entries in the table.
     * @return
     */
    public int size() { return n; }
    /**
     * Checks if the table is empty or not.
     */
    public boolean isEmpty(){ return size() == 0;}
    /**
     * Puts the given value into the table. The key is generated from
     * the binary value of the given value.
     * @param value as the Value to be inserted.
     */
    public void put(V value) { bucketPut(generateKey(value), value); }
    /**
     * Search the given value in the table. Key is generated from the binary
     * value of the given value.
     * @param value as the Value to be searched.
     */
    public void search(V value) { bucketSearch(generateKey(value), value); }

    // private utilities
    /** Generates a key for the specified value
     * @return key as integer.*/
    private int generateKey(V value) {
    	char[] val = value.toString().toCharArray();
    	int key = 0;
    	for (int i = val.length-1; i >= 0; i--) {
    		//Gives the optimal solutions in between 4-352.
			key = (int) ((key + ((val[i]) * (Math.pow(31, i))))%prime);              
    	}
    	return key;
    }
    
    public void resize(){
    	try{
    		throw new UnsupportedOperationException("Resize operations must be handled by subclasses");
    	}catch(UnsupportedOperationException e){
    		System.out.println(e.getMessage());
    	}
    }
    
    // protected abstract methods to be implemented by subclasses
    /** Creates an empty table having length equal to current capacity. */
    protected abstract void createTable();

    protected abstract void bucketPut(int k, V v);
    
    protected abstract MapEntry<V> bucketSearch(int k , V v);
    
    protected abstract V bucketRemove(int k, V v);

}