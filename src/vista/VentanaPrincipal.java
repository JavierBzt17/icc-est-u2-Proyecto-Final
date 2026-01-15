package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private PanelMapa panelMapa;
    private JButton btnNodo, btnArista, btnAutoUnir, btnBorrarNodo, btnInicio, btnFin, btnBFS, btnDFS, btnLimpiar;

    public VentanaPrincipal() {
        setTitle("Sistema de Rutas - Proyecto Final");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panelMapa = new PanelMapa();
        add(panelMapa, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        
        btnNodo = new JButton("1. Crear");
        btnArista = new JButton("2. Unir Manual");
        btnAutoUnir = new JButton("⚡ Auto Unir");
        
        // Botón rojo para borrar
        btnBorrarNodo = new JButton("❌ Borrar Nodo");
        btnBorrarNodo.setForeground(Color.RED);

        btnInicio = new JButton("3. Inicio");
        btnFin = new JButton("4. Fin");
        
        btnBFS = new JButton("BFS");
        btnDFS = new JButton("DFS");
        btnLimpiar = new JButton("Reset Total");

        toolbar.add(btnNodo);
        toolbar.add(btnArista);
        toolbar.add(btnAutoUnir);
        toolbar.add(btnBorrarNodo);
        toolbar.addSeparator();
        toolbar.add(btnInicio);
        toolbar.add(btnFin);
        toolbar.addSeparator();
        toolbar.add(btnBFS);
        toolbar.add(btnDFS);
        toolbar.add(btnLimpiar);
        
        add(toolbar, BorderLayout.NORTH);
    }

    public PanelMapa getPanelMapa() { return panelMapa; }
    public JButton getBtnNodo() { return btnNodo; }
    public JButton getBtnArista() { return btnArista; }
    public JButton getBtnAutoUnir() { return btnAutoUnir; }
    public JButton getBtnBorrarNodo() { return btnBorrarNodo; } 
    public JButton getBtnInicio() { return btnInicio; }
    public JButton getBtnFin() { return btnFin; }
    public JButton getBtnBFS() { return btnBFS; }
    public JButton getBtnDFS() { return btnDFS; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
}