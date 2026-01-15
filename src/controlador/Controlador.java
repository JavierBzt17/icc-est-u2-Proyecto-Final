package controlador;

import java.awt.event.*;
import java.util.*;
import javax.swing.JOptionPane;
import modelo.*;
import vista.VentanaPrincipal;

public class Controlador {
    private Grafo modelo;
    private VentanaPrincipal vista;
    private int modo = 0; 
    private int contador = 1; 
    private Nodo seleccionado = null; 
    private Nodo inicio = null, fin = null;

    public Controlador(Grafo m, VentanaPrincipal v) {
        this.modelo = m;
        this.vista = v;
        init();
    }

    private void init() {
        vista.getBtnNodo().addActionListener(e -> { modo = 1; limpiarSeleccion(); mensaje("Modo: Click para crear nodos"); });
        vista.getBtnArista().addActionListener(e -> { modo = 2; limpiarSeleccion(); mensaje("Modo: UNIÓN RÁPIDA (Click A -> Click B)"); });
        vista.getBtnAutoUnir().addActionListener(e -> { autoUnirSecuencial(); mensaje("Nodos unidos en secuencia."); });
        
        vista.getBtnBorrarNodo().addActionListener(e -> { 
            modo = 5; 
            limpiarSeleccion(); 
            mensaje("Modo: ELIMINAR (Haz click en un nodo para borrarlo)"); 
        });
        vista.getBtnInicio().addActionListener(e -> { modo = 3; mensaje("Modo: Selecciona el nodo de INICIO"); });
        vista.getBtnFin().addActionListener(e -> { modo = 4; mensaje("Modo: Selecciona el nodo de FIN"); });
        
        vista.getBtnBFS().addActionListener(e -> ejecutarAlgoritmo("BFS"));
        vista.getBtnDFS().addActionListener(e -> ejecutarAlgoritmo("DFS"));
        
        vista.getBtnLimpiar().addActionListener(e -> {
            modelo.reiniciar(); 
            inicio = null; fin = null; seleccionado = null; contador = 1; modo = 0;
            actualizarVista(null); 
            mensaje("Todo borrado.");
        });

        vista.getPanelMapa().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (modo == 1) crearNodo(e.getX(), e.getY());
                else if (modo == 2) conectarNodos(e.getX(), e.getY());
                else if (modo == 3) seleccionarPunto(e.getX(), e.getY(), true);
                else if (modo == 4) seleccionarPunto(e.getX(), e.getY(), false);
                else if (modo == 5) borrarNodo(e.getX(), e.getY()); // Nuevo evento
            }
        });
        
        vista.setVisible(true);
    }
    
    // --- LÓGICA PARA BORRAR ---
    private void borrarNodo(int x, int y) {
        Nodo n = buscarNodoCercano(x, y);
        if (n != null) {
            if (inicio == n) inicio = null;
            if (fin == n) fin = null;
            if (seleccionado == n) seleccionado = null;

            modelo.eliminarNodo(n.getId());
            actualizarVista(null);
            mensaje("Nodo " + n.getId() + " eliminado.");
        }
    }


    private void autoUnirSecuencial() {
        List<Nodo> listaNodos = new ArrayList<>(modelo.getNodos().values());
        if (listaNodos.size() < 2) return;

        listaNodos.sort((n1, n2) -> {
            // Extraer número del ID "N12" -> 12
            try {
                int id1 = Integer.parseInt(n1.getId().substring(1));
                int id2 = Integer.parseInt(n2.getId().substring(1));
                return Integer.compare(id1, id2);
            } catch (Exception ex) { return 0; }
        });

        for (int i = 0; i < listaNodos.size() - 1; i++) {
            Nodo a = listaNodos.get(i);
            Nodo b = listaNodos.get(i + 1);
            modelo.agregarArista(a.getId(), b.getId());
        }
        actualizarVista(null);
    }

    private void mensaje(String msg) { vista.setTitle("Sistema de Rutas - " + msg); }
    private void limpiarSeleccion() { seleccionado = null; }

    private void crearNodo(int x, int y) {
        modelo.agregarNodo(new Nodo("N" + contador++, x, y));
        actualizarVista(null);
    }

    private void conectarNodos(int x, int y) {
        Nodo clickeado = buscarNodoCercano(x, y);
        if (clickeado != null) {
            if (seleccionado == null) {
                seleccionado = clickeado;
                mensaje("Seleccionado " + clickeado.getId() + "...");
            } else {
                if (seleccionado != clickeado) {
                    modelo.agregarArista(seleccionado.getId(), clickeado.getId());
                    String anterior = seleccionado.getId();
                    seleccionado = null; 
                    actualizarVista(null);
                    mensaje("Unido " + anterior + " con " + clickeado.getId());
                }
            }
        }
    }

    private void seleccionarPunto(int x, int y, boolean esInicio) {
        Nodo n = buscarNodoCercano(x, y);
        if (n != null) {
            if (esInicio) { inicio = n; mensaje("Inicio: " + n.getId()); } 
            else { fin = n; mensaje("Fin: " + n.getId()); }
            actualizarVista(null);
            modo = 0; 
        }
    }

    private void ejecutarAlgoritmo(String tipo) {
        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(vista, "Falta Inicio o Fin.");
            return;
        }
        List<Nodo> ruta = tipo.equals("BFS") ? modelo.bfs(inicio.getId(), fin.getId()) : modelo.dfs(inicio.getId(), fin.getId());
        if (ruta == null || ruta.isEmpty()) JOptionPane.showMessageDialog(vista, "No hay camino.");
        else actualizarVista(ruta);
    }

    private Nodo buscarNodoCercano(int x, int y) {
        for (Nodo n : modelo.getNodos().values()) {
            if (n.getPoint().distance(x, y) < 20) return n; 
        }
        return null;
    }

    private void actualizarVista(List<Nodo> ruta) {
        vista.getPanelMapa().actualizar(modelo.getNodos(), modelo.getAdyacencias(), ruta, inicio, fin);
    }
}