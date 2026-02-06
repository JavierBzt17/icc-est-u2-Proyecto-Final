import controlador.Controlador;
import modelo.Grafo;
import vista.VentanaPrincipal;

public class App {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Controlador(new Grafo(), new VentanaPrincipal());
        });
    }
}