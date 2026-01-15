package modelo;

import java.awt.Point;

public class Nodo {
    private String id;
    private Point punto;

    public Nodo(String id, int x, int y) {
        this.id = id;
        this.punto = new Point(x, y);
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return punto.x;
    }

    public int getY() {
        return punto.y;
    }

    public Point getPoint() {
        return punto;
    }
}