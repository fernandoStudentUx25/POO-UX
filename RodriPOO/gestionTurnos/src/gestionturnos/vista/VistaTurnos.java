
package gestionturnos.vista;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VistaTurnos extends JFrame {

    private JTextField campoId;
    private JButton botonEntrada;
    private JButton botonSalida;
    private JLabel reloj;
    private JButton botonRegistros;


    public VistaTurnos() {

        setTitle("Gestion de Turnos");
        setSize(920, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fondo = new JPanel(null);
        fondo.setBackground(new Color(17, 17, 17));
        setContentPane(fondo);

        // Marco principal
        JPanel marco = crearPanelRedondeado(
                new Color(17, 17, 17),
                Color.LIGHT_GRAY, 35);
        
        
        botonRegistros = crearBoton(
        "VER REGISTROS",
        new Color(17, 17, 17),
        Color.WHITE);

        botonRegistros.setBounds(30, 25, 175, 52);
        marco.add(botonRegistros);


        marco.setLayout(null);
        marco.setBounds(40, 20, 825, 470);
        fondo.add(marco);

        // Fecha y hora
        JPanel cajaReloj = crearPanelRedondeado(
                new Color(215, 215, 215),
                Color.LIGHT_GRAY, 18);

        cajaReloj.setLayout(new BorderLayout());
        cajaReloj.setBounds(292, 40, 240, 40);

        reloj = new JLabel();
        reloj.setHorizontalAlignment(SwingConstants.CENTER);
        reloj.setFont(new Font("Consolas", Font.PLAIN, 14));
        reloj.setForeground(Color.BLACK);

        cajaReloj.add(reloj);
        marco.add(cajaReloj);

        actualizarReloj();

        Timer timer = new Timer(1000, e -> actualizarReloj());
        timer.start();

        // Campo ID empleado
        JPanel cajaId = crearPanelRedondeado(
                new Color(76, 60, 115),
                Color.LIGHT_GRAY, 35);

        cajaId.setLayout(new BorderLayout());
        cajaId.setBounds(165, 100, 490, 75);

        campoId = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    String texto = "INGRESA NUMERO DE COLABORADOR";

                    g2.setColor(new Color(220, 215, 230));
                    g2.setFont(new Font("Consolas", Font.PLAIN, 14));

                    FontMetrics fm = g2.getFontMetrics();

                    int x = (getWidth() - fm.stringWidth(texto)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2
                            + fm.getAscent();

                    g2.drawString(texto, x, y);
                    g2.dispose();
                }
            }
        };

        campoId.setFont(new Font("Consolas", Font.PLAIN, 20));
        campoId.setHorizontalAlignment(JTextField.CENTER);
        campoId.setForeground(Color.WHITE);
        campoId.setCaretColor(Color.WHITE);
        campoId.setOpaque(false);
        campoId.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));

        cajaId.add(campoId, BorderLayout.CENTER);
        marco.add(cajaId);

        // Boton entrada
        botonEntrada = crearBoton(
                "ENTRADA",
                new Color(0, 70, 15),
                Color.WHITE);

        botonEntrada.setBounds(35, 230, 270, 60);
        marco.add(botonEntrada);

        // Boton salida
        botonSalida = crearBoton(
                "SALIDA",
                new Color(215, 215, 215),
                Color.BLACK);

        botonSalida.setBounds(495, 230, 275, 60);
        marco.add(botonSalida);
    }

    private JPanel crearPanelRedondeado(
            Color fondo, Color borde, int radio) {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(fondo);
                g2.fillRoundRect(1, 1,
                        getWidth() - 3, getHeight() - 3,
                        radio, radio);

                g2.setColor(borde);
                g2.drawRoundRect(1, 1,
                        getWidth() - 3, getHeight() - 3,
                        radio, radio);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        return panel;
    }

    private JButton crearBoton(
            String texto, Color fondo, Color colorTexto) {

        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(fondo);
                g2.fillRoundRect(1, 1,
                        getWidth() - 3, getHeight() - 3,
                        20, 20);

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(1, 1,
                        getWidth() - 3, getHeight() - 3,
                        20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setOpaque(false);
        boton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    private void actualizarReloj() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                "dd/MM/yyyy hh:mma", Locale.US);

        reloj.setText(LocalDateTime.now().format(formato));
    }

    public String getIdEmpleado() {
        return campoId.getText().trim();
    }

    public JButton getBotonEntrada() {
        return botonEntrada;
    }

    public JButton getBotonSalida() {
        return botonSalida;
    }

    public void limpiarCampo() {
        campoId.setText("");
    }

    public void habilitarBotones(boolean habilitar) {
        botonEntrada.setEnabled(habilitar);
        botonSalida.setEnabled(habilitar);
    }

    public JButton getBotonRegistros() {
    return botonRegistros;
}

}

