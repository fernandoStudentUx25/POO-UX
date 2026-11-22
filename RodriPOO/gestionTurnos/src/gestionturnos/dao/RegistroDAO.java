
package gestionturnos.dao;

import gestionturnos.conexion.ConexionBD;
import java.sql.*;

public class RegistroDAO {

    public String obtenerUltimoMovimiento(int idEmpleado)
            throws SQLException {

        String sql = """
            SELECT t.nombre
            FROM registro r
            INNER JOIN tipo_registro t
                ON r.tipo_registro_id = t.id_tipo_registro
            WHERE r.id_empleado = ?
            ORDER BY r.fecha_hora DESC, r.id_registro DESC
            LIMIT 1
            """;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        }

        return null;
    }

    public String registrarMovimiento(int idEmpleado, int tipo)
            throws SQLException {

        if (tipo != 1 && tipo != 2) {
            return "Tipo de movimiento invalido.";
        }

        try (Connection con = ConexionBD.obtenerConexion()) {

            con.setAutoCommit(false);

            try {
                // Verificar empleado y bloquearlo durante el registro
                String sqlEmpleado =
                    "SELECT activo FROM empleado " +
                    "WHERE id_empleado = ? FOR UPDATE";

                try (PreparedStatement ps =
                        con.prepareStatement(sqlEmpleado)) {

                    ps.setInt(1, idEmpleado);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            return "El empleado no existe.";
                        }

                        if (!rs.getBoolean("activo")) {
                            return "El empleado esta inactivo.";
                        }
                    }
                }

                // Consultar el ultimo movimiento
                String sqlUltimo = """
                    SELECT tipo_registro_id
                    FROM registro
                    WHERE id_empleado = ?
                    ORDER BY fecha_hora DESC, id_registro DESC
                    LIMIT 1
                    """;

                Integer ultimo = null;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlUltimo)) {

                    ps.setInt(1, idEmpleado);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ultimo = rs.getInt("tipo_registro_id");
                        }
                    }
                }

                // Validaciones
                if (tipo == 1 && ultimo != null && ultimo == 1) {
                    return "Ya tienes una entrada registrada.";
                }

                if (tipo == 2 && (ultimo == null || ultimo != 1)) {
                    return "No tienes una entrada pendiente.";
                }

                // Guardar movimiento
                String sqlInsert = """
                    INSERT INTO registro
                    (id_empleado, fecha_hora, tipo_registro_id)
                    VALUES (?, CURRENT_TIMESTAMP, ?)
                    """;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlInsert)) {

                    ps.setInt(1, idEmpleado);
                    ps.setInt(2, tipo);
                    ps.executeUpdate();
                }

                con.commit();

                return tipo == 1
                    ? "ENTRADA registrada correctamente."
                    : "SALIDA registrada correctamente.";

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

public java.util.List<Object[]> listarRegistros()
        throws SQLException {

    java.util.List<Object[]> registros =
            new java.util.ArrayList<>();

    String sql = """
        SELECT
            r.id_registro,
            r.id_empleado,
            e.nombre || ' ' || e.apellido AS empleado,
            TO_CHAR(r.fecha_hora, 'DD/MM/YYYY HH24:MI:SS') AS fecha,
            t.nombre AS movimiento
        FROM registro r
        INNER JOIN empleado e
            ON r.id_empleado = e.id_empleado
        INNER JOIN tipo_registro t
            ON r.tipo_registro_id = t.id_tipo_registro
        ORDER BY r.fecha_hora DESC, r.id_registro DESC
        """;

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            registros.add(new Object[]{
                rs.getInt("id_registro"),
                rs.getInt("id_empleado"),
                rs.getString("empleado"),
                rs.getString("fecha"),
                rs.getString("movimiento")
            });
        }
    }

    return registros;
}

}
