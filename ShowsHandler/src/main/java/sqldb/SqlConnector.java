package sqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 * @author Martin Ramonda
 * Clase para comprobar si una determinada base de datos existe en la ruta indicada.
 * Las credenciales de acceso y la ruta jdbc puede ser modificada para diferentes accesos.
 */
public class SqlConnector {
    
    private static final String JDBC_URL = "jdbc:mariadb://localhost:1801/";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "slimm1db";
    private static final String DATABASE_NAME = "reports";
    
    // realiza una operacion de busqueda para comprobar si la base de datos existe en el puerto establecido.
    // si no existe, la crea.
    public void checkOrCreateDatabase(){
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE '" + DATABASE_NAME + "'");
                if (!resultSet.next()) {
                    createDatabase(statement);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al abrir la conexion sql. REVISAR QUE EL PUERTO 1801 ESTA OPERATIVO y reinicia la app", "ERROR", JOptionPane.OK_OPTION);
        }
    }

    private void createDatabase(Statement statement) throws SQLException {
        String createDatabaseSQL = "CREATE DATABASE " + DATABASE_NAME;
        statement.executeUpdate(createDatabaseSQL);
    }
}
