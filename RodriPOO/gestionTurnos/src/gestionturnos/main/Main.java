
package gestionturnos.main;

import gestionturnos.vista.VistaTurnos;
import gestionturnos.controlador.TurnosControlador;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VistaTurnos vista = new VistaTurnos();

            new TurnosControlador(vista);

            vista.setVisible(true);
        });
    }
}
    