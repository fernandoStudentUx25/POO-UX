
package gestionturnos.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class VistaRegistros extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    public VistaRegistros(JFrame menuPrincipal) {

        setTitle("Registros de Turnos");
        setSize(860, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel fondo = new JPanel(null);
        fondo.setBackground(new Color(17, 17, 17));
        setContentPane(fondo);

        // Marco principal
        JPanel marco = crearPanelRedondeado(35);
        marco.setLayout(null);
        marco.setBounds(25, 20, 790, 460);
        fondo.add(marco);

        // Boton volver
        JButton botonVolver = new JButton("VOLVER");

        botonVolver.setBounds(30, 25, 130, 45);
        botonVolver.setBackground(new Color(25, 25, 25));
        botonVolver.setForeground(Color.WHITE);
        botonVolver.setFont(
                new Font("Consolas", Font.BOLD, 14));
        botonVolver.setFocusPainted(false);
        botonVolver.setBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        marco.add(botonVolver);

        // Contenedor de la tabla
        JPanel contenedor = crearPanelRedondeado(30);
        contenedor.setLayout(null);
        contenedor.setBounds(75, 95, 640, 320);
        marco.add(contenedor);

        // Modelo JTable
        modelo = new DefaultTableModel(
                new Object[]{
                    "ID", "ID Empleado", "Empleado",
                    "Fecha y hora", "Movimiento"
                }, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));

        tabla.setBackground(new Color(30, 30, 30));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(75, 75, 75));

        tabla.setSelectionBackground(new Color(76, 60, 115));
        tabla.setSelectionForeground(Color.WHITE);

        tabla.getTableHeader().setBackground(
                new Color(65, 65, 65));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setPreferredSize(
                new Dimension(0, 35));

        tabla.setFillsViewportHeight(true);

        tabla.getColumnModel().getColumn(0)
                .setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1)
                .setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2)
                .setPreferredWidth(140);
        tabla.getColumnModel().getColumn(3)
                .setPreferredWidth(170);
        tabla.getColumnModel().getColumn(4)
                .setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(15, 15, 610, 290);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        contenedor.add(scroll);

        // Regresar al menu
        botonVolver.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                menuPrincipal.setVisible(true);
            }
        });
    }

    private JPanel crearPanelRedondeado(int radio) {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(17, 17, 17));
                g2.fillRoundRect(1, 1,
                    getWidth() - 3, getHeight() - 3,
                    radio, radio);

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(1, 1,
                    getWidth() - 3, getHeight() - 3,
                    radio, radio);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        return panel;
    }

    public void mostrarRegistros(List<Object[]> registros) {

        modelo.setRowCount(0);

        for (Object[] registro : registros) {
            modelo.addRow(registro);
        }
    }
}
