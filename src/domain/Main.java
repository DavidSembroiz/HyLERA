package domain;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    
    private static int TOTAL_STEPS = 12000;
    
    
    
    public static void main(String[] args) throws IOException {
       
        int step = 0;
        
        Network net = new Network();
        Dijkstra dij = new Dijkstra(net);
        //net.deleteFiles();
        //net.printNodeDistribution();
        while (step < TOTAL_STEPS) {
            //System.out.println("------------------------------------------STEP " + step + "-----------------------------------------");
            net.decreaseTimesToLive();
            //net.createConnectionsFile(step);
            //ArrayList<Connection> connections = net.generateConnectionsFromFile(step);
            ArrayList<Connection> connections = net.generateConnections();
            /*if (step == 959) {
                net.setBlockedConnections(0);
                net.setTotalConnections(0);
            }*/
            if (step > 0 && step%50 == 0) {
                System.out.println(net.calculateBlock(net.getPartialBlockedConnections(), net.getPartialTotalConnections()));
            }
            else {
                net.insertPartialData(net.getPartialBlockedConnections(), net.getPartialTotalConnections());
            }
            net.setPartialBlockedConnections(0);
            net.setPartialTotalConnections(0);
            if (step > 0 && step%500 == 0) {
                    System.out.println("Blocked connections: " + net.getBlockedConnections());
                    System.out.println("Total connections: " + net.getTotalConnections());
                    System.out.println("Percentaje of blocking: " + net.getBlockingPercentaje());
                    System.out.println("Actual Network Consumption: " + net.getActualConsumption());
                    System.out.println("Total Network Consumption: " + net.getTotalConsumption());
                    System.out.println("");
            }
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
