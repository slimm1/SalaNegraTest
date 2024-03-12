package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Event;
import org.bson.conversions.Bson;

/**
 *
 * @author Martin Ramonda
 */
public class MongoEventHandler {
    
    private MongoCollection<Event> eventCollection;
    
    private static MongoEventHandler instance;
    
    // inicia la collecion en mongo a través de la base de datos de MongoConnector.
    private MongoEventHandler(){
        eventCollection = MongoConnector.getInstance().getDatabase().getCollection("events",Event.class);
    }
      
    public static MongoEventHandler getInstance(){
        if(instance == null){
            instance = new MongoEventHandler();
        }
        return instance;
    }
    
    public Event getEventByTitle(String title){
        Bson query = Filters.eq("title", title);
        return eventCollection.find(query).first();
    }
    
    public boolean insertEvent(Event e){
        try{
            eventCollection.insertOne(e);
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
    
    public List<Event> getEventsOnDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        Bson query = Filters.and(
            Filters.gte("startDateTime", startOfDay),
            Filters.lte("finishDateTime", endOfDay)
        );
        return eventCollection.find(query).into(new ArrayList<>());
    }
}
