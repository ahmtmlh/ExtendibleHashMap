import java.util.ArrayList;

public class UnsortedTableMap<V> extends AbstractMap<V> {
    /** Underlying storage for the map of entries. */
    private ArrayList<MapEntry<V>> table;
    protected int localDepth;
    private boolean visited;
    /** Constructs an initially empty map. */
    public UnsortedTableMap(int global) {
    	table = new ArrayList<>();
    	localDepth = global;
    	visited = false;
    }
    /**
     * This is a private function that finds the index of the given key.
     * @param key
     * @param v
     * @return
     */
    private int findIndex(int key , V v) {
        int n = table.size();
        for (int j=0; j < n; j++)
            if (table.get(j).getKey() == key && table.get(j).getValue().equals(v))
                return j;
        //Special value denotes that entry was not found
        return -1;                                   
    }
    /**
     * For debug purposes.
     * @param c
     * @return
     */
    public int printAll(int c){
    	for (int i = 0; i < table.size(); i++) 
			System.out.println(++c + "--" + table.get(i).getValue());
    	visited = true;
    	return c;
    }
    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public V put(int key, V value) {
    	int index = findIndex(key , value);
    	// If the specified entry cannot be found in the bucket, a new entry is created
    	if(index == -1){
    		//Given key is not found in this bucket. Insert the value and return null.
    		table.add(new MapEntry<>(key, value));
    		//Returning null will be used for the general entry counter for the global table.
    		//If the returned value is null, it means its a new unique value for the table.
    		return null;
    	}
    	else{
    		// If the entry with a same value occurs in the bucket, increase the counter only
    		table.get(index).incrementCounter();  			
    		// To check if the entry is a new or not in ExtendibleHashMap
    		return table.get(index).getValue();             
    	}
    }
    /**
     * To insert the whole entry with all attributes into the bucket
     * @param entry
     */
    public void putEntry(MapEntry<V> entry) { table.add(entry); } 

    /**
     * Chech if the bucket is empty or not.
     */
	public boolean isEmpty() { return table.size() == 0; }
	
	/**
	 * DEBUG PURPOSES ONLY
	 * @return
	 */
	public boolean isVisited() {return visited;}
	
	/**
	 * Search the given key with the given value. Return null if not found.
	 * @param key
	 * @param v
	 * @return
	 */
	public MapEntry<V> search(int key , V v) {
		int index = findIndex(key , v);
		if(index == -1) return null;
		return table.get(index);
	}
	/**
	 * Bucket size is set to be 10 at max. If the bucket has 10 elements, it means its full
	 * This function checks if the bucket is full or not.
	 * @return
	 */
	protected boolean isFull(){ return table.size() >= 10;}
	
	/**
	 * Return the address of the bucket. This is used for resizing and reconfiguring the pointers
	 * of the buckets in all global table.
	 * @return
	 */
	protected ArrayList<MapEntry<V>> getTable() { return table; }

	
}