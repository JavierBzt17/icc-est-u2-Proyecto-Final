package view;

import controller.Controlador;
import model.Grafo;

/**
 * Clase App
 *
 * Es la clase principal del sistema.
 * Contiene el método main, que inicia la aplicación.
 *
 * Su función es:
 * - Crear el modelo (Grafo)
 * - Crear la vista (VentanaPrincipal)
 * - Crear el controlador que conecta modelo y vista
 *
 * Se utiliza SwingUtilities.invokeLater para asegurar
 * que la interfaz gráfica se ejecute en el hilo correcto de Swing.
 */
public class App {

    /**
     * Método principal del programa.
     * Punto de entrada de la aplicación.
     */
    public static void main(String[] args) {

        // Ejecuta la interfaz gráfica en el hilo de eventos de Swing
        javax.swing.SwingUtilities.invokeLater(() -> {

            // Se crea el controlador pasando:
            // - Un nuevo Grafo (modelo)
            // - Una nueva VentanaPrincipal (vista)
            new Controlador(new Grafo(), new VentanaPrincipal());
        });
    }
}
