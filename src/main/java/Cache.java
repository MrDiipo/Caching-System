import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class Cache<KEY,VALUE> {

    private final Map<KEY, VALUE> map = new ConcurrentHashMap<>(); // Data structure for the cache
    // Data source connected to this cache
    private final DataSource<KEY, VALUE> dataSource;
    private final PersistenceAlgorithm persistenceAlgorithm;
    private final EvictionAlgorithm evictionAlgorithm;
    private static Integer THRESHOLD_SIZE = 500;

    public Cache(DataSource<KEY, VALUE> dataSource, PersistenceAlgorithm persistenceAlgorithm, EvictionAlgorithm evictionAlgorithm) {
        this.dataSource = dataSource;
        this.persistenceAlgorithm = persistenceAlgorithm;
        this.evictionAlgorithm = evictionAlgorithm;
    }

    public Future<VALUE> get(KEY key){

        if (map.containsKey(key)){
            return CompletableFuture.completedFuture(map.get(key));
        } else {
            return dataSource.get(key); // Returns the key if not contained in the cache
        }
    }

    public Future<Void> set(KEY key, VALUE value){

        if (!map.containsKey(key) && map.size() >= THRESHOLD_SIZE) {
                //LRU LFU
                // updation
                if (persistenceAlgorithm.equals(PersistenceAlgorithm.WRITE_THROUGH)){
                    dataSource.persist(key, value).
                            thenAccept(__-> map.put(key, value));
                } else {
                    map.put(key, value);
                    dataSource.persist(key, value);
                    return CompletableFuture.completedFuture(null);
                }
            }
        } else{
            // replacement or direct load
            if (map.size() >= THRESHOLD_SIZE){
        }
    }
}

class Record<VALUE> implements Comparable<Record<VALUE>> {
    private final VALUE value;
    private long accessTimeStamp;
    private long accessCount;

    public Record(VALUE value) {
        this.value = value;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
     * for all {@code x} and {@code y}.  (This
     * implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
     * all {@code z}.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Record<VALUE> record`) {
        return (int) (accessTimeStamp - record.accessTimeStamp);
    }
}
