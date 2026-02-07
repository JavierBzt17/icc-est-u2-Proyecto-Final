package model;

import java.awt.Point;

public class Nodo {

    private String id;
    private Point punto;
    private boolean fijo;

    public Nodo(String id, int x, int y, boolean fijo) {
        this.id = id;
        this.punto = new Point(x, y);
        this.fijo = fijo;
    }

    public String getId() { return id; }
    public int getX() { return punto.x; }
    public int getY() { return punto.y; }
    public Point getPoint() { return punto; }
    public boolean esFijo() { return fijo; }

    @Override
    public String toString() {
        return id + "," + punto.x + "," + punto.y + "," + (fijo ? "1" : "0");
    }
}
