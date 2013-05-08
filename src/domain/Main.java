package domain;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    
    private static int TOTAL_STEPS = 2;
    
    
    public static void main(String[] args) throws IOException {
       
        int step = 0;
        Network net = new Network();
        Dijkstra dij = new Dijkstra(net);
        //net.deleteFiles();
        while (step < TOTAL_STEPS) {
            System.out.println("------------------------------------------STEP " + step + "-----------------------------------------");
            net.decreaseTimesToLive();
            //net.createConnectionsFile(step);
            //ArrayList<Connection> connections = net.generateConnectionsFromFile(step);
            ArrayList<Connection> connections = net.generateConnections();
            for (Connection c : connections) {
                dij.execute(net.getRouter(c.getSource()), c);
                //net.printConnection(c);
                //net.printConnectionToFile(c);
            }
            ++step;
        }
        System.out.println("Blocked connections: " + net.getBlockedConnections());
        System.out.println("Total connections: " + net.getTotalConnections());
        System.out.println("Percentaje of blocking: " + net.getBlockingPercentaje());
        System.out.println("Actual Network Consumption: " + net.getActualConsumption());
        System.out.println("Total Network Consumption: " + net.getTotalConsumption());
    }
}
