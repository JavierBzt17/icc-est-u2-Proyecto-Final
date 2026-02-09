package model;

import java.util.List;
import java.util.Map;

/**
 * Clase ResultadoBusqueda
 *
 * Representa el resultado obtenido después de ejecutar
 * un algoritmo de búsqueda (BFS o DFS).
 *
 * Contiene:
 * - La ruta encontrada desde el nodo inicio hasta el nodo fin
 * - El orden de nodos visitados durante la ejecución
 * - El mapa de padres usado para reconstruir el camino
 * - El tiempo de ejecución en nanosegundos
 *
 * Esta clase funciona como un contenedor de datos
 * que permite enviar toda la información al controlador y la vista.
 */
public class ResultadoBusqueda {

    // Lista que representa el camino final encontrado
    public List<Nodo> ruta;

    // Lista de nodos visitados en el orden en que fueron explorados
    public List<Nodo> visitados;

    // Mapa que almacena las relaciones padre-hijo
    // Clave: nodo actual
    // Valor: nodo desde el cual se llegó
    public Map<String, String> padres;

    // Tiempo total de ejecución del algoritmo (en nanosegundos)
    public long tiempo;

    /**
     * Constructor que inicializa todos los datos del resultado.
     *
     * @param ruta Camino final encontrado
     * @param visitados Lista de nodos visitados
     * @param padres Estructura usada para reconstruir el camino
     * @param tiempo Tiempo de ejecución en nanosegundos
     */
    public ResultadoBusqueda(List<Nodo> ruta,
                             List<Nodo> visitados,
                             Map<String, String> padres,
                             long tiempo) {
        this.ruta = ruta;
        this.visitados = visitados;
        this.padres = padres;
        this.tiempo = tiempo;
    }
}
