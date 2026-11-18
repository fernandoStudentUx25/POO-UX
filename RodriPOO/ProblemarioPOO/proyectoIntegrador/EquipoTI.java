package proyectoIntegrador;
import java.util.*;
public abstract class EquipoTI {
    private String nombre;
    private String fechaExpiracion;

    public EquipoTI(){}

    public EquipoTI(String nombre, String fechaExpiracion) {
        this.nombre = nombre;
        this.fechaExpiracion = fechaExpiracion;
    }

    public abstract boolean conexionInternet(String provedor);

    public abstract void consumoDeEnergia();

    public abstract void mostrarEquiposTI(List<EquipoTI> equipos);

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }


    
}