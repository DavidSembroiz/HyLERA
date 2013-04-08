/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Main {
    
    private static int TOTAL_STEPS = 2000;
    
    
    public static void main(String[] args) {
        
        /* Cambiar asignacion inicial de lambda, quedarse con la que tiene distancia menor
         * Cambiar lightpathfiber por conjunto de lightpaths y crearlas en createLightpath
         * para simular la concatenacion.
         * Asignarle longitud correcta y bw correcto a los lightpaths
         */
        
        int step = 0;
        Network net = new Network();
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            System.out.println("------------------------------------------STEP " + step + "-----------------------------------------");
            net.decreaseTimesToLive();
            ArrayList<Connection> connections = net.generateConnections();
            if (step > 1900) connections.clear();
            for (Connection c : connections) {
                dij.execute(net.getRouter(c.getSource()), c);
                net.printConnection(c);
            }
            ++step;
        }
        boolean error = false;
        for (Router r : net.getRouters()) {
            if (r.getActualConsumption()*2 != r.getTotalConsumption()) {
                error = true;
                System.out.println("ERROR");
            }
        }
        if (!error) System.out.println("OK!");
        System.out.println("Blocked connections: " + net.getBlockedConnections());
        System.out.println("Total connections: " + net.getTotalConnections());
        System.out.println("Percentaje of blocking: " + net.getBlockingPercentaje());
    }
}
