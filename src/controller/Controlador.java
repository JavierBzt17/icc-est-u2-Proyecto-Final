package controller;

import java.awt.event.*;
import java.io.File;
import javax.swing.JOptionPane;
import model.*;
import view.VentanaPrincipal;

public class Controlador {

    private Grafo modelo;
    private VentanaPrincipal vista;

    private static final String ARCHIVO = "grafo.txt";

    private Nodo inicio = null;
    private Nodo fin = null;
    private Nodo nodoTemporal = null;

    private String modo = "";

    public Controlador(Grafo modelo, VentanaPrincipal vista) {
        this.modelo = modelo;
        this.vista = vista;

        init();
        cargarDatos();

        vista.setVisible(true);
    }

    private void init() {

        vista.getBtnInicio().addActionListener(e -> {
            modo = "INICIO";
            vista.setInfo("Seleccione nodo de inicio.");
        });

        vista.getBtnFin().addActionListener(e -> {
            modo = "FIN";
            vista.setInfo("Seleccione nodo destino.");
        });

        vista.getBtnBFS().addActionListener(e -> ejecutar("BFS"));
        vista.getBtnDFS().addActionListener(e -> ejecutar("DFS"));

        vista.getBtnNodo().addActionListener(e -> {
            modo = "CREAR";
            vista.setInfo("Click en el mapa para crear nodo.");
        });

        vista.getBtnBorrarNodo().addActionListener(e -> {
            modo = "BORRAR";
            vista.setInfo("Click en un nodo para eliminar.");
        });

        vista.getBtnArista().addActionListener(e -> {
            modo = "UNIR";
            nodoTemporal = null;
            vista.setInfo("Seleccione dos nodos para unir.");
        });

        vista.getBtnBorrarArista().addActionListener(e -> {
            modo = "ROMPER";
            nodoTemporal = null;
            vista.setInfo("Seleccione dos nodos para romper unión.");
        });

        vista.getBtnLimpiar().addActionListener(e -> {
            inicio = null;
            fin = null;
            actualizarVista(null);
            vista.setInfo("Reset realizado.");
        });

        vista.getBtnGuardar().addActionListener(e -> {
            modelo.guardarGrafo(ARCHIVO);
            vista.setInfo("Progreso guardado.");
        });

        vista.getPanelMapa().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int realX = vista.getPanelMapa().convertirX(e.getX());
                int realY = vista.getPanelMapa().convertirY(e.getY());

                manejarClick(realX, realY);
            }
        });
    }

    private void cargarDatos() {
        File f = new File(ARCHIVO);

        if (f.exists()) {
            modelo.cargarGrafo(ARCHIVO);
            actualizarVista(null);
            vista.setInfo("Mapa cargado correctamente.");
        } else {
            vista.setInfo("No se encontró grafo.txt");
        }
    }

    private void manejarClick(int x, int y) {

        Nodo nodoCercano = buscarNodo(x, y);

        switch (modo) {

            case "INICIO":
                if (nodoCercano != null) {
                    inicio = nodoCercano;
                    vista.setInfo("Inicio: " + nodoCercano.getId());
                    actualizarVista(null);
                }
                break;

            case "FIN":
                if (nodoCercano != null) {
                    fin = nodoCercano;
                    vista.setInfo("Fin: " + nodoCercano.getId());
                    actualizarVista(null);
                }
                break;

            case "CREAR":
                String nuevoId = "N" + (modelo.getNodos().size() + 1);
                modelo.agregarNodo(new Nodo(nuevoId, x, y, false));
                actualizarVista(null);
                vista.setInfo("Nodo creado: " + nuevoId);
                break;

            case "BORRAR":
                if (nodoCercano != null) {
                    modelo.eliminarNodo(nodoCercano.getId());
                    actualizarVista(null);
                    vista.setInfo("Nodo eliminado.");
                }
                break;

            case "UNIR":
                if (nodoCercano != null) {
                    if (nodoTemporal == null) {
                        nodoTemporal = nodoCercano;
                    } else {
                        modelo.agregarArista(
                                nodoTemporal.getId(),
                                nodoCercano.getId(),
                                true
                        );
                        nodoTemporal = null;
                        actualizarVista(null);
                        vista.setInfo("Nodos unidos.");
                    }
                }
                break;

            case "ROMPER":
                if (nodoCercano != null) {
                    if (nodoTemporal == null) {
                        nodoTemporal = nodoCercano;
                    } else {
                        modelo.eliminarArista(
                                nodoTemporal.getId(),
                                nodoCercano.getId()
                        );
                        nodoTemporal = null;
                        actualizarVista(null);
                        vista.setInfo("Unión eliminada.");
                    }
                }
                break;
        }
    }

    private Nodo buscarNodo(int x, int y) {
        for (Nodo n : modelo.getNodos().values()) {
            if (n.getPoint().distance(x, y) < 15) {
                return n;
            }
        }
        return null;
    }

    private void ejecutar(String tipo) {

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione inicio y fin.");
            return;
        }

        ResultadoBusqueda res = tipo.equals("BFS")
                ? modelo.bfsCompleto(inicio.getId(), fin.getId())
                : modelo.dfsCompleto(inicio.getId(), fin.getId());

        if (res == null) {
            vista.setInfo("No existe ruta.");
            return;
        }

        actualizarVista(res.ruta);
        vista.setInfo("Ruta encontrada.");
    }

    private void actualizarVista(java.util.List<Nodo> ruta) {

        vista.getPanelMapa().actualizar(
                modelo.getNodos(),
                modelo.getAristasVisibles(),
                ruta,
                null,
                null,
                inicio,
                fin
        );
    }
}
