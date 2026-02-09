package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Clase VentanaTiempos
 *
 * Muestra una tabla comparativa con los tiempos de ejecución
 * de los algoritmos BFS y DFS.
 *
 * Permite:
 * - Visualizar los tiempos en milisegundos
 * - Abrir una gráfica comparativa (VentanaGraficaComparacion)
 *
 * Esta ventana es informativa y no permite editar datos.
 */
public class VentanaTiempos extends JFrame {

    // Modelo de datos de la tabla
    private DefaultTableModel modeloTabla;

    // Tabla donde se muestran los tiempos
    private JTable tabla;

    // Listas con los tiempos registrados
    private List<Long> tiemposBFS;
    private List<Long> tiemposDFS;

    /**
     * Constructor de la ventana.
     *
     * @param tiemposBFS Lista de tiempos BFS (nanosegundos)
     * @param tiemposDFS Lista de tiempos DFS (nanosegundos)
     */
    public VentanaTiempos(List<Long> tiemposBFS, List<Long> tiemposDFS) {

        this.tiemposBFS = tiemposBFS;
        this.tiemposDFS = tiemposDFS;

        setTitle("Comparación de Tiempos BFS vs DFS");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        /**
         * Configuración del modelo de tabla.
         * Columnas:
         * - Número de ejecución
         * - Tiempo BFS en ms
         * - Tiempo DFS en ms
         */
        modeloTabla = new DefaultTableModel(
                new Object[]{"Ejecución", "BFS (ms)", "DFS (ms)"},
                0
        ) {
            // Hace que las celdas no sean editables
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        // Configuración visual de la tabla
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowSelectionAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        /**
         * Botón que abre la gráfica comparativa
         */
        JButton btnGrafica = new JButton("Ver Comparación Gráfica");
        btnGrafica.addActionListener(e ->
                new VentanaGraficaComparacion(tiemposBFS, tiemposDFS).setVisible(true)
        );

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnGrafica);

        add(panelBoton, BorderLayout.SOUTH);

        // Carga los datos en la tabla
        agregarDatos(tiemposBFS, tiemposDFS);
    }

    /**
     * Agrega los datos de BFS y DFS a la tabla.
     * Convierte los tiempos de nanosegundos a milisegundos.
     */
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
