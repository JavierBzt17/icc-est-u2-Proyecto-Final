package view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;

/**
 * Clase VentanaPrincipal
 *
 * Representa la ventana principal del sistema.
 *
 * Contiene:
 * - El panel del mapa (MapaPanel)
 * - Una barra de herramientas con botones de edición y acciones
 * - Controles para ejecutar BFS y DFS
 * - Selector de modo (instantáneo o animado)
 * - Etiqueta informativa inferior
 *
 * Esta clase solo construye la interfaz gráfica.
 * La lógica se maneja en el Controlador.
 */
public class VentanaPrincipal extends JFrame {

    // Panel central donde se dibuja el mapa y los nodos
    private MapaPanel panelMapa;

    // Botones para selección de inicio y fin
    private JButton btnInicio, btnFin, btnBFS, btnDFS;

    // Botones de edición del grafo
    private JButton btnNodo, btnArista, btnBorrarNodo, btnBorrarArista;

    // Botones de acciones generales
    private JButton btnLimpiar, btnGuardar, btnTiempos;

    // Selector de modo de visualización
    private JComboBox<String> comboModo;

    // Etiqueta informativa inferior
    private JLabel lblInfo;

    /**
     * Constructor de la ventana principal.
     * Configura todos los componentes visuales.
     */
    public VentanaPrincipal() {

        setTitle("MAPA - Centro de Cuenca");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel central del mapa
        panelMapa = new MapaPanel();
        add(panelMapa, BorderLayout.CENTER);

        // Barra de herramientas superior
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Botones de edición
        btnNodo = new JButton("Crear Nodo");
        btnBorrarNodo = new JButton("Eliminar Nodo");
        btnArista = new JButton("Crear Conexión");
        btnBorrarArista = new JButton("Eliminar Conexión");

        // Botones de acciones
        btnLimpiar = new JButton("Resetear");
        btnGuardar = new JButton("Guardar");
        btnTiempos = new JButton("Ver Tiempos");

        // Colores para distinguir acciones importantes
        btnLimpiar.setBackground(new Color(255, 200, 200));
        btnGuardar.setBackground(new Color(200, 255, 200));
        btnTiempos.setBackground(new Color(200, 220, 255));

        // Botones para definir inicio y fin
        btnInicio = new JButton("INICIO");
        btnFin = new JButton("FIN");

        // Opciones de modo de ejecución
        String[] modos = {
                "Modo: RUTA INSTANTÁNEA",
                "Modo: ANIMACIÓN EXPLORACIÓN"
        };

        comboModo = new JComboBox<>(modos);

        // Botones para ejecutar algoritmos
        btnBFS = new JButton("BFS");
        btnDFS = new JButton("DFS");

        /**
         * Organización de la barra de herramientas
         */
        toolbar.add(new JLabel(" EDITAR: "));
        toolbar.add(btnNodo);
        toolbar.add(btnBorrarNodo);
        toolbar.add(btnArista);
        toolbar.add(btnBorrarArista);
        toolbar.addSeparator();

        toolbar.add(new JLabel(" ACCIONES: "));
        toolbar.add(btnLimpiar);
        toolbar.add(btnGuardar);
        toolbar.add(btnTiempos);
        toolbar.addSeparator();

        toolbar.add(new JLabel(" RUTA: "));
        toolbar.add(btnInicio);
        toolbar.add(btnFin);
        toolbar.add(comboModo);
        toolbar.add(btnBFS);
        toolbar.add(btnDFS);

        add(toolbar, BorderLayout.NORTH);

        // Panel inferior de información
        JPanel panelInfo = new JPanel();
        lblInfo = new JLabel("Mapa cargado.");
        panelInfo.add(lblInfo);
        add(panelInfo, BorderLayout.SOUTH);
    }

    /**
     * Devuelve el panel del mapa.
     */
    public MapaPanel getPanelMapa() { return panelMapa; }

    /**
     * Getters de botones y componentes
     * Se utilizan en el Controlador para asignar eventos.
     */
    public JButton getBtnInicio() { return btnInicio; }
    public JButton getBtnFin() { return btnFin; }
    public JButton getBtnBFS() { return btnBFS; }
    public JButton getBtnDFS() { return btnDFS; }

    public JComboBox<String> getComboModo() { return comboModo; }

    public JButton getBtnNodo() { return btnNodo; }
    public JButton getBtnArista() { return btnArista; }
    public JButton getBtnBorrarNodo() { return btnBorrarNodo; }
    public JButton getBtnBorrarArista() { return btnBorrarArista; }

    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnTiempos() { return btnTiempos; }

    /**
     * Actualiza el mensaje informativo inferior.
     */
    public void setInfo(String texto) {
        lblInfo.setText(texto);
    }
}
