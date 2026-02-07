package model;

import java.io.*;
import java.util.*;

public class Grafo {
    private Map<String, Nodo> nodos;
    private Map<String, List<String>> adyacencias;
    private Set<String> aristasVisibles; 

    public Grafo() {
        nodos = new HashMap<>();
        adyacencias = new HashMap<>();
        aristasVisibles = new HashSet<>();
    }

    public void reiniciar() {
        nodos.clear();
        adyacencias.clear();
        aristasVisibles.clear();
    }

    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
        if (!adyacencias.containsKey(nodo.getId())) {
            adyacencias.put(nodo.getId(), new ArrayList<>());
        }
    }

    public void eliminarNodo(String id) {
        if (nodos.containsKey(id)) {
            nodos.remove(id);
            adyacencias.remove(id);
            for (List<String> vecinos : adyacencias.values()) {
                vecinos.remove(id);
            }
            aristasVisibles.removeIf(k -> k.contains(id));
        }
    }

    public void agregarArista(String a, String b, boolean visible) {
        if (!adyacencias.containsKey(a) || !adyacencias.containsKey(b)) return;
        
        if (!a.equals(b) && !adyacencias.get(a).contains(b)) {
            adyacencias.get(a).add(b);
            adyacencias.get(b).add(a);
        }

        if (visible) {
            String clave = a.compareTo(b) < 0 ? a + "-" + b : b + "-" + a;
            aristasVisibles.add(clave);
        }
    }

    public void eliminarArista(String a, String b) {
        if (adyacencias.containsKey(a)) adyacencias.get(a).remove(b);
        if (adyacencias.containsKey(b)) adyacencias.get(b).remove(a);
        
        String clave = a.compareTo(b) < 0 ? a + "-" + b : b + "-" + a;
        aristasVisibles.remove(clave);
    }

    public ResultadoBusqueda bfsCompleto(String inicio, String fin) {
        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin)) return null;
        long startTime = System.nanoTime();
        List<Nodo> visitadosOrden = new ArrayList<>(); 
        Queue<String> cola = new LinkedList<>();
        Set<String> visitadosSet = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        cola.add(inicio);
        visitadosSet.add(inicio);
        visitadosOrden.add(nodos.get(inicio));
        padres.put(inicio, null);

        while (!cola.isEmpty()) {
            String actualId = cola.poll();
            if (actualId.equals(fin)) {
                long tiempo = System.nanoTime() - startTime;
                return new ResultadoBusqueda(reconstruirCamino(padres, fin), visitadosOrden, padres, tiempo);
            }
            for (String vecino : adyacencias.get(actualId)) {
                if (!visitadosSet.contains(vecino)) {
                    visitadosSet.add(vecino);
                    visitadosOrden.add(nodos.get(vecino));
                    padres.put(vecino, actualId);
                    cola.add(vecino);
                }
            }
        }
        return null;
    }

    public ResultadoBusqueda dfsCompleto(String inicio, String fin) {
        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin)) return null;
        long startTime = System.nanoTime();
        List<Nodo> visitadosOrden = new ArrayList<>();
        Stack<String> pila = new Stack<>();
        Set<String> visitadosSet = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        pila.push(inicio);
        
        while (!pila.isEmpty()) {
            String actualId = pila.pop();
            if (!visitadosSet.contains(actualId)) {
                visitadosSet.add(actualId);
                visitadosOrden.add(nodos.get(actualId));

                if (actualId.equals(fin)) {
                    long tiempo = System.nanoTime() - startTime;
                    return new ResultadoBusqueda(reconstruirCamino(padres, fin), visitadosOrden, padres, tiempo);
                }
                for (String vecino : adyacencias.get(actualId)) {
                    if (!visitadosSet.contains(vecino)) {
                        padres.put(vecino, actualId); 
                        pila.push(vecino);
                    }
                }
            }
        }
        return null;
    }

    private List<Nodo> reconstruirCamino(Map<String, String> padres, String fin) {
        List<Nodo> camino = new ArrayList<>();
        String actual = fin;
        while (actual != null) {
            camino.add(0, nodos.get(actual));
            actual = padres.get(actual);
        }
        return camino;
    }

    public void guardarGrafo(String rutaArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
            pw.println("NODOS");
            for (Nodo n : nodos.values()) pw.println(n.toString());
            
            pw.println("ARISTAS");
            Set<String> guardadas = new HashSet<>();
            for (String id : adyacencias.keySet()) {
                for (String vecino : adyacencias.get(id)) {
                    String clave = id.compareTo(vecino) < 0 ? id + "-" + vecino : vecino + "-" + id;
                    if (!guardadas.contains(clave)) {
                        String esVisible = aristasVisibles.contains(clave) ? "1" : "0";
                        pw.println(id + "," + vecino + "," + esVisible);
                        guardadas.add(clave);
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void cargarGrafo(String rutaArchivo) {
        reiniciar();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean leyendoNodos = false;
            boolean leyendoAristas = false;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (linea.equals("NODOS")) { leyendoNodos = true; leyendoAristas = false; continue; }
                if (linea.equals("ARISTAS")) { leyendoNodos = false; leyendoAristas = true; continue; }

                if (leyendoNodos) {
                    try {
                        String[] partes = linea.split(",");
                        if (partes.length >= 3) {
                            boolean esFijo = (partes.length > 3) && partes[3].equals("1");
                            agregarNodo(new Nodo(partes[0], Integer.parseInt(partes[1]), Integer.parseInt(partes[2]), esFijo));
                        }
                    } catch(Exception e) {}
                } else if (leyendoAristas) {
                    try {
                        String[] partes = linea.split(",");
                        if (partes.length >= 2) {
                            boolean visible = (partes.length > 2) && partes[2].equals("1");
                            agregarArista(partes[0], partes[1], visible);
                        }
                    } catch(Exception e) {}
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public Map<String, Nodo> getNodos() { return nodos; }
    public Map<String, List<String>> getAdyacencias() { return adyacencias; }
    public Set<String> getAristasVisibles() { return aristasVisibles; } 
}