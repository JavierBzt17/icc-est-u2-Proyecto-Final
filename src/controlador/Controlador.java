package controlador;

import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import modelo.*;
import vista.VentanaPrincipal;

public class Controlador {
    private Grafo modelo;
    private VentanaPrincipal vista;
    
    private static final String ARCHIVO_DATOS = "datos_grafo.txt";

    private final int MODO_NORMAL = 0;
    private final int MODO_AGREGAR_NODO = 1;
    private final int MODO_ELIMINAR_NODO = 2;
    private final int MODO_SELECCIONAR_INICIO = 3;
    private final int MODO_SELECCIONAR_FIN = 4;
    private final int MODO_AGREGAR_ARISTA = 5;
    private final int MODO_ELIMINAR_ARISTA = 6;

    private int modoInteraccion = MODO_NORMAL;
    private int contador = 1; 
    
    private Nodo inicio = null, fin = null;
    private Nodo nodoSeleccionado = null;
    
    private Timer timerAnimacion;
    private List<Nodo> nodosAnimados;
    private Map<String, String> mapaPadresAnimacion; 
    private int indiceAnimacion = 0;

    public Controlador(Grafo m, VentanaPrincipal v) {
        this.modelo = m;
        this.vista = v;
        init();
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            cargarDatosAutomaticamente();
        });
    }

    private void cargarDatosAutomaticamente() {
        File f = new File(ARCHIVO_DATOS);
        if (f.exists()) {
            modelo.cargarGrafo(ARCHIVO_DATOS);
            
            int maxId = 0;
            for(String id : modelo.getNodos().keySet()) {
                try {
                    String num = id.replaceAll("\\D+",""); 
                    if(!num.isEmpty()) {
                        int n = Integer.parseInt(num);
                        if(n > maxId) maxId = n;
                    }
                } catch(Exception e){}
            }
            contador = maxId + 1;
            
            vista.setInfo("Datos cargados correctamente.");
            actualizarVista(null, null, null);
        } else {
            generarMallaCalibrada();
        }
    }

    private void init() {
        vista.getBtnInicio().addActionListener(e -> setModo(MODO_SELECCIONAR_INICIO, "Selecciona PUNTO DE PARTIDA"));
        vista.getBtnFin().addActionListener(e -> setModo(MODO_SELECCIONAR_FIN, "Selecciona DESTINO"));
        vista.getBtnBFS().addActionListener(e -> ejecutarAlgoritmo("BFS"));
        vista.getBtnDFS().addActionListener(e -> ejecutarAlgoritmo("DFS"));

        vista.getBtnNodo().addActionListener(e -> setModo(MODO_AGREGAR_NODO, "Haz click en el mapa para CREAR NODO"));
        vista.getBtnBorrarNodo().addActionListener(e -> setModo(MODO_ELIMINAR_NODO, "Haz click en un nodo para BORRARLO"));
        
        vista.getBtnArista().addActionListener(e -> {
            nodoSeleccionado = null;
            setModo(MODO_AGREGAR_ARISTA, "Selecciona el PRIMER nodo para unir");
        });
        
        vista.getBtnBorrarArista().addActionListener(e -> {
            nodoSeleccionado = null;
            setModo(MODO_ELIMINAR_ARISTA, "Selecciona el PRIMER nodo para romper la unión");
        });

        vista.getBtnLimpiar().addActionListener(e -> {
            if (timerAnimacion != null && timerAnimacion.isRunning()) timerAnimacion.stop();
            inicio = null; 
            fin = null;
            
            List<String> aEliminar = new ArrayList<>();
            for (Nodo n : modelo.getNodos().values()) {
                if (!n.esFijo()) aEliminar.add(n.getId());
            }
            for (String id : aEliminar) modelo.eliminarNodo(id);

            generarMallaCalibrada(); 
            
            new File(ARCHIVO_DATOS).delete();
            
            vista.setInfo("Reset completado. Mapa original restaurado.");
        });

        vista.getBtnGuardar().addActionListener(e -> {
            modelo.guardarGrafo(ARCHIVO_DATOS);
            vista.setInfo("Progreso guardado automáticamente.");
        });
        
        vista.getPanelMapa().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int logX = vista.getPanelMapa().getLogicalX(e.getX());
                int logY = vista.getPanelMapa().getLogicalY(e.getY());
                manejarClick(logX, logY);
            }
        });
        vista.setVisible(true);
    }

    private void setModo(int m, String msg) {
        modoInteraccion = m;
        vista.setInfo(msg);
        if (m != MODO_AGREGAR_ARISTA && m != MODO_ELIMINAR_ARISTA) nodoSeleccionado = null;
    }

    private void manejarClick(int x, int y) {
        Nodo nodoClickeado = buscarNodoCercanoLogico(x, y, 30); 

        switch (modoInteraccion) {
            case MODO_AGREGAR_NODO:
                if (nodoClickeado == null) {
                    String nuevoId = "N_User" + contador++;
                    modelo.agregarNodo(new Nodo(nuevoId, x, y, false));
                    actualizarVista(null, null, null);
                    vista.setInfo("Nodo " + nuevoId + " creado.");
                } else {
                    vista.setInfo("Ya existe un nodo aquí.");
                }
                break;
            case MODO_ELIMINAR_NODO:
                if (nodoClickeado != null) {
                    modelo.eliminarNodo(nodoClickeado.getId());
                    if (inicio == nodoClickeado) inicio = null;
                    if (fin == nodoClickeado) fin = null;
                    actualizarVista(null, null, null);
                    vista.setInfo("Nodo eliminado.");
                }
                break;

            case MODO_AGREGAR_ARISTA:
                if (nodoClickeado != null) {
                    if (nodoSeleccionado == null) {
                        nodoSeleccionado = nodoClickeado;
                        vista.setInfo("Seleccionado: " + nodoClickeado.getId() + ". Click en otro para unir.");
                    } else {
                        if (nodoSeleccionado != nodoClickeado) {
                            modelo.agregarArista(nodoSeleccionado.getId(), nodoClickeado.getId(), true);
                            actualizarVista(null, null, null);
                            vista.setInfo("Unión manual creada.");
                            nodoSeleccionado = null; 
                        }
                    }
                }
                break;

            case MODO_ELIMINAR_ARISTA:
                if (nodoClickeado != null) {
                    if (nodoSeleccionado == null) {
                        nodoSeleccionado = nodoClickeado;
                        vista.setInfo("Seleccionado: " + nodoClickeado.getId() + ". Click en conectado para romper.");
                    } else {
                        modelo.eliminarArista(nodoSeleccionado.getId(), nodoClickeado.getId());
                        actualizarVista(null, null, null);
                        vista.setInfo("Unión eliminada.");
                        nodoSeleccionado = null;
                    }
                }
                break;

            case MODO_SELECCIONAR_INICIO:
                if (nodoClickeado != null) {
                    inicio = nodoClickeado;
                    actualizarVista(null, null, null);
                    vista.setInfo("Inicio: " + nodoClickeado.getId());
                    modoInteraccion = MODO_NORMAL;
                }
                break;

            case MODO_SELECCIONAR_FIN:
                if (nodoClickeado != null) {
                    fin = nodoClickeado;
                    actualizarVista(null, null, null);
                    vista.setInfo("Fin: " + nodoClickeado.getId());
                    modoInteraccion = MODO_NORMAL;
                }
                break;
        }
    }

    private void ejecutarAlgoritmo(String tipo) {
        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(vista, "Debes seleccionar Inicio y Fin.");
            return;
        }
        if (timerAnimacion != null && timerAnimacion.isRunning()) timerAnimacion.stop();

        ResultadoBusqueda res = tipo.equals("BFS") ? modelo.bfsCompleto(inicio.getId(), fin.getId()) : modelo.dfsCompleto(inicio.getId(), fin.getId());

        if (res == null) {
            vista.setInfo("No existe ruta.");
            return;
        }
        
        boolean modoAnimacion = vista.getComboModo().getSelectedIndex() == 1;

        if (modoAnimacion) {
            iniciarAnimacion(res, tipo);
        } else {
            actualizarVista(res.ruta, null, null);
            vista.setInfo(tipo + " finalizado. Pasos: " + res.ruta.size());
        }
    }

    private void iniciarAnimacion(ResultadoBusqueda res, String tipo) {
        nodosAnimados = new ArrayList<>();
        mapaPadresAnimacion = res.padres; 
        indiceAnimacion = 0;
        vista.setInfo("Animando " + tipo + "...");
        timerAnimacion = new Timer(100, e -> { 
            if (indiceAnimacion < res.visitados.size()) {
                nodosAnimados.add(res.visitados.get(indiceAnimacion));
                actualizarVista(null, nodosAnimados, mapaPadresAnimacion); 
                indiceAnimacion++;
            } else {
                ((Timer)e.getSource()).stop();
                actualizarVista(res.ruta, nodosAnimados, mapaPadresAnimacion); 
                vista.setInfo("Ruta encontrada.");
            }
        });
        timerAnimacion.start();
    }

    private Nodo buscarNodoCercanoLogico(int logX, int logY, double radio) {
        for (Nodo n : modelo.getNodos().values()) {
            if (n.getPoint().distance(logX, logY) < radio) return n;
        }
        return null;
    }

    private void actualizarVista(List<Nodo> ruta, List<Nodo> animacion, Map<String, String> padres) {
        vista.getPanelMapa().actualizar(modelo.getNodos(), modelo.getAristasVisibles(), ruta, animacion, padres, inicio, fin);
    }

    private void generarMallaCalibrada() {
        modelo.reiniciar();
        contador = 1;
        int[] callesX = { 28, 113, 198, 283, 368, 455, 540, 625, 710, 795, 880, 965, 1050, 1135 };
        int[] callesY = { 58, 160, 263, 365, 468, 570, 673, 758 };

        for (int y : callesY) {
            for (int x : callesX) {
                 if (x < 1200 && y < 800) {
                     if (vista.getPanelMapa().esCalle(x, y)) {
                        modelo.agregarNodo(new Nodo("N" + contador++, x, y, true));
                     }
                 }
            }
        }
        autoUnirOrtogonalEstricto(); 
        vista.setInfo("Mapa cargado.");
        actualizarVista(null, null, null);
    }

    private void autoUnirOrtogonalEstricto() {
        List<Nodo> lista = new ArrayList<>(modelo.getNodos().values());
        int distanciaCuadra = 160; 

        for (Nodo n1 : lista) {
            for (Nodo n2 : lista) {
                if (n1 == n2) continue;
                double dist = n1.getPoint().distance(n2.getPoint());
                if (dist < distanciaCuadra) { 
                    boolean mismaX = Math.abs(n1.getX() - n2.getX()) < 10;
                    boolean mismaY = Math.abs(n1.getY() - n2.getY()) < 10;
                    // false = invisible
                    if (mismaX || mismaY) {
                         modelo.agregarArista(n1.getId(), n2.getId(), false);
                    }
                }
            }
        }
    }
}