package view;

import java.awt.*;
import java.util.Map;
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
    private Map<String, Boolean> aristasVisibles;

    private Nodo nodoInicio;
    private Nodo nodoFin;
    private Nodo nodoSeleccionado;

    public MapaPanel() {

        ImageIcon icono = new ImageIcon("resources/Mapa.png");
        mapa = icono.getImage();
        imgW = icono.getIconWidth();
        imgH = icono.getIconHeight();
    }

    public void actualizar(
            Map<String, Nodo> nodos,
            Map<String, Boolean> aristasVisibles,
            java.util.List<Nodo> ruta,
            java.util.List<Nodo> animacion,
            Map<String, String> padres,
            Nodo inicio,
            Nodo fin) {

        this.nodos = nodos;
        this.aristasVisibles = aristasVisibles;
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

        if (aristasVisibles != null) {

            g2.setStroke(new BasicStroke(3));

            for (Map.Entry<String, Boolean> entry : aristasVisibles.entrySet()) {

                String clave = entry.getKey();
                boolean bidireccional = entry.getValue();

                String[] partes = clave.split("-");

                if (partes.length == 2) {

                    Nodo n1 = nodos.get(partes[0]);
                    Nodo n2 = nodos.get(partes[1]);

                    if (n1 != null && n2 != null) {

                        int x1 = (int) (n1.getX() * escala) + offsetX;
                        int y1 = (int) (n1.getY() * escala) + offsetY;

                        int x2 = (int) (n2.getX() * escala) + offsetX;
                        int y2 = (int) (n2.getY() * escala) + offsetY;

                        if (bidireccional)
                            g2.setColor(new Color(180, 180, 180));
                        else
                            g2.setColor(new Color(90, 90, 90));

                        g2.drawLine(x1, y1, x2, y2);
                    }
                }
            }
        }

        for (Nodo n : nodos.values()) {

            int x = (int) (n.getX() * escala) + offsetX;
            int y = (int) (n.getY() * escala) + offsetY;

            if (n == nodoSeleccionado)
                g2.setColor(Color.BLUE);
            else if (n == nodoInicio)
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

    public void setNodoSeleccionado(Nodo n) {
        this.nodoSeleccionado = n;
        repaint();
    }
}
