package hanoi;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.*;

public class Hanoi extends JPanel {

    // Una pila por torre: 0 = A, 1 = B, 2 = C
    ArrayList<Stack<Integer>> torres = new ArrayList<>();
    // Lista de movimientos {origen, destino} generada con recursividad
    ArrayList<int[]> movimientos = new ArrayList<>();
    int paso = 0;
    int n = 3;

    Timer timer;
    JComboBox<Integer> combo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
    JLabel info = new JLabel(" ");

    public Hanoi() {
        for (int i = 0; i < 3; i++) {
            torres.add(new Stack<>());
        }
        combo.setSelectedItem(3);

        JButton btnResolver = new JButton("Resolver");
        JButton btnReiniciar = new JButton("Reiniciar");

        JPanel arriba = new JPanel();
        arriba.add(new JLabel("Discos:"));
        arriba.add(combo);
        arriba.add(btnResolver);
        arriba.add(btnReiniciar);

        setLayout(new BorderLayout());
        add(arriba, BorderLayout.NORTH);
        add(info, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(620, 360));

        timer = new Timer(500, e -> {
            if (!darPaso()) {
                timer.stop();
            }
        });

        combo.addActionListener(e -> reiniciar());
        btnReiniciar.addActionListener(e -> reiniciar());
        btnResolver.addActionListener(e -> {
            reiniciar();
            generar(n, 0, 1, 2);
            timer.start();
        });

        reiniciar();
    }

    // Deja las torres en la posición inicial: todos los discos en A
    void reiniciar() {
        if (timer != null) {
            timer.stop();
        }
        n = (Integer) combo.getSelectedItem();
        for (Stack<Integer> t : torres) {
            t.clear();
        }
        for (int d = n; d >= 1; d--) {
            torres.get(0).push(d);
        }
        movimientos.clear();
        paso = 0;
        actualizarInfo("Listo para resolver");
        repaint();
    }

    // MÉTODO RECURSIVO: genera la lista de movimientos
    void generar(int k, int origen, int auxiliar, int destino) {
        if (k == 1) {                                  // caso base
            movimientos.add(new int[]{origen, destino});
            return;
        }
        generar(k - 1, origen, destino, auxiliar);     // 1) n-1 a la auxiliar
        movimientos.add(new int[]{origen, destino});   // 2) el mayor al destino
        generar(k - 1, auxiliar, origen, destino);     // 3) n-1 al destino
    }

    // Ejecuta el siguiente movimiento de la lista. Devuelve false si ya no hay más.
    boolean darPaso() {
        if (paso >= movimientos.size()) {
            return false;
        }
        int[] m = movimientos.get(paso);
        int disco = torres.get(m[0]).pop();
        Stack<Integer> destino = torres.get(m[1]);
        if (!destino.isEmpty() && destino.peek() < disco) {
            throw new IllegalStateException("Movimiento no permitido");
        }
        destino.push(disco);
        paso++;
        actualizarInfo("Disco " + disco + ": " + nombre(m[0]) + " -> " + nombre(m[1]));
        repaint();
        return true;
    }

    String nombre(int t) {
        return "" + (char) ('A' + t);
    }

    void actualizarInfo(String actual) {
        info.setText("  Movimiento actual: " + actual
                + "   |   Movimientos realizados: " + paso + " de " + movimientos.size());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int base = 320;
        for (int t = 0; t < 3; t++) {
            int x = 110 + t * 200;
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x - 3, base - 220, 6, 220);   // poste
            g.fillRect(x - 90, base, 180, 5);        // base
            g.drawString(nombre(t), x - 3, base + 20);
            int y = base;
            for (int disco : torres.get(t)) {         // de abajo hacia arriba
                int ancho = 30 + disco * 20;
                y -= 22;
                g.setColor(Color.getHSBColor(disco / 9f, 0.7f, 0.9f));
                g.fillRect(x - ancho / 2, y, ancho, 20);
                g.setColor(Color.BLACK);
                g.drawRect(x - ancho / 2, y, ancho, 20);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Torre de Hanoi");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new Hanoi());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
