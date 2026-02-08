package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaTiempos extends JFrame {

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    private List<Long> tiemposBFS;
    private List<Long> tiemposDFS;

    public VentanaTiempos(List<Long> tiemposBFS, List<Long> tiemposDFS) {

        this.tiemposBFS = tiemposBFS;
        this.tiemposDFS = tiemposDFS;

        setTitle("Comparación de Tiempos BFS vs DFS");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(
                new Object[]{"Ejecución", "BFS (ms)", "DFS (ms)"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowSelectionAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JButton btnGrafica = new JButton("Ver Comparación Gráfica");
        btnGrafica.addActionListener(e ->
                new VentanaGraficaComparacion(tiemposBFS, tiemposDFS).setVisible(true)
        );

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnGrafica);

        add(panelBoton, BorderLayout.SOUTH);

        agregarDatos(tiemposBFS, tiemposDFS);
    }

    private void agregarDatos(List<Long> bfs, List<Long> dfs) {

        int total = Math.min(bfs.size(), dfs.size());

        for (int i = 0; i < total; i++) {

            modeloTabla.addRow(new Object[]{
                    i + 1,
                    bfs.get(i) / 1_000_000.0,
                    dfs.get(i) / 1_000_000.0
            });
        }
    }
}
