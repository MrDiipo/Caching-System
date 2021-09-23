import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class Cache<KEY,VALUE> {

    private Map<KEY, VALUE> map = new ConcurrentHashMap<>(); // Data structure for the cache
    // Data source connected to this cache
    private DataSource<KEY, VALUE> dataSource;


    public Future<VALUE> get(KEY key){

        if (map.containsKey(key)){
            return CompletableFuture.completedFuture(map.get(key));
        } else {
            return dataSource.get(key); // Returns the key if not contained in the cache
        }
    }

    public Future<Void> set(KEY key, VALUE value){

        if (map.containsKey(key)){
            //
        }
    }
}
