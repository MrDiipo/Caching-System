import javax.sql.DataSource;
import java.util.concurrent.Future;

public class Cache<KEY,VALUE> {

    // Data source connected to this cache
    private DataSource<KEY, VALUE> dataSource;


    public Future<VALUE> get(KEY key){

    }

    public Future<Void> set(KEY key, VALUE value){

    }
}
