/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.List;

/**
 *
 * @author David
 */
public class Main {
    
    private static int TOTAL_STEPS = 5000;
    
    
    public static void main(String[] args) {
        
        /* Cambiar asignacion inicial de lambda, quedarse con la que tiene distancia menor
         * Cambiar lightpathfiber por conjunto de lightpaths y crearlas en createLightpath
         * para simular la concatenacion.
         * Asignarle longitud correcta y bw correcto a los lightpaths
         */
        
        int step = 0;
        Network net = new Network();
        Connection c, c2, c3, c4, c5;
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            c = new Connection(step + 1, 8, 2500, 24, 1);
            /*c2 = new Connection(step + 2, 16, 310, 1, 3);
            c3 = new Connection(step + 3, 16, 310, 1, 3);
            c4 = new Connection(step + 4, 16, 310, 1, 2);
            c5 = new Connection(step + 4, 16, 200000, 1, 2);*/
            dij.execute(net.getRouter(c.getSource()), c);
            /*dij.execute(net.getRouter(c2.getSource()), c2);
            dij.execute(net.getRouter(c3.getSource()), c3);
            dij.execute(net.getRouter(c4.getSource()), c4);
            dij.execute(net.getRouter(c5.getSource()), c5);*/
            //c.printConnection();
            //c.printPath();
            /*c2.printPath();
            c2.printConnection();
            c3.printPath();
            c3.printConnection();
            c4.printPath();
            c4.printConnection();
            c5.printPath();
            c5.printConnection();*/
            //System.out.println("Chosen lambda: " + c.getLambda());
            ++step;
        }
        //net.printNetwork();
        System.out.println("Blocked connections: " + net.getBlocking());
    }
}
