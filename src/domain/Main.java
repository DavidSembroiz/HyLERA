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
    

    public static void main(String[] args) {
        Network net = new Network();
        Connection c = new Connection(1, 10, 1000, 1, 2, 2);
        Dijkstra dij = new Dijkstra(net, c);
        dij.execute(net.getRouter(c.getSource()));
        LinkedList<Router> path = dij.getPath(net.getRouter(c.getDestination()));
        if (path == null) System.out.println("No possible path");
        else {
            for (Router r : path) {
                System.out.println(r.getName() + " ");
            }
        }
    }
}
