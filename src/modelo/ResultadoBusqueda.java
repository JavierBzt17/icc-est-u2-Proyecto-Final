package modelo;
import java.util.List;
import java.util.Map;

public class ResultadoBusqueda {
    public List<Nodo> ruta;
    public List<Nodo> visitados;
    public Map<String, String> padres; 
    public long tiempo;

    public ResultadoBusqueda(List<Nodo> ruta, List<Nodo> visitados, Map<String, String> padres, long tiempo) {
        this.ruta = ruta;
        this.visitados = visitados;
        this.padres = padres;
        this.tiempo = tiempo;
    }
}