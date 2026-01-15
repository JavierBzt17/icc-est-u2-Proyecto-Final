import controlador.Controlador;
import modelo.Grafo;
import vista.VentanaPrincipal;

public class App {
    public static void main(String[] args) {
        new Controlador(new Grafo(), new VentanaPrincipal());
    }
}