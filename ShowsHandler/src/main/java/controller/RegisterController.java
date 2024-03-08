package controller;


import view.RegisterFrame;

/**
 *
 * @author Martin Ramonda
 */
public class RegisterController {
    
    private RegisterFrame frame;
    
    private static RegisterController instance;
    
    private RegisterController(){

    }
    
    public static RegisterController getInstance(){
        if(instance == null){
            instance = new RegisterController();
        }
        return instance;
    }
    
    public void launchRegisterFrame(){
        frame = new RegisterFrame();
    }
    
    public RegisterFrame getMainFrame(){
        return frame;
    }
}
