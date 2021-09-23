import java.util.concurrent.Future;

public class DataSource<KEY, VALUE> {

    public Future<VALUE> get(){
        return null;
    }

    public Future<Void> persist(KEY key, VALUE value){
        return null;
    }

}
