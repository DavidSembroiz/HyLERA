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
    
    private static int TOTAL_STEPS = 10000;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Connection c = new Connection(1, 10, 9999, 1, 12, 2);
        Connection c2 = new Connection(1, 10, 2500, 1, 12, 2);
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            
            dij.execute(net.getRouter(c.getSource()), c);
            dij.decreaseBandwidths();
            LinkedList<Router> path = dij.getPath(net.getRouter(c.getDestination()));
            dij.execute(net.getRouter(c2.getSource()), c2);
            net.printNetwork();
            LinkedList<Router> path2 = dij.getPath(net.getRouter(c.getDestination()));
            if (path == null) System.out.println("No path");
            else {
                net.addEnrutedConnection(c);
                for (Router r : path) {
                    System.out.println(r.getName());
                }
            }
            if (path2 == null) System.out.println("No path");
            else {
                for (Router r : path2) {
                    System.out.println(r.getName());
                }
            }
            net.decreaseTimesToLive();
            ++step;
        }
    }
}
