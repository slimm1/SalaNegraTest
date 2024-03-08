package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Show;
import model.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * @author Martin Ramonda
 * Esta clase incluye los métodos de acceso a la base de datos.
 */
public class MongoDataLoader {
    
    private MongoCollection<User> userCollection;
    private MongoCollection<Show> showCollection;
    
    private static MongoDataLoader instance;
    
    // inicia la collecion en mongo a través de la base de datos de MongoConnector.
    private MongoDataLoader(){
        userCollection = MongoConnector.getInstance().getDatabase().getCollection("user", User.class);
        showCollection = MongoConnector.getInstance().getDatabase().getCollection("eventos",Show.class);
    }
    
    public boolean insertUserIntoDb(User user){
        try{
            userCollection.insertOne(user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    //Obtiene un bson(Document) con el id especificado y busca y reemplaza la coincidencia en la base de datos
    public boolean updateUserInDb(User user){
        try{
            Document doc = new Document("_id", user.getMongoId());
            userCollection.findOneAndReplace(doc, user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public User getUser(String username, String pass){
        Bson filter = Filters.and(
                Filters.eq("username", username),
                Filters.eq("password",pass));
        return userCollection.find(filter).first();
    }
    
    public User getUserByEmail(String email){
        Bson filter = Filters.eq("email",email);
        return userCollection.find(filter).first();
    }
    
    public User getUserByUsername(String username){
        Bson filter = Filters.eq("username",username);
        return userCollection.find(filter).first();
    }
    
    public boolean removeUserFromDb(ObjectId id){
        if (id == null) {
            return false;
        }
        try{
            Document doc = new Document("_id", id);
            userCollection.findOneAndDelete(doc);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    //Uso de filtro para determinar si la cadena coincide totalmente con shows.titulo.
    // ejemplo de acceso a un objeto diferente dentro de una coleccion de objetos
    public List<User> listByShow(String show){
        if(show.isEmpty()){return null;}
        Bson filter = Filters.eq("shows.titulo", show);
        return userCollection.find(filter).into(new ArrayList());
    }
    
    // Filtro para determinar si un registro contiene una determinada cadena de caracteres.
    public List<User> listByUsername(String username){
        if(username.isEmpty()){return null;}
        Bson filter = Filters.regex("username", username);
        return userCollection.find(filter).into(new ArrayList());
    }
    
    public List<User> listByAge(int age){
        Bson filter = Filters.eq("edad", age);
        return userCollection.find(filter).into(new ArrayList());
    }
    
    //devuelve toda la coleccion en una lista de objetos.
    public List<User> getAllUsers(){
        return userCollection.find().into(new ArrayList());
    }
    
    public Set<Show> getAllShow(){
        Set<Show> allShows = new HashSet();
        MongoCursor<User> cursor = userCollection.find().iterator();
        while(cursor.hasNext()){
            User u = cursor.next();
            allShows.addAll(u.getPurchasedShows());
        }
        return allShows;
    }
    
    public Show getOneShow(Show show){
        MongoCursor<User> cursor = userCollection.find().iterator();
        while(cursor.hasNext()){
            User u = cursor.next();
            if(u.getPurchasedShows().contains(show)) return u.getPurchasedShows().get(u.getPurchasedShows().indexOf(show));
        }        
        return null;
    }
    
    public static MongoDataLoader getInstance(){
        if(instance==null){
            instance = new MongoDataLoader();
        }
        return instance;
    }
}
