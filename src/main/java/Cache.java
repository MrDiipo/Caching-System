import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Cache<KEY,VALUE> {

    private final Map<KEY, Record> map = new ConcurrentHashMap<>(); // Data structure for the cache
    // Data source connected to this cache
    private final DataSource<KEY, VALUE> dataSource;
    private final PersistenceAlgorithm persistenceAlgorithm;
    private final EvictionAlgorithm evictionAlgorithm;
    private final Integer expiryTimeInMillis;
    private final Map<Long, List<Record<VALUE>>> expiryQueue = new ConcurrentHashMap<>();
    private final Map<AccessDetails, List<Record<VALUE>>> priorityQueue;
    private static Integer THRESHOLD_SIZE = 500;

    public Cache(DataSource<KEY, VALUE> dataSource,
                 PersistenceAlgorithm persistenceAlgorithm,
                 EvictionAlgorithm evictionAlgorithm,
                 Integer expiryTimeInMillis, Map<Long, List<Record<VALUE>>> expiryQueue,
                 Map<AccessDetails, List<Record<VALUE>>> priorityQueue) {
        this.dataSource = dataSource;
        this.persistenceAlgorithm = persistenceAlgorithm;
        this.evictionAlgorithm = evictionAlgorithm;
        this.expiryTimeInMillis = expiryTimeInMillis;
        this.priorityQueue = new ConcurrentSkipListMap<>((first, second) -> {
            if (evictionAlgorithm.equals(EvictionAlgorithm.LRU)){
            return first.getAccessTimeStamp() - second.getAccessTimeStamp();
        } else {
                return first.getAccessCount() - second.getAccessCount();
            });
    }
    }

    public CompletableFuture<VALUE> get(KEY key){

        if (map.containsKey(key) && map.get(key).getAccessTimeStamp() < System.currentTimeMillis() - expiryTimeInMillis){
            return (CompletableFuture<VALUE>) CompletableFuture.completedFuture(map.get(key)).thenApply(Record::getValue);
        } else {
            return dataSource.get(key).thenCompose(value -> set(key, value).thenApply(__ -> value)); // Returns the key if not contained in the cache
        }
    }

    public CompletableFuture<Void> set(KEY key, VALUE value){
        Record<VALUE> valueRecord = new Record<>(value, loadTime, accessDetails);
        if (!map.containsKey(key) && map.size() >= THRESHOLD_SIZE) {
                //LRU LFU
                if (evictionAlgorithm.equals(evictionAlgorithm.LRU)){
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
        TreeMap<KEY, Record<VALUE>> treeMap = new TreeMap<>();
        treeMap.firstEntry();
    }
}

class Record<VALUE> implements Comparable<Record<VALUE>> {
    private final VALUE value;
    private long loadTime;
    private final AccessDetails accessDetails;

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }

    public AccessDetails getAccessDetails() {
        return accessDetails;
    }

    public Record(VALUE value, long loadTime, AccessDetails accessDetails) {
        this.value = value;
        this.loadTime = loadTime;
        this.accessDetails = accessDetails;
    }
    public VALUE getValue() {
        return value;
    }

}

class AccessDetails{

    private long accessTimeStamp
    private long accessCount;

    public long getAccessTimeStamp() {
        return accessTimeStamp;
    }

    public void setAccessTimeStamp(long accessTimeStamp) {
        this.accessTimeStamp = accessTimeStamp;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }
}
