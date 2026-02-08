package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;

public class VentanaGraficaComparacion extends JFrame {

    public VentanaGraficaComparacion(List<Long> bfs, List<Long> dfs) {

        setTitle("Comparación Visual BFS vs DFS");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(new PanelGrafica(bfs, dfs));
    }

    static class PanelGrafica extends JPanel {

        private List<Long> bfs;
        private List<Long> dfs;

        public PanelGrafica(List<Long> bfs, List<Long> dfs) {
            this.bfs = bfs;
            this.dfs = dfs;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int margin = 50;

            g2.drawLine(margin, height - margin, width - margin, height - margin);
            g2.drawLine(margin, margin, margin, height - margin);

            int total = Math.min(bfs.size(), dfs.size());
            if (total == 0) return;

            long max = 0;
            for (int i = 0; i < total; i++) {
                max = Math.max(max, bfs.get(i));
                max = Math.max(max, dfs.get(i));
            }

            if (max == 0) return;

            int graphWidth = width - 2 * margin;
            int graphHeight = height - 2 * margin;

            int barWidth = graphWidth / (total * 2);

            for (int i = 0; i < total; i++) {

                double bfsMs = bfs.get(i) / 1_000_000.0;
                double dfsMs = dfs.get(i) / 1_000_000.0;

                int bfsHeight = (int) ((bfs.get(i) / (double) max) * graphHeight);
                int dfsHeight = (int) ((dfs.get(i) / (double) max) * graphHeight);

                int xBase = margin + i * barWidth * 2;

                g2.setColor(new Color(100, 149, 237));
                g2.fillRect(xBase,
                        height - margin - bfsHeight,
                        barWidth,
                        bfsHeight);

                g2.setColor(new Color(255, 99, 71));
                g2.fillRect(xBase + barWidth,
                        height - margin - dfsHeight,
                        barWidth,
                        dfsHeight);
            }

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
