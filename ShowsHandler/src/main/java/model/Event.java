package model;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 * @author Martin Ramonda
 */
public class Event {
    private ObjectId id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private String description;
    private String url;
    private List<Category> cats;
    
    public Event(){
        cats = new ArrayList();
    }

    public Event(String title, LocalDateTime startDateTime, LocalDateTime finishDateTime, String description, String url, List<Category> cats) {
        this.title = title;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
        this.description = description;
        this.url = url;
        this.cats = cats;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(LocalDateTime finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Category> getCats() {
        return cats;
    }

    public void setCats(List<Category> cats) {
        this.cats = cats;
    }
}
