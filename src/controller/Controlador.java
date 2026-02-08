package controller;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.*;
import view.*;

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

    private List<Long> tiemposBFS = new ArrayList<>();
    private List<Long> tiemposDFS = new ArrayList<>();

    public Controlador(Grafo modelo, VentanaPrincipal vista) {
        this.modelo = modelo;
        this.vista = vista;

        init();
        cargarDatos();
        ajustarContador();

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
            actualizarVista(null);
            vista.setInfo("Sistema restaurado al último guardado.");
        });


        vista.getBtnGuardar().addActionListener(e -> {
            modelo.guardarGrafo(ARCHIVO);
            vista.setInfo("Progreso guardado.");
        });

        vista.getBtnTiempos().addActionListener(e -> {
            new VentanaTiempos(tiemposBFS, tiemposDFS).setVisible(true);
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

    private void ajustarContador() {

        int max = 1000;

        for (String id : modelo.getNodos().keySet()) {

            if (id.startsWith("N_User_")) {

                try {
                    int numero = Integer.parseInt(id.replace("N_User_", ""));
                    if (numero >= max) {
                        max = numero + 1;
                    }
                } catch (Exception ignored) {}
            }
        }

        contador = max;
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
                    actualizarVista(null);
                }
                break;

            case "FIN":
                if (nodoCercano != null) {
                    fin = nodoCercano;
                    actualizarVista(null);
                }
                break;

            case "CREAR":
                String nuevoId = "N_User_" + contador++;
                modelo.agregarNodo(new Nodo(nuevoId, x, y, false));
                actualizarVista(null);
                break;

            case "BORRAR":
                if (nodoCercano != null) {
                    modelo.eliminarNodo(nodoCercano.getId());
                    if (inicio == nodoCercano) inicio = null;
                    if (fin == nodoCercano) fin = null;
                    actualizarVista(null);
                }
                break;

            case "UNIR":

                if (nodoCercano == null) return;

                if (nodoTemporal == null) {
                    nodoTemporal = nodoCercano;
                    vista.getPanelMapa().setNodoSeleccionado(nodoTemporal);
                    vista.setInfo("Seleccione el segundo nodo.");
                    return;
                }

                if (nodoTemporal == nodoCercano) return;

                int dx = nodoCercano.getX() - nodoTemporal.getX();
                int dy = nodoCercano.getY() - nodoTemporal.getY();

                boolean esHorizontal = Math.abs(dy) < 20;
                boolean esVertical   = Math.abs(dx) < 20;

                if (!(esHorizontal || esVertical)) {

                    JOptionPane.showMessageDialog(
                            vista,
                            "Solo se pueden conectar nodos vecinos (arriba, abajo, izquierda o derecha).",
                            "Conexión inválida",
                            JOptionPane.ERROR_MESSAGE
                    );

                    nodoTemporal = null;
                    vista.getPanelMapa().setNodoSeleccionado(null);
                    return;
                }

                // 🔥 VERIFICAR QUE NO HAYA NODOS ENTRE MEDIO
                for (Nodo n : modelo.getNodos().values()) {

                    if (n == nodoTemporal || n == nodoCercano) continue;

                    if (esHorizontal) {

                        if (Math.abs(n.getY() - nodoTemporal.getY()) < 20) {

                            if ((n.getX() > nodoTemporal.getX() && n.getX() < nodoCercano.getX()) ||
                                (n.getX() < nodoTemporal.getX() && n.getX() > nodoCercano.getX())) {

                                JOptionPane.showMessageDialog(
                                        vista,
                                        "Existe un nodo intermedio. Solo puedes unir vecinos directos.",
                                        "Conexión inválida",
                                        JOptionPane.ERROR_MESSAGE
                                );

                                nodoTemporal = null;
                                vista.getPanelMapa().setNodoSeleccionado(null);
                                return;
                            }
                        }

                    } else if (esVertical) {

                        if (Math.abs(n.getX() - nodoTemporal.getX()) < 20) {

                            if ((n.getY() > nodoTemporal.getY() && n.getY() < nodoCercano.getY()) ||
                                (n.getY() < nodoTemporal.getY() && n.getY() > nodoCercano.getY())) {

                                JOptionPane.showMessageDialog(
                                        vista,
                                        "Existe un nodo intermedio. Solo puedes unir vecinos directos.",
                                        "Conexión inválida",
                                        JOptionPane.ERROR_MESSAGE
                                );

                                nodoTemporal = null;
                                vista.getPanelMapa().setNodoSeleccionado(null);
                                return;
                            }
                        }
                    }
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
                    modelo.agregarArista(
                            nodoTemporal.getId(),
                            nodoCercano.getId(),
                            true,
                            true
                    );
                } else if (opcion == 1) {
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
            if (n.getPoint().distance(x, y) < 25) return n;
        }
        return null;
    }

    private void ejecutar(String tipo) {

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione inicio y fin.");
            return;
        }

        ResultadoBusqueda res = tipo.equals("BFS")
                ? modelo.bfsCompleto(inicio.getId(), fin.getId())
                : modelo.dfsCompleto(inicio.getId(), fin.getId());

        if (res == null) {
            vista.setInfo("No existe ruta.");
            return;
        }

        if (tipo.equals("BFS")) {
            tiemposBFS.add(res.tiempo);
        } else {
            tiemposDFS.add(res.tiempo);
        }
        exportarCSV();

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

        if (timerAnimacion != null && timerAnimacion.isRunning())
            timerAnimacion.stop();

        nodosAnimados = new ArrayList<>();
        indiceAnimacion = 0;

        timerAnimacion = new Timer(150, e -> {

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

                ((Timer) e.getSource()).stop();

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

    private void exportarCSV() {

        try (java.io.PrintWriter pw = new java.io.PrintWriter("tiempos.csv")) {

            pw.println("Ejecucion,BFS_ms,DFS_ms");

            int total = Math.max(tiemposBFS.size(), tiemposDFS.size());

            for (int i = 0; i < total; i++) {

                double bfs = i < tiemposBFS.size()
                        ? tiemposBFS.get(i) / 1_000_000.0
                        : 0;

                double dfs = i < tiemposDFS.size()
                        ? tiemposDFS.get(i) / 1_000_000.0
                        : 0;

                pw.println((i + 1) + "," + bfs + "," + dfs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarVista(List<Nodo> ruta) {
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
