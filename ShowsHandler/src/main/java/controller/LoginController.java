package controller;

import api.ApiHandler;
import mongodb.MongoConnector;
import view.LoginFrame;

/**
 * @author Martin Ramonda
 */
public class LoginController {
    
    private LoginFrame frame;
    
    private final ApiHandler api;
    
    private static LoginController instance;
    
    private LoginController(){
        MongoConnector.getInstance().tryConnect();
        api = new ApiHandler();
        api.storeApiResponse();
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
