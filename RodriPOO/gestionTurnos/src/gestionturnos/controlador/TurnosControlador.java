
package gestionturnos.controlador;

import gestionturnos.vista.VistaTurnos;
import gestionturnos.dao.RegistroDAO;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

import gestionturnos.vista.VistaRegistros;

public class TurnosControlador {

    private VistaTurnos vista;
    private RegistroDAO registroDAO;

    public TurnosControlador(VistaTurnos vista) {

        this.vista = vista;
        this.registroDAO = new RegistroDAO();

        vista.getBotonEntrada().addActionListener(
                e -> registrarMovimiento(1));

        vista.getBotonSalida().addActionListener(
                e -> registrarMovimiento(2));
        
        vista.getBotonRegistros().addActionListener(
        e -> abrirRegistros());

    }

    private void registrarMovimiento(int tipo) {

        int idEmpleado;

        try {
            idEmpleado = Integer.parseInt(vista.getIdEmpleado());

            if (idEmpleado <= 0) {
                mostrarMensaje("Ingresa un ID valido.");
                return;
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un numero de empleado valido.");
            return;
        }

        vista.habilitarBotones(false);

        // Ejecutar la consulta sin bloquear Swing
        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                return registroDAO.registrarMovimiento(
                        idEmpleado, tipo);
            }

            @Override
            protected void done() {

                try {
                    String resultado = get();

                    boolean correcto = resultado.contains(
                            "registrada correctamente.");

                    JOptionPane.showMessageDialog(
                            vista,
                            resultado,
                            correcto ? "Registro exitoso" : "Aviso",
                            correcto
                                ? JOptionPane.INFORMATION_MESSAGE
                                : JOptionPane.WARNING_MESSAGE
                    );

                    if (correcto) {
                        vista.limpiarCampo();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mostrarMensaje("Operacion interrumpida.");

                } catch (ExecutionException e) {
                    mostrarMensaje("Error al registrar: "
                            + e.getCause().getMessage());

                } finally {
                    vista.habilitarBotones(true);
                }
            }

        }.execute();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje);
    }

private void abrirRegistros() {

    VistaRegistros ventana = new VistaRegistros(vista);

    vista.setVisible(false);
    ventana.setVisible(true);

    new SwingWorker<java.util.List<Object[]>, Void>() {

        @Override
        protected java.util.List<Object[]> doInBackground()
                throws Exception {

            return registroDAO.listarRegistros();
        }

        @Override
        protected void done() {

            try {
                ventana.mostrarRegistros(get());

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                JOptionPane.showMessageDialog(
                    ventana, "Consulta interrumpida.");

            } catch (ExecutionException e) {

                JOptionPane.showMessageDialog(
                    ventana,
                    "Error al consultar registros: "
                    + e.getCause().getMessage());
            }
        }

    }.execute();
}

}
