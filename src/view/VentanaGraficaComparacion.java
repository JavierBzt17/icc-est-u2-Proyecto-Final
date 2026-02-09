package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Clase VentanaGraficaComparacion
 *
 * Ventana que muestra una comparación visual
 * entre los tiempos de ejecución de BFS y DFS.
 *
 * Utiliza un gráfico de barras donde:
 * - Azul representa BFS
 * - Rojo representa DFS
 *
 * Cada par de barras representa una ejecución.
 */
public class VentanaGraficaComparacion extends JFrame {

    /**
     * Constructor de la ventana.
     *
     * @param bfs Lista de tiempos de ejecución de BFS (en nanosegundos)
     * @param dfs Lista de tiempos de ejecución de DFS (en nanosegundos)
     */
    public VentanaGraficaComparacion(List<Long> bfs, List<Long> dfs) {

        setTitle("Comparación Visual BFS vs DFS");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Agrega el panel que dibuja la gráfica
        add(new PanelGrafica(bfs, dfs));
    }

    /**
     * Clase interna que se encarga de dibujar la gráfica.
     */
    static class PanelGrafica extends JPanel {

        // Lista de tiempos BFS
        private List<Long> bfs;

        // Lista de tiempos DFS
        private List<Long> dfs;

        /**
         * Constructor del panel gráfico.
         */
        public PanelGrafica(List<Long> bfs, List<Long> dfs) {
            this.bfs = bfs;
            this.dfs = dfs;
        }

        /**
         * Dibuja la gráfica de barras.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int margin = 50;

            // Dibuja ejes X e Y
            g2.drawLine(margin, height - margin, width - margin, height - margin);
            g2.drawLine(margin, margin, margin, height - margin);

            int total = Math.min(bfs.size(), dfs.size());
            if (total == 0) return;

            // Obtiene el valor máximo para escalar la gráfica
            long max = 0;
            for (int i = 0; i < total; i++) {
                max = Math.max(max, bfs.get(i));
                max = Math.max(max, dfs.get(i));
            }

            if (max == 0) return;

            int graphWidth = width - 2 * margin;
            int graphHeight = height - 2 * margin;

            int barWidth = graphWidth / (total * 2);

            /**
             * Dibuja las barras
             */
            for (int i = 0; i < total; i++) {

                // Conversión a milisegundos (solo informativo)
                double bfsMs = bfs.get(i) / 1_000_000.0;
                double dfsMs = dfs.get(i) / 1_000_000.0;

                // Altura proporcional respecto al valor máximo
                int bfsHeight = (int) ((bfs.get(i) / (double) max) * graphHeight);
                int dfsHeight = (int) ((dfs.get(i) / (double) max) * graphHeight);

                int xBase = margin + i * barWidth * 2;

                // Barra BFS (Azul)
                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(xBase,
                        height - margin - bfsHeight,
                        barWidth,
                        bfsHeight);

                // Barra DFS (Rojo)
                g2.setColor(new Color(255, 99, 71));
                g2.fillRect(xBase + barWidth,
                        height - margin - dfsHeight,
                        barWidth,
                        dfsHeight);
            }

            /**
             * Leyenda de colores
             */
            g2.setColor(new Color(100, 149, 237));
            g2.fillRect(width - 180, 20, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawString("BFS", width - 160, 32);

            g2.setColor(new Color(255, 99, 71));
            g2.fillRect(width - 120, 20, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawString("DFS", width - 100, 32);
        }
    }
}
