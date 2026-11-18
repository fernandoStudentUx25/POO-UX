package proyectoIntegrador;

import java.util.List;

public class Servidor extends EquipoTI{
    private String locacion;

    public Servidor(){}

    public Servidor(String nombre, String fechaExpiracion, String locacion) {
        super(nombre, fechaExpiracion);
        this.locacion = locacion;
    }

    @Override
    public boolean conexionInternet(String provedor) {
        return provedor.toLowerCase().equals("pillophone") ? true : false;
    }

    @Override
    public void consumoDeEnergia() {
       System.out.println("servidor consume 80% de la energia total");
    }

    @Override
    public void mostrarEquiposTI(List<EquipoTI> equipos) {
        for(EquipoTI equipo : equipos){
            System.out.println(equipo.getNombre());
        }        
    }

    public String getLocacion() {
        return locacion;
    }

    public void setLocacion(String locacion) {
        this.locacion = locacion;
    }
    
}
