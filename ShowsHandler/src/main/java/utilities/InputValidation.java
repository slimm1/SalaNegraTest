package utilities;

import java.util.Date;
import javax.swing.JOptionPane;
import mongodb.MongoDataLoader;

/**
 * @author Martin Ramonda
 */
public class InputValidation {
    
    public static boolean validateLogin(String username, String password){
        password = Security.hashSha256(password);
        if(username.isBlank() || password.isBlank()){
            JOptionPane.showConfirmDialog(null, "Debes completar todos los campos para continuar", "ALERTA", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if(MongoDataLoader.getInstance().getUser(username, password) == null){
            JOptionPane.showConfirmDialog(null, "Usuario o contraseña incorrecta", "ALERTA", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    public static boolean validateRegister(String name, String surname, String email, Date birthDate, String gender, String username, String pass, String repeatPass){
        if(name.isBlank() || surname.isBlank() || email.isBlank() || birthDate == null || gender == null || username.isBlank() || pass.isBlank() || repeatPass.isBlank()){
            JOptionPane.showConfirmDialog(null, "Debes completar todos los campos para continuar", "ALERTA", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if(!pass.equalsIgnoreCase(repeatPass)){
            JOptionPane.showConfirmDialog(null, "Las contraseñas no coinciden", "ALERTA", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(MongoDataLoader.getInstance().getUserByEmail(email)!=null){
            JOptionPane.showConfirmDialog(null, "Ya existe un usuario con el email indicado", "ALERTA", JOptionPane.ERROR_MESSAGE);            
            return false;
        }
        else if(MongoDataLoader.getInstance().getUserByUsername(username)!=null){
            JOptionPane.showConfirmDialog(null, "Ya existe un usuario con ese nombre de usuario", "ALERTA", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
}
