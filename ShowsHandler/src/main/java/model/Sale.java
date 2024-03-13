package model;

import java.time.LocalDateTime;

/**
 *
 * @author Martin Ramonda
 */
public class Sale {
    private long id;
    private User user;
    private Event show;
    private LocalDateTime saleDateTime;
    private int numTickets;
    private double totalPrice;

    public Sale() {
    }

    public Sale(User user, Event show, LocalDateTime saleDateTime, int numTickets, double totalPrice) {
        this.user = user;
        this.show = show;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getShow() {
        return show;
    }

    public void setShow(Event show) {
        this.show = show;
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
