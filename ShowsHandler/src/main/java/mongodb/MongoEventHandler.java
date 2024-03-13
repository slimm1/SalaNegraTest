package mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Category;
import model.Event;
import org.bson.Document;
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
        return eventCollection.find(query).into(new ArrayList());
    }
    
    public Set<String> getAllCategories() {
        Set<String> categorias = new HashSet<>();
        MongoCursor<Event> cursor = eventCollection.find().iterator();
        while(cursor.hasNext()){
            Event e = cursor.next();
            e.getCats().forEach(cat->{
                categorias.add(cat.getCatName());
            });
        }
        return categorias;
    }
    
    public List<Event> getAllEvents(){
        return eventCollection.find().into(new ArrayList());
    }
    
    public List<Event> getEventsBy(String title, String category, int month){
        List<Bson> filters = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            filters.add(new Document("title", new Document("$regex", title)));
        }
        if (category != null && !category.isEmpty()) {
            filters.add(new Document("cats", new Document("$elemMatch", new Document("catName", category))));
        }
        if (month > 0 && month <= 12) {
            LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(month), 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
            filters.add(and(
                    gte("startDateTime", startOfMonth.atStartOfDay()),
                    lt("startDateTime", endOfMonth.plusDays(1).atStartOfDay())
            ));
        }
        Bson query = Filters.and(filters);
        return eventCollection.find(query).into(new ArrayList());
    }
}
