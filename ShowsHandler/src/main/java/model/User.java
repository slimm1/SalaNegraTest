package model;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private List<Event> purchasedShows;

    public User() {
    }

    public User(String username, String password, String name, String surname, String email, String gender, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.purchasedShows = new ArrayList();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<Event> getPurchasedShows() {
        return purchasedShows;
    }

    public void setPurchasedShows(List<Event> purchasedShows) {
        this.purchasedShows = purchasedShows;
    }

    public ObjectId getMongoId() {
        return mongoId;
    }
}
