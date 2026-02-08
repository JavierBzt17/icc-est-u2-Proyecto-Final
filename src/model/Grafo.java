package model;

import java.io.*;
import java.util.*;

public class Grafo {

    private Map<String, Nodo> nodos;
    private Map<String, List<String>> adyacencias;
    private Map<String, Boolean> aristasVisibles;

    public Grafo() {
        nodos = new HashMap<>();
        adyacencias = new HashMap<>();
        aristasVisibles = new HashMap<>();
    }

    public void reiniciar() {
        nodos.clear();
        adyacencias.clear();
        aristasVisibles.clear();
    }

    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
        adyacencias.putIfAbsent(nodo.getId(), new ArrayList<>());
    }

    public void eliminarNodo(String id) {

        nodos.remove(id);
        adyacencias.remove(id);

        for (List<String> vecinos : adyacencias.values()) {
            vecinos.remove(id);
        }

        aristasVisibles.entrySet().removeIf(e ->
                e.getKey().startsWith(id + "-") ||
                e.getKey().endsWith("-" + id));
    }

    public void agregarArista(String a, String b, boolean visible, boolean bidireccional) {

        if (!adyacencias.containsKey(a) || !adyacencias.containsKey(b)) return;

        if (!adyacencias.get(a).contains(b)) {
            adyacencias.get(a).add(b);
        }

        if (bidireccional) {
            if (!adyacencias.get(b).contains(a)) {
                adyacencias.get(b).add(a);
            }
        }

        if (visible) {
            aristasVisibles.put(a + "-" + b, bidireccional);
        }
    }




    public void eliminarArista(String a, String b) {

        if (adyacencias.containsKey(a))
            adyacencias.get(a).remove(b);

        if (adyacencias.containsKey(b))
            adyacencias.get(b).remove(a);

        aristasVisibles.remove(a + "-" + b);
        aristasVisibles.remove(b + "-" + a);
    }


    public ResultadoBusqueda bfsCompleto(String inicio, String fin) {

        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin))
            return null;

        long startTime = System.nanoTime();

        List<Nodo> visitadosOrden = new ArrayList<>();
        Queue<String> cola = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        cola.add(inicio);
        visitados.add(inicio);
        padres.put(inicio, null);

        while (!cola.isEmpty()) {

            String actual = cola.poll();
            visitadosOrden.add(nodos.get(actual));

            if (actual.equals(fin)) {
                long tiempo = System.nanoTime() - startTime;
                return new ResultadoBusqueda(
                        reconstruirCamino(padres, fin),
                        visitadosOrden,
                        padres,
                        tiempo
                );
            }

            for (String vecino : adyacencias.get(actual)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }

        return null;
    }

    public ResultadoBusqueda dfsCompleto(String inicio, String fin) {

        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin))
            return null;

        long startTime = System.nanoTime();

        List<Nodo> visitadosOrden = new ArrayList<>();
        Stack<String> pila = new Stack<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        pila.push(inicio);

        while (!pila.isEmpty()) {

            String actual = pila.pop();

            if (!visitados.contains(actual)) {

                visitados.add(actual);
                visitadosOrden.add(nodos.get(actual));

                if (actual.equals(fin)) {
                    long tiempo = System.nanoTime() - startTime;
                    return new ResultadoBusqueda(
                            reconstruirCamino(padres, fin),
                            visitadosOrden,
                            padres,
                            tiempo
                    );
                }

                for (String vecino : adyacencias.get(actual)) {
                    if (!visitados.contains(vecino)) {
                        padres.put(vecino, actual);
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
            for (Nodo n : nodos.values()) {
                pw.println(n.toString());
            }

            pw.println("ARISTAS");

            for (Map.Entry<String, Boolean> entry : aristasVisibles.entrySet()) {

                String clave = entry.getKey();
                boolean bidireccional = entry.getValue();

                String[] partes = clave.split("-");
                pw.println(partes[0] + "," + partes[1] + "," +
                        (bidireccional ? "1" : "0"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

                if (linea.equals("NODOS")) {
                    leyendoNodos = true;
                    leyendoAristas = false;
                    continue;
                }

                if (linea.equals("ARISTAS")) {
                    leyendoNodos = false;
                    leyendoAristas = true;
                    continue;
                }

                if (leyendoNodos) {

                    String[] partes = linea.split(",");

                    if (partes.length >= 3) {

                        boolean esFijo = partes.length > 3 &&
                                        partes[3].equals("1");

                        agregarNodo(new Nodo(
                                partes[0],
                                Integer.parseInt(partes[1]),
                                Integer.parseInt(partes[2]),
                                esFijo
                        ));
                    }
                }

                else if (leyendoAristas) {

                    String[] partes = linea.split(",");

                    if (partes.length >= 2) {

                        boolean bidireccional =
                                partes.length > 2 &&
                                partes[2].equals("1");

                        agregarArista(
                                partes[0],
                                partes[1],
                                true,
                                bidireccional
                        );
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String, Nodo> getNodos() {
        return nodos;
    }

    public Map<String, List<String>> getAdyacencias() {
        return adyacencias;
    }

    public Map<String, Boolean> getAristasVisibles() {
        return aristasVisibles;
    }
}
