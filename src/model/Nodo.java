package model;

import java.awt.Point;

/**
 * Clase Nodo
 *
 * Representa un nodo dentro del grafo.
 *
 * Cada nodo contiene:
 * - Un identificador único (id)
 * - Una posición en el plano (x, y)
 * - Un indicador que define si es un nodo fijo o editable
 *
 * Los nodos son utilizados por la clase Grafo
 * para construir la estructura de conexiones.
 */
public class Nodo {

    // Identificador único del nodo
    private String id;

    // Posición del nodo en el mapa
    private Point punto;

    // Indica si el nodo es fijo (no editable)
    private boolean fijo;

    /**
     * Constructor del nodo.
     *
     * @param id Identificador único
     * @param x Coordenada X en el mapa
     * @param y Coordenada Y en el mapa
     * @param fijo Indica si el nodo es fijo
     */
    public Nodo(String id, int x, int y, boolean fijo) {
        this.id = id;
        this.punto = new Point(x, y);
        this.fijo = fijo;
    }

    /**
     * Devuelve el identificador del nodo.
     */
    public String getId() { return id; }

    /**
     * Devuelve la coordenada X del nodo.
     */
    public int getX() { return punto.x; }

    /**
     * Devuelve la coordenada Y del nodo.
     */
    public int getY() { return punto.y; }

    /**
     * Devuelve el objeto Point completo.
     */
    public Point getPoint() { return punto; }

    /**
     * Indica si el nodo es fijo.
     */
    public boolean esFijo() { return fijo; }

    /**
     * Convierte el nodo en formato texto.
     *
     * Formato:
     * id,x,y,1 (si es fijo)
     * id,x,y,0 (si no es fijo)
     *
     * Este formato se usa para guardar el grafo en archivo.
     */
    @Override
    public String toString() {
        return id + "," + punto.x + "," + punto.y + "," + (fijo ? "1" : "0");
    }
}
