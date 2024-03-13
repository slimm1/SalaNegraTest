package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Martin Ramonda
 */
@Entity
@Table(name = "sales")
@NamedQueries(value = {
    @NamedQuery(
        name = "Sale.findByEvent",
        query = "SELECT s FROM Sale s WHERE s.event = :eventName"
    ),
        @NamedQuery(
        name = "Sale.findByCategory",
        query = "SELECT s FROM Sale s WHERE s.category = :categoryName"
    ),
        @NamedQuery(
        name = "Sale.findByMonth",
        query = "SELECT s FROM Sale s WHERE FUNCTION('MONTH', s.saleDateTime) = :month"
    ),
        @NamedQuery(
        name = "Sale.findByYear",
        query = "SELECT s FROM Sale s WHERE FUNCTION('YEAR', s.saleDateTime) = :year"
    ),
        @NamedQuery(
        name = "Sale.findByPriceGreaterThan",
        query = "SELECT s FROM Sale s WHERE s.totalPrice > :price"
    )
})
public class Sale implements Serializable {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String user;
    private String event;
    private LocalDateTime saleDateTime;
    private int numTickets;
    private double totalPrice;

    public Sale() {
    }

    public Sale(String user, String event, LocalDateTime saleDateTime, int numTickets, double totalPrice) {
        this.user = user;
        this.event = event;
        this.saleDateTime = saleDateTime;
        this.numTickets = numTickets;
        this.totalPrice = totalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    public void setSaleDateTime(LocalDateTime saleDateTime) {
        this.saleDateTime = saleDateTime;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public void setNumTickets(int numTickets) {
        this.numTickets = numTickets;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    
}
