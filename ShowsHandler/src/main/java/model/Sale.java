package model;

import java.time.LocalDateTime;

/**
 *
 * @author Martin Ramonda
 */
public class Sale {
    private long id;
    private User user;
    private Show show;
    private LocalDateTime saleDateTime;
    private int numTickets;
    private double totalPrice;
}
