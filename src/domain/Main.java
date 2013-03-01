/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.LinkedList;

/**
 *
 * @author David
 */
public class Main {
    
    private static int TOTAL_STEPS = 10;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Connection c;
        Connection c2 = new Connection(1, 10, 2500, 1, 12, 2);
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            c = new Connection(step + 1, 3, 1000, 1, 12, 2);
            dij.execute(net.getRouter(c.getSource()), c);
            LinkedList<Router> path = dij.getPath(net.getRouter(c.getDestination()));
            //dij.execute(net.getRouter(c2.getSource()), c2);
            //LinkedList<Router> path2 = dij.getPath(net.getRouter(c.getDestination()));
            //net.printNetwork();
            if (path == null) System.out.println("No path");
            else {
                //net.addEnrutedConnection(c);
                for (Router r : path) {
                    System.out.println(r.getName());
                }
            }
            /*if (path2 == null) System.out.println("No path");
            else {
                for (Router r : path2) {
                    System.out.println(r.getName());
                }
            }*/
            ++step;
        }
    }
}
