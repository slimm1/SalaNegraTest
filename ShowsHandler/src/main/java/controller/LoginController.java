package controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import mongodb.MongoConnector;
import view.LoginFrame;

/**
 * @author Martin Ramonda
 */
public class LoginController {
    
    private LoginFrame frame;
    
    private static LoginController instance;
    
    private LoginController(){
        //MongoConnector.getInstance().tryConnect();
    }
    
    public static LoginController getInstance(){
        if(instance == null){
            instance = new LoginController();
        }
        return instance;
    }
    
    public void launchLoginFrame(){
        frame = new LoginFrame();
    }
    
    public LoginFrame getLoginFrame(){
        return frame;
    }
}
