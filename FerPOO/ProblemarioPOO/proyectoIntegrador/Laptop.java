package proyectoIntegrador;

import java.util.List;

public class Laptop extends EquipoTI{
    private String marca;
    private double precio;

    public Laptop(){}

    public Laptop(String nombre, String fechaExpiracion, String marca, double precio) {
        super(nombre,fechaExpiracion);
        this.marca = marca;
        this.precio = precio;
    }

    @Override
    public boolean conexionInternet(String provedor) {
        return provedor.toLowerCase().equals("telmex") ? true : false;
    }

    @Override
    public void consumoDeEnergia() {
       System.out.println("laptop consume 20% de la energia total");
    }

    @Override
    public void mostrarEquiposTI(List<EquipoTI> equipos) {
        for(EquipoTI equipo : equipos){
            if (equipo instanceof Laptop) {
                System.out.println(equipo.getNombre());
            }
        }        
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    
}
