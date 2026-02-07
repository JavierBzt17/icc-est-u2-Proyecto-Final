package view;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import model.Nodo;

public class MapaPanel extends JPanel {

    private Image mapa;
    private int imgW;
    private int imgH;

    private double escala;
    private int offsetX;
    private int offsetY;

    private Map<String, Nodo> nodos;
    private Nodo nodoInicio;
    private Nodo nodoFin;

    public MapaPanel() {

        ImageIcon icono = new ImageIcon("resources/Mapa.png");
        mapa = icono.getImage();
        imgW = icono.getIconWidth();
        imgH = icono.getIconHeight();
    }

    public void actualizar(Map<String, Nodo> nodos,
                           Set<String> aristas,
                           List<Nodo> ruta,
                           List<Nodo> animacion,
                           Map<String, String> padres,
                           Nodo inicio,
                           Nodo fin) {

        this.nodos = nodos;
        this.nodoInicio = inicio;
        this.nodoFin = fin;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();

        escala = Math.min(
                (double) panelW / imgW,
                (double) panelH / imgH
        );

        int mapaW = (int) (imgW * escala);
        int mapaH = (int) (imgH * escala);

        offsetX = (panelW - mapaW) / 2;
        offsetY = (panelH - mapaH) / 2;

        g.drawImage(mapa, offsetX, offsetY, mapaW, mapaH, this);

        if (nodos == null) return;

        Graphics2D g2 = (Graphics2D) g;

        for (Nodo n : nodos.values()) {

            int x = (int) (n.getX() * escala) + offsetX;
            int y = (int) (n.getY() * escala) + offsetY;

            if (n == nodoInicio)
                g2.setColor(Color.GREEN);
            else if (n == nodoFin)
                g2.setColor(Color.RED);
            else
                g2.setColor(Color.BLACK);

            g2.fillOval(x - 6, y - 6, 12, 12);
        }
    }

    public int convertirX(int mouseX) {
        return (int) ((mouseX - offsetX) / escala);
    }

    public int convertirY(int mouseY) {
        return (int) ((mouseY - offsetY) / escala);
    }
}
