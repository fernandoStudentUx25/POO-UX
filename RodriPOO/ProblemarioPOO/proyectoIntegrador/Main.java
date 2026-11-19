package proyectoIntegrador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<EquipoTI> equipos = Arrays.asList(
            new Laptop("Dell Latitude", "2027-12-31", "Dell", 15000),
            new Laptop("MacBook Pro", "2028-06-15", "Apple", 35000),

            new Servidor("Servidor Principal", "2028-12-31", "Ciudad de México"),
            new Servidor("Servidor Secundario", "2029-06-30", "Guadalajara")
        );
        EquipoTI laptop = new Laptop();
        System.out.println("***** ejemplo laptos *****");
        laptop.mostrarEquiposTI(equipos);

        EquipoTI servidor = new Servidor();
        System.out.println("***** ejemplo con equipos TI *****");
        servidor.mostrarEquiposTI(equipos);
    }
}
