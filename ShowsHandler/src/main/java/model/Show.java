package model;

import java.util.List;
import java.time.LocalDateTime;

/**
 * @author Martin Ramonda
 */
public class Show {
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private String description;
    private String url;
    private List<Category> cats;
}
