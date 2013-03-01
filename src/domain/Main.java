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
    
    private static int TOTAL_STEPS = 100;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Connection c;
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            c = new Connection(step + 1, 5, 1000, 1, 24, 2);
            dij.execute(net.getRouter(c.getSource()), c);
            LinkedList<Router> path = c.getPath();
            if (path != null) {
                for (Router r : path) {
                    System.out.println(r.getName());
                }
            }
            ++step;
        }
        net.printNetwork();
        System.out.println("Blocked connections: " + net.getBlocking());
    }
}
