package model;

import java.time.LocalDateTime;

/**
 * @author Martin Ramonda
 */
public class Session {
    private LocalDateTime loginDateTime;
    private LocalDateTime logoutDateTime;
    private User user;
}
