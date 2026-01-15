package modelo;

import java.util.*;

public class Grafo {
    private Map<String, Nodo> nodos;
    private Map<String, List<String>> adyacencias;

    public Grafo() {
        nodos = new HashMap<>();
        adyacencias = new HashMap<>();
    }

    public void reiniciar() {
        nodos.clear();
        adyacencias.clear();
    }

    public void agregarNodo(Nodo nodo) {
        nodos.put(nodo.getId(), nodo);
        adyacencias.put(nodo.getId(), new ArrayList<>());
    }

    // --- NUEVO MÉTODO PARA BORRAR UN NODO ---
    public void eliminarNodo(String id) {
        if (nodos.containsKey(id)) {
            nodos.remove(id); // Borra el nodo
            adyacencias.remove(id); // Borra su lista de amigos
            
            // Borra cualquier conexión que otros nodos tengan con este
            for (List<String> vecinos : adyacencias.values()) {
                vecinos.remove(id);
            }
        }
    }
    // ----------------------------------------

    public void agregarArista(String a, String b) {
        if (!adyacencias.containsKey(a) || !adyacencias.containsKey(b)) return;
        if (!adyacencias.get(a).contains(b)) {
            adyacencias.get(a).add(b);
            adyacencias.get(b).add(a);
        }
    }

    public Map<String, Nodo> getNodos() { return nodos; }
    public Map<String, List<String>> getAdyacencias() { return adyacencias; }

    public List<Nodo> bfs(String inicio, String fin) {
        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin)) return null;
        Queue<String> cola = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        cola.add(inicio);
        visitados.add(inicio);
        padres.put(inicio, null);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(fin)) return reconstruirCamino(padres, fin);

            for (String vecino : adyacencias.get(actual)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }
        return new ArrayList<>();
    }

    public List<Nodo> dfs(String inicio, String fin) {
        if (!nodos.containsKey(inicio) || !nodos.containsKey(fin)) return null;
        Stack<String> pila = new Stack<>();
        Set<String> visitados = new HashSet<>();
        Map<String, String> padres = new HashMap<>();

        pila.push(inicio);
        visitados.add(inicio);
        padres.put(inicio, null);

        while (!pila.isEmpty()) {
            String actual = pila.pop();
            if (actual.equals(fin)) return reconstruirCamino(padres, fin);

            for (String vecino : adyacencias.get(actual)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    pila.push(vecino);
                }
            }
        }
        return new ArrayList<>();
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
}