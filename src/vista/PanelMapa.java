package vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;
import modelo.Nodo;

public class PanelMapa extends JPanel {
    private BufferedImage imagenMapa;
    private Map<String, Nodo> nodos;
    private Set<String> aristasVisibles;
    
    private List<Nodo> rutaFinal;
    private List<Nodo> nodosAnimacion;
    private Map<String, String> padresAnimacion; 
    private Nodo nodoInicio, nodoFin;

    private final double BASE_ANCHO = 1200.0;
    private final double BASE_ALTO = 800.0;

    public PanelMapa() {
        try {
            File archivoImagen = new File("imagenes/Mapa.jpg");
            if (archivoImagen.exists()) imagenMapa = ImageIO.read(archivoImagen);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public int getScreenX(int logicalX) {
        return (int) (logicalX * (getWidth() / BASE_ANCHO));
    }

    public int getScreenY(int logicalY) {
        return (int) (logicalY * (getHeight() / BASE_ALTO));
    }
    
    public int getLogicalX(int screenX) {
        return (int) (screenX / (getWidth() / BASE_ANCHO));
    }

    public int getLogicalY(int screenY) {
        return (int) (screenY / (getHeight() / BASE_ALTO));
    }

    public boolean esCalle(int x, int y) {
        if (imagenMapa == null) return false;
        int imgX = (int) (x * (imagenMapa.getWidth() / (double)getWidth()));
        int imgY = (int) (y * (imagenMapa.getHeight() / (double)getHeight()));
        
        if (imgX < 0 || imgX >= imagenMapa.getWidth() || imgY < 0 || imgY >= imagenMapa.getHeight()) return false;
        
        int color = imagenMapa.getRGB(imgX, imgY);
        Color c = new Color(color);
        return (c.getRed() > 180 && c.getGreen() > 180 && c.getBlue() > 180);
    }

    public boolean hayObstaculo(Point p1, Point p2) {
        return false; 
    }
    
    public void actualizar(Map<String, Nodo> ns, Set<String> visibles, 
                           List<Nodo> ruta, List<Nodo> animacion, Map<String, String> padres, Nodo ini, Nodo fin) {
        this.nodos = ns;
        this.aristasVisibles = visibles;
        this.rutaFinal = ruta;
        this.nodosAnimacion = animacion;
        this.padresAnimacion = padres;
        this.nodoInicio = ini;
        this.nodoFin = fin;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (imagenMapa != null) {
            g.drawImage(imagenMapa, 0, 0, getWidth(), getHeight(), this);
        }

        if (nodos != null && aristasVisibles != null) {
            g2.setColor(Color.MAGENTA);
            g2.setStroke(new BasicStroke(3));
            
            for (String clave : aristasVisibles) {
                String[] partes = clave.split("-");
                if (partes.length == 2) {
                    Nodo n1 = nodos.get(partes[0]);
                    Nodo n2 = nodos.get(partes[1]);
                    if (n1 != null && n2 != null) {
                        g2.drawLine(getScreenX(n1.getX()), getScreenY(n1.getY()), 
                                    getScreenX(n2.getX()), getScreenY(n2.getY()));
                    }
                }
            }
        }

        if (nodosAnimacion != null && !nodosAnimacion.isEmpty() && padresAnimacion != null) {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.CYAN); 
            for (Nodo hijo : nodosAnimacion) {
                String idPadre = padresAnimacion.get(hijo.getId());
                if (idPadre != null && nodos.containsKey(idPadre)) {
                    Nodo padre = nodos.get(idPadre);
                    g2.drawLine(getScreenX(padre.getX()), getScreenY(padre.getY()), 
                                getScreenX(hijo.getX()), getScreenY(hijo.getY()));
                }
            }
            for (Nodo n : nodosAnimacion) {
                g2.fillOval(getScreenX(n.getX()) - 5, getScreenY(n.getY()) - 5, 10, 10);
            }
        }

        if (rutaFinal != null && !rutaFinal.isEmpty()) {
            g2.setStroke(new BasicStroke(5)); 
            g2.setColor(new Color(0, 255, 0)); 
            for (int i = 0; i < rutaFinal.size() - 1; i++) {
                Nodo n1 = rutaFinal.get(i);
                Nodo n2 = rutaFinal.get(i+1);
                g2.drawLine(getScreenX(n1.getX()), getScreenY(n1.getY()), 
                            getScreenX(n2.getX()), getScreenY(n2.getY()));
            }
        }

        if (nodos != null) {
            for (Nodo n : nodos.values()) {
                g2.setColor(new Color(255, 140, 0)); 
                if (n == nodoInicio) g2.setColor(Color.BLUE);
                else if (n == nodoFin) g2.setColor(Color.RED);
                
                int radio = (n == nodoInicio || n == nodoFin) ? 18 : 12;
                int sx = getScreenX(n.getX());
                int sy = getScreenY(n.getY());
                
                g2.fillOval(sx - (radio/2), sy - (radio/2), radio, radio);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.drawOval(sx - (radio/2), sy - (radio/2), radio, radio);
            }
        }
    }
    
    public int getAnchoImagen() { return (int)BASE_ANCHO; }
    public int getAltoImagen() { return (int)BASE_ALTO; }
}