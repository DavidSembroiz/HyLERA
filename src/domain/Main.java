package domain;

import java.util.ArrayList;

public class Main {
    
    private static int TOTAL_STEPS = 500;
    
    
    public static void main(String[] args) {
        
        int step = 0;
        Network net = new Network();
        Dijkstra dij = new Dijkstra(net);
        while (step < TOTAL_STEPS) {
            System.out.println("------------------------------------------STEP " + step + "-----------------------------------------");
            net.decreaseTimesToLive();
            //net.createConnectionsFile(step);
            ArrayList<Connection> connections = net.generateConnectionsFromFile(step);
            for (Connection c : connections) {
                dij.execute(net.getRouter(c.getSource()), c);
                net.printConnection(c);
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
