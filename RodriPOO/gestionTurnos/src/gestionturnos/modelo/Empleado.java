
package gestionturnos.modelo;

public class Empleado {

    private int idEmpleado;
    private String nombre;
    private String apellido;
    private boolean activo;

    public Empleado(int idEmpleado, String nombre,
                    String apellido, boolean activo) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = activo;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public boolean isActivo() {
        return activo;
    }
}
