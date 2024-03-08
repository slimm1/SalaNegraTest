
package controller;

import view.MainFrame;

/**
 * @author Martin Ramonda
 */
public class MainController {
    
    private MainFrame frame;
    
    private static MainController instance;
    
    private MainController(){
        
    }
    
    public static MainController getInstance(){
        if(instance == null){
            instance = new MainController();
        }
        return instance;
    }
    
    public void launchMainFrame(){
        frame = new MainFrame();
    }
    
    public MainFrame getMainFrame(){
        return frame;
    }
}
