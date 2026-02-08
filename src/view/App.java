package view;

import controller.Controlador;
import model.Grafo;

public class App {
        public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Controlador(new Grafo(), new VentanaPrincipal());
        });
    }
}