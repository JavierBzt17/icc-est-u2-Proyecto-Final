package vista;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import modelo.Nodo;

public class PanelMapa extends JPanel {
    private Image imagenMapa;
    private Map<String, Nodo> nodos;
    private Map<String, List<String>> adyacencias;
    private List<Nodo> rutaActual;
    private Nodo nodoInicio, nodoFin;

    public PanelMapa() {
        try {
            imagenMapa = new ImageIcon("imagenes/mapa.png").getImage();
        } catch (Exception e) {}
    }

    public void actualizar(Map<String, Nodo> ns, Map<String, List<String>> ady, List<Nodo> ruta, Nodo inicio, Nodo fin) {
        this.nodos = ns;
        this.adyacencias = ady;
        this.rutaActual = ruta;
        this.nodoInicio = inicio;
        this.nodoFin = fin;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (imagenMapa != null) g.drawImage(imagenMapa, 0, 0, getWidth(), getHeight(), this);

        if (nodos != null && adyacencias != null) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLUE);
            for (String id : adyacencias.keySet()) {
                Nodo n1 = nodos.get(id);
                for (String vecino : adyacencias.get(id)) {
                    Nodo n2 = nodos.get(vecino);
                    g2.drawLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
                }
            }
        }

        if (rutaActual != null && !rutaActual.isEmpty()) {
            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.GREEN);
            for (int i = 0; i < rutaActual.size() - 1; i++) {
                Nodo n1 = rutaActual.get(i);
                Nodo n2 = rutaActual.get(i+1);
                g2.drawLine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
            }
        }

        if (nodos != null) {
            for (Nodo n : nodos.values()) {
                if (n == nodoInicio) g2.setColor(Color.GREEN);
                else if (n == nodoFin) g2.setColor(Color.RED);
                else g2.setColor(Color.ORANGE);
                
                g2.fillOval(n.getX()-6, n.getY()-6, 12, 12);
                g2.setColor(Color.BLACK);
                g2.drawString(n.getId(), n.getX()+8, n.getY()-6);
            }
        }
    }
}