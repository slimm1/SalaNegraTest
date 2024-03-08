package mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.swing.JOptionPane;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoConnector {
    
    private static MongoConnector instance;
    
    // clase cliente de mongo. se usa para instanciar la base de datos.
    private MongoClient client;
    
    // clase de base de datos de mongo. se usa para acceder a las colecciones
    private MongoDatabase db;
    
    // coded mongo. "serializa" las clases para escribirlas en la base de datos.
    private CodecRegistry pojoCodecRegistry;
    
    private MongoConnector(){
        // inicia el codec pojo 
        initPojo();
    }
    
    public static MongoConnector getInstance(){
        if(instance == null){
            instance = new MongoConnector();
        }
        return instance;
    }
    
    /***
     * prueba la conexion a la base de datos mongo. La clase connectionString se crea a partir de la cadena de conexion.
     * MongoClientSettings determina unos ajustes para iniciar el cliente, que intenta conectarse a la base de datos desde el metodo
     * tryConnectConnection(MongoClient)
     * @return 
     */
    public boolean tryConnect(){
        ConnectionString connString = new ConnectionString("mongodb://localhost:57017");
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .build();
        try{
            client = MongoClients.create(settings);
            System.out.println("Probando conexion");
            tryConnectCollection(client);
            return true;
        }
        catch(MongoException m){
            JOptionPane.showConfirmDialog(null, "Error al abrir la conexion mongodb. REVISAR QUE EL PUERTO 57017 ESTA OPERATIVO y reinicia la app", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Instancia la base de datos con el codec pojo especificado en el constructor de esta clase. Además crea la coleccion almacenada en props
    private void tryConnectCollection(MongoClient client) throws MongoException{
        db = client.getDatabase("sala").withCodecRegistry(pojoCodecRegistry);
        db.createCollection("test");
    }
    
    public boolean dropDatabase(String dbName){
        try{
            db.drop();
            System.out.println();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public MongoDatabase getDatabase(){
        try{
            return db;
        }
        catch(Exception e){
            System.out.println("Parece que " + e.getMessage());
            return null;
        }
    }
    
    public ListDatabasesIterable<Document> listDatabases(){
        ListDatabasesIterable<Document> databases = client.listDatabases();
        return databases;
    }
    
    // inicia el codecregistry a traves del codec provider. estas instanciaciones son las que permiten que las clases se escriban automáticamente
    //en la base de datos.
    public void initPojo(){
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));
    }
}
