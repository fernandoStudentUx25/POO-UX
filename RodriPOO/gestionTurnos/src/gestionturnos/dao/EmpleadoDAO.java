
package gestionturnos.dao;

import gestionturnos.conexion.ConexionBD;
import gestionturnos.modelo.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpleadoDAO {

    public Empleado buscarPorId(int idEmpleado) throws SQLException {

        String sql = "SELECT id_empleado, nombre, apellido, activo "
                   + "FROM empleado WHERE id_empleado = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    return new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getBoolean("activo")
                    );
                }
            }
        }

        return null;
    }
}
