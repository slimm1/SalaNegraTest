package model;

import java.util.List;
import java.time.LocalDate;
import org.bson.types.ObjectId;

/**
 * @author Martin Ramonda
 */
public class User {
    private ObjectId mongoId;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private List<Show> purchasedShows;
}
