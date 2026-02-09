package model;

import java.io.*;
import java.util.*;

/**
 * Clase Grafo
 *
 * Representa la estructura principal del grafo del sistema.
 *
 * Contiene:
 * - Un conjunto de nodos
 * - Una estructura de adyacencias (lista de vecinos)
 * - Un registro de aristas visibles (para mostrar en la vista)
 *
 * También implementa:
 * - Algoritmo BFS (Búsqueda en Anchura)
 * - Algoritmo DFS (Búsqueda en Profundidad)
 * - Guardado y carga desde archivo
 */
public class Grafo {

    // Mapa que almacena los nodos usando su ID como clave
    private Map<String, Nodo> nodos;

    // Lista de adyacencias (representa conexiones entre nodos)
    // Clave: ID del nodo
    // Valor: Lista de IDs vecinos
    private Map<String, List<String>> adyacencias;

    // Almacena las aristas visibles en el mapa
    // Clave: "A-B"
    // Valor: true si es bidireccional, false si es unidireccional
    private Map<String, Boolean> aristasVisibles;

    /**
     * Constructor del grafo.
     * Inicializa las estructuras de datos.
     */
    public Grafo() {
        nodos = new HashMap<>();
        adyacencias = new HashMap<>();
        aristasVisibles = new HashMap<>();
    }

    /**
     * Reinicia completamente el grafo.
     * Elimina nodos, adyacencias y aristas.
     */
    public void reiniciar() {
        nodos.clear();
        adyacencias.clear();
        aristasVisibles.clear();
    }

    /**
     * Agrega un nuevo nodo al grafo.
     */
    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
        adyacencias.put(nodo.getId(), new ArrayList<>());
    }

    /**
     * Elimina un nodo del grafo y todas sus conexiones.
     */
    public void eliminarNodo(String id) {

        nodos.remove(id);
        adyacencias.remove(id);

        // Elimina referencias en otras listas de vecinos
        for (List<String> vecinos : adyacencias.values()) {
            vecinos.remove(id);
        }

        // Elimina aristas visibles relacionadas con el nodo
        aristasVisibles.entrySet().removeIf(e ->
                e.getKey().startsWith(id + "-") ||
                e.getKey().endsWith("-" + id));
    }

    /**
     * Agrega una arista entre dos nodos.
     *
     * @param a Nodo origen
     * @param b Nodo destino
     * @param visible Indica si debe mostrarse en pantalla
     * @param bidireccional Indica si la conexión es en ambos sentidos
     */
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

    /**
     * Elimina la conexión entre dos nodos.
     */
    public void eliminarArista(String a, String b) {

        if (adyacencias.containsKey(a))
            adyacencias.get(a).remove(b);

        if (adyacencias.containsKey(b))
            adyacencias.get(b).remove(a);

        String clave1 = a + "-" + b;
        String clave2 = b + "-" + a;

        if (aristasVisibles.containsKey(clave1)) {
            aristasVisibles.remove(clave1);
        }

        if (aristasVisibles.containsKey(clave2)) {
            aristasVisibles.remove(clave2);
        }
    }

    /**
     * Ejecuta el algoritmo BFS (Breadth First Search).
     * Busca el camino más corto en número de aristas.
     */
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

    /**
     * Ejecuta el algoritmo DFS (Depth First Search).
     * Explora lo más profundo posible antes de retroceder.
     */
    public ResultadoBusqueda dfsCompleto(String inicio, String fin) {

        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin))
            return null;

        long startTime = System.nanoTime();

        List<Nodo> visitadosOrden = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        boolean encontrado = dfsRecursivo(
                inicio,
                fin,
                visitados,
                padres,
                visitadosOrden
        );

        if (!encontrado) return null;

        long tiempo = System.nanoTime() - startTime;

        return new ResultadoBusqueda(
                reconstruirCamino(padres, fin),
                visitadosOrden,
                padres,
                tiempo
        );
    }

    /**
     * Método recursivo utilizado por DFS.
     */
    private boolean dfsRecursivo(
        String actual,
        String destino,
        Set<String> visitados,
        Map<String, String> padres,
        List<Nodo> visitadosOrden) {

        visitados.add(actual);
        visitadosOrden.add(nodos.get(actual));

        if (actual.equals(destino)) {
            return true;
        }

        for (String vecino : adyacencias.get(actual)) {

            if (!visitados.contains(vecino)) {

                padres.put(vecino, actual);

                if (dfsRecursivo(vecino, destino, visitados, padres, visitadosOrden)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Reconstruye el camino desde el nodo final
     * utilizando el mapa de padres.
     */
    private List<Nodo> reconstruirCamino(Map<String, String> padres, String fin) {

        List<Nodo> camino = new ArrayList<>();
        String actual = fin;

        while (actual != null) {
            camino.add(0, nodos.get(actual));
            actual = padres.get(actual);
        }

        return camino;
    }

    /**
     * Guarda el grafo en un archivo de texto.
     * Se almacenan nodos y aristas.
     */
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

    /**
     * Carga un grafo desde archivo.
     * Reconstruye nodos y aristas.
     */
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

    public void cargarGrafoDesdeStream(java.io.InputStream inputStream) {

        reiniciar();

        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(inputStream))) {

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

                        boolean esFijo =
                                partes.length > 3 &&
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve todos los nodos del grafo.
     */
    public Map<String, Nodo> getNodos() {
        return nodos;
    }

    /**
     * Devuelve la estructura de adyacencias.
     */
    public Map<String, List<String>> getAdyacencias() {
        return adyacencias;
    }

    /**
     * Devuelve las aristas visibles.
     */
    public Map<String, Boolean> getAristasVisibles() {
        return aristasVisibles;
    }
}
