
package gestionturnos.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:postgresql://[::1]:5432/Gestor_Turnos";

    private static final String USUARIO = "postgres";

    private static final String PASSWORD = "Javarod29";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                URL, USUARIO, PASSWORD
        );
    }
}
