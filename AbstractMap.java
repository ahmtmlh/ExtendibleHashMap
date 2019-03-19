public abstract class AbstractMap<V>{

    public abstract boolean isEmpty();
    
    //---------------- nested MapEntry class ----------------
    
    protected static class MapEntry<V>{
        private int k;  // key
        private V v;    // value
        private int counter;   // Counter for replicas.
        
        public MapEntry(int key, V value) {
            k = key;
            v = value;
            counter = 1;
        }
        //Public functions.
        public int getKey() { return k; }
        public V getValue() { return v; }
        public int getCounter(){ return counter; }
        
        //Protected functions.
        protected void setKey(int key) { k = key; }
        protected void setValue(V value) { v = value; }
        protected void incrementCounter(){ counter++; };
    } //----------- end of nested MapEntry class -----------
}