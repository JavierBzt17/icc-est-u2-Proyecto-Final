package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private PanelMapa panelMapa;
    private JButton btnInicio, btnFin, btnBFS, btnDFS;
    private JComboBox<String> comboModo;
    private JLabel lblInfo;
    private JButton btnNodo, btnArista, btnBorrarNodo, btnBorrarArista, btnLimpiar, btnGuardar;

    public VentanaPrincipal() {
        setTitle("Sistema de Rutas - Proyecto Final");
        // Iniciamos maximizado
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelMapa = new PanelMapa();
        add(panelMapa, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        btnNodo = new JButton("Crear Nodo");
        btnBorrarNodo = new JButton("Borrar Nodo");
        btnArista = new JButton("Unir Nodos");
        btnBorrarArista = new JButton("Romper Unión");
        btnLimpiar = new JButton("Resetear");
        btnGuardar = new JButton("Guardar");
        
        btnLimpiar.setBackground(new Color(255, 200, 200));
        btnGuardar.setBackground(new Color(200, 255, 200));

        btnInicio = new JButton("Sel. INICIO");
        btnFin = new JButton("Sel. FIN");

        String[] modos = {"Modo: RUTA INSTANTÁNEA", "Modo: ANIMACIÓN EXPLORACIÓN"};
        comboModo = new JComboBox<>(modos);

        btnBFS = new JButton("BFS");
        btnDFS = new JButton("DFS");

        toolbar.add(new JLabel(" EDITAR: "));
        toolbar.add(btnNodo);
        toolbar.add(btnBorrarNodo);
        toolbar.add(btnArista);
        toolbar.add(btnBorrarArista);
        toolbar.addSeparator();
        
        toolbar.add(new JLabel(" ACCIONES: "));
        toolbar.add(btnLimpiar);
        toolbar.add(btnGuardar);
        toolbar.addSeparator();

        toolbar.add(new JLabel(" RUTA: "));
        toolbar.add(btnInicio);
        toolbar.add(btnFin);
        toolbar.add(comboModo);
        toolbar.add(btnBFS);
        toolbar.add(btnDFS);

        add(toolbar, BorderLayout.NORTH);

        JPanel panelInfo = new JPanel();
        lblInfo = new JLabel("Mapa cargado.");
        panelInfo.add(lblInfo);
        add(panelInfo, BorderLayout.SOUTH);
    }

    // Getters
    public PanelMapa getPanelMapa() { return panelMapa; }
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
    
    public void setInfo(String texto) { lblInfo.setText(texto); }
}