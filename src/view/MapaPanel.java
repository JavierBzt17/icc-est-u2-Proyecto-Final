package view;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import model.Nodo;

/**
 * Clase MapaPanel
 *
 * Es el panel gráfico encargado de dibujar:
 * - El mapa de fondo
 * - Los nodos
 * - Las aristas
 * - La ruta final encontrada
 * - La animación de recorrido
 *
 * También maneja:
 * - Escalado automático de la imagen
 * - Conversión de coordenadas del mouse
 * - Representación visual de nodos especiales (inicio, fin, seleccionados)
 */
public class MapaPanel extends JPanel {

    // Imagen de fondo del mapa
    private final Image mapa;

    // Dimensiones originales de la imagen
    private final int imgW;
    private final int imgH;

    // Escala aplicada al mapa
    private double escala;

    // Desplazamiento horizontal y vertical
    private int offsetX;
    private int offsetY;

    // Estructuras que recibe desde el controlador
    private Map<String, Nodo> nodos;
    private Map<String, Boolean> aristasVisibles;
    private List<Nodo> nodosAnimacion;
    private List<Nodo> rutaFinal;

    // Nodos especiales
    private Nodo nodoInicio;
    private Nodo nodoFin;
    private Nodo nodoSeleccionado;

    /**
     * Constructor del panel.
     * Carga la imagen del mapa desde resources.
     */
    public MapaPanel() {

        URL url = getClass().getResource("/resources/Mapa.png");
        if (url == null) {
            throw new RuntimeException("No se encontró /resources/Mapa.png en el classpath");
        }

        ImageIcon icono = new ImageIcon(url);
        mapa = icono.getImage();
        imgW = icono.getIconWidth();
        imgH = icono.getIconHeight();
    }

    /**
     * Actualiza la información que debe dibujarse en el panel.
     * Luego fuerza un repintado.
     */
    public void actualizar(
            Map<String, Nodo> nodos,
            Map<String, Boolean> aristas,
            List<Nodo> ruta,
            List<Nodo> animacion,
            Map<String, String> padres,
            Nodo inicio,
            Nodo fin) {

        this.nodos = nodos;
        this.aristasVisibles = aristas;
        this.rutaFinal = ruta;
        this.nodosAnimacion = animacion;
        this.nodoInicio = inicio;
        this.nodoFin = fin;

        repaint();
    }

    /**
     * Método principal de dibujo del panel.
     * Se ejecuta automáticamente cuando se llama a repaint().
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();

        // Calcula la escala manteniendo proporción
        escala = Math.min(
                (double) panelW / imgW,
                (double) panelH / imgH
        );

        int mapaW = (int) (imgW * escala);
        int mapaH = (int) (imgH * escala);

        // Centra el mapa dentro del panel
        offsetX = (panelW - mapaW) / 2;
        offsetY = (panelH - mapaH) / 2;

        // Dibuja el mapa de fondo
        g.drawImage(mapa, offsetX, offsetY, mapaW, mapaH, this);

        if (nodos == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /**
         * Dibuja las aristas visibles
         */
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

                        // Color diferente según tipo de arista
                        if (bidireccional)
                            g2.setColor(new Color(180, 180, 180));
                        else
                            g2.setColor(new Color(90, 90, 90));

                        g2.drawLine(x1, y1, x2, y2);

                        // Si es unidireccional dibuja flecha
                        if (!bidireccional) {
                            dibujarFlecha(g2, x1, y1, x2, y2);
                        }
                    }
                }
            }
        }

        /**
         * Dibuja la ruta final encontrada en color azul
         */
        if (rutaFinal != null && rutaFinal.size() > 1) {

            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(4));

            for (int i = 0; i < rutaFinal.size() - 1; i++) {

                Nodo n1 = rutaFinal.get(i);
                Nodo n2 = rutaFinal.get(i + 1);

                int x1 = (int) (n1.getX() * escala) + offsetX;
                int y1 = (int) (n1.getY() * escala) + offsetY;

                int x2 = (int) (n2.getX() * escala) + offsetX;
                int y2 = (int) (n2.getY() * escala) + offsetY;

                g2.drawLine(x1, y1, x2, y2);
            }
        }

        /**
         * Dibuja los nodos
         */
        for (Nodo n : nodos.values()) {

            int x = (int) (n.getX() * escala) + offsetX;
            int y = (int) (n.getY() * escala) + offsetY;

            // Asigna color según el estado del nodo
            if (n == nodoSeleccionado)
                g2.setColor(Color.BLUE);
            else if (n == nodoInicio)
                g2.setColor(Color.GREEN);
            else if (n == nodoFin)
                g2.setColor(Color.RED);
            else if (nodosAnimacion != null && nodosAnimacion.contains(n))
                g2.setColor(Color.CYAN);
            else if (rutaFinal != null && rutaFinal.contains(n))
                g2.setColor(Color.BLUE);
            else
                g2.setColor(Color.BLACK);

            g2.fillOval(x - 6, y - 6, 12, 12);
        }
    }

    /**
     * Dibuja la flecha para aristas unidireccionales.
     */
    private void dibujarFlecha(Graphics2D g2,
                               int x1, int y1,
                               int x2, int y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        double angle = Math.atan2(dy, dx);

        int arrowLength = 10;
        int arrowWidth = 6;

        int xArrow = (int) (x2 - arrowLength * Math.cos(angle));
        int yArrow = (int) (y2 - arrowLength * Math.sin(angle));

        Polygon flecha = new Polygon();
        flecha.addPoint(x2, y2);
        flecha.addPoint(
                (int)(xArrow - arrowWidth * Math.sin(angle)),
                (int)(yArrow + arrowWidth * Math.cos(angle))
        );
        flecha.addPoint(
                (int)(xArrow + arrowWidth * Math.sin(angle)),
                (int)(yArrow - arrowWidth * Math.cos(angle))
        );

        g2.fillPolygon(flecha);
    }

    /**
     * Convierte coordenada X del mouse
     * a coordenada real del mapa.
     */
    public int convertirX(int mouseX) {
        return (int) ((mouseX - offsetX) / escala);
    }

    /**
     * Convierte coordenada Y del mouse
     * a coordenada real del mapa.
     */
    public int convertirY(int mouseY) {
        return (int) ((mouseY - offsetY) / escala);
    }

    /**
     * Establece un nodo como seleccionado visualmente.
     */
    public void setNodoSeleccionado(Nodo n) {
        this.nodoSeleccionado = n;
        repaint();
    }
}
