package controller;

import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import model.*;
import view.VentanaPrincipal;

public class Controlador {

    private Grafo modelo;
    private VentanaPrincipal vista;

    private static final String ARCHIVO = "grafo.txt";

    private Nodo inicio = null;
    private Nodo fin = null;
    private Nodo nodoTemporal = null;

    private Timer timerAnimacion;
    private List<Nodo> nodosAnimados;
    private int indiceAnimacion = 0;


    private int contador = 1000;

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
            nodoTemporal = null;

            modelo.reiniciar();    
            cargarDatos();       

            vista.getPanelMapa().setNodoSeleccionado(null);
            vista.setInfo("Sistema restaurado al último guardado.");
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
                String nuevoId = "N_User_" + contador++;
                modelo.agregarNodo(new Nodo(nuevoId, x, y, false));
                actualizarVista(null);
                vista.setInfo("Nodo creado: " + nuevoId);
                break;

            case "BORRAR":

                if (nodoCercano != null) {

                    modelo.eliminarNodo(nodoCercano.getId());

                    if (inicio == nodoCercano) inicio = null;
                    if (fin == nodoCercano) fin = null;

                    actualizarVista(null);
                    vista.setInfo("Nodo eliminado.");
                }

                break;


            case "UNIR":

                if (nodoCercano != null) {

                    if (nodoTemporal == null) {
                        nodoTemporal = nodoCercano;
                        vista.getPanelMapa().setNodoSeleccionado(nodoTemporal);
                        vista.setInfo("Seleccione el segundo nodo.");
                        return;
                    }

                    if (nodoTemporal == nodoCercano) {
                        return;
                    }

                    int dx = Math.abs(nodoTemporal.getX() - nodoCercano.getX());
                    int dy = Math.abs(nodoTemporal.getY() - nodoCercano.getY());

                    if (dx > 20 && dy > 20) {

                        JOptionPane.showMessageDialog(
                                vista,
                                "Solo se puede conectar de manera horizontal o vertical.",
                                "Conexión inválida",
                                JOptionPane.ERROR_MESSAGE
                        );

                        nodoTemporal = null;
                        vista.getPanelMapa().setNodoSeleccionado(null);
                        return;
                    }

                    int opcion = JOptionPane.showOptionDialog(
                            vista,
                            "Seleccione tipo de conexión:",
                            "Tipo de Arista",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Bidireccional", "Unidireccional"},
                            "Bidireccional"
                    );

                    if (opcion == 0) {
                        // Bidireccional
                        modelo.agregarArista(
                                nodoTemporal.getId(),
                                nodoCercano.getId(),
                                true,
                                true
                        );
                    }
                    else if (opcion == 1) {
                        // Unidireccional
                        modelo.agregarArista(
                                nodoTemporal.getId(),
                                nodoCercano.getId(),
                                true,
                                false
                        );
                    }

                    nodoTemporal = null;
                    vista.getPanelMapa().setNodoSeleccionado(null);
                    actualizarVista(null);
                    vista.setInfo("Conexión creada.");
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
            if (n.getPoint().distance(x, y) < 25) {
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

        boolean modoAnimado =
                vista.getComboModo().getSelectedIndex() == 1;

        if (!modoAnimado) {

            vista.getPanelMapa().actualizar(
                    modelo.getNodos(),
                    modelo.getAristasVisibles(),
                    res.ruta,
                    null,
                    null,
                    inicio,
                    fin
            );

            vista.setInfo("Ruta encontrada.");
            return;
        }

        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.stop();
        }

        nodosAnimados = new java.util.ArrayList<>();
        indiceAnimacion = 0;

        timerAnimacion = new javax.swing.Timer(150, e -> {

            if (indiceAnimacion < res.visitados.size()) {

                nodosAnimados.add(res.visitados.get(indiceAnimacion));

                vista.getPanelMapa().actualizar(
                        modelo.getNodos(),
                        modelo.getAristasVisibles(),
                        null,
                        nodosAnimados,
                        null,
                        inicio,
                        fin
                );

                indiceAnimacion++;

            } else {

                ((javax.swing.Timer) e.getSource()).stop();

                vista.getPanelMapa().actualizar(
                        modelo.getNodos(),
                        modelo.getAristasVisibles(),
                        res.ruta,
                        nodosAnimados,
                        null,
                        inicio,
                        fin
                );

                vista.setInfo("Ruta encontrada.");
            }
        });

        timerAnimacion.start();
    }

    private void iniciarAnimacion(ResultadoBusqueda res) {

        java.util.List<Nodo> visitados = res.visitados;
        java.util.List<Nodo> rutaFinal = res.ruta;

        javax.swing.Timer timer = new javax.swing.Timer(200, null);

        final int[] indice = {0};

        timer.addActionListener(e -> {

            if (indice[0] < visitados.size()) {

                java.util.List<Nodo> parcial =
                        visitados.subList(0, indice[0] + 1);

                vista.getPanelMapa().actualizar(
                        modelo.getNodos(),
                        modelo.getAristasVisibles(),
                        null,
                        parcial,
                        null,
                        inicio,
                        fin
                );

                indice[0]++;

            } else {

                timer.stop();

                actualizarVista(rutaFinal);

                vista.setInfo("Ruta encontrada.");
            }
        });

        timer.start();
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