/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author David
 */
public class Main {
    
    private static int TOTAL_STEPS = 1000;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Connection c;
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            c = new Connection(step + 1, 16, 10000, 1, 24);
            dij.execute(net.getRouter(c.getSource()), c);
            System.out.println("Chosen lambda: " + c.getLambda());
            ++step;
        }
        //net.printNetwork();
        System.out.println("Blocked connections: " + net.getBlocking());
    }
}
