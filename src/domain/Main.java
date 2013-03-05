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
    
    private static int TOTAL_STEPS = 1;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Connection c, c2;
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            c = new Connection(step + 1, 16, 2500, 1, 2);
            c2 = new Connection(step + 1, 16, 2500, 1, 2);
            dij.execute(net.getRouter(c.getSource()), c);
            dij.execute(net.getRouter(c2.getSource()), c2);
            c.printPath();
            //c2.printPath();
            //System.out.println("Chosen lambda: " + c.getLambda());
            ++step;
        }
        //net.printNetwork();
        System.out.println("Blocked connections: " + net.getBlocking());
    }
}
