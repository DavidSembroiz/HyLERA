package domain;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    
   
    public static void main(String[] args) throws IOException {
       
        int step = 0;
        boolean hybrid = true;
        
        Network net = new Network();
        Dijkstra dij = new Dijkstra(net);
        
        
        /*
         * Block to generate only N connections, all at the same time and check 
         * the behavior of the network in this scenario
         */
        
        int n = 500;
        //net.createRawConnectionsFile(n);
        ArrayList<Connection> con = net.generateRawConnectionsFromFile();
        for (Connection co : con) {
            dij.execute(net.getRouter(co.getSource()), co);
            //net.printConnectionToFile(co);
        }
        
        /*
         * End of block
         */
        
        /*
        int TOTAL_STEPS = net.getTotalSteps();
        int DAYS = net.getDays();
        double partial = 0;
        //net.deleteFiles();
        //net.printNodeDistribution();
        while (step < TOTAL_STEPS) {
            net.decreaseTimesToLive();
            //net.createConnectionsFile(step);
            ArrayList<Connection> connections = net.generateConnectionsFromFile(step);
            //ArrayList<Connection> connections = net.generateConnections();
            if (step > 0 && step%(TOTAL_STEPS/(240*DAYS)) == 0) { // 10 times every hour
                partial = net.calculateBlock(net.getPartialBlockedConnections(), net.getPartialTotalConnections());
                System.out.println(partial);
            }
            else {
                net.insertPartialData(net.getPartialBlockedConnections(), net.getPartialTotalConnections());
            }
            net.setPartialBlockedConnections(0);
            net.setPartialTotalConnections(0);
            if (step > 0 && step%(TOTAL_STEPS/(24*DAYS)) == 0) { // 1 time every hour
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
            */
            /**
             * Block to use hybrid method with step constraint rather than
             * percentaje constraint.
             */
            
            /*
            if (hybrid) {
                if (net.MODE == 0) {
                    if (step%2400 < 700 || step%2400 >= 1700) {
                        net.MODE = 1;
                        System.out.println("Mode changed from 0 to 1 (Activated Energy Awareness)");
                    }
                }
                else if (net.MODE == 1) {
                    if (step%2400 >= 700 && step%2400 < 1700) {
                        net.MODE = 0;
                        System.out.println("Mode changed from 1 to 0 (Activated Blocking Awareness)");
                    }
                }
            }
            */
            
            /**
             * Block to use hybrid method with percentaje constraint.
             */
            
            /*
            if (hybrid && step > 0 && step%(TOTAL_STEPS/(240*DAYS)) == 0) {
                if (net.MODE == 1 && partial > 2) {
                    net.MODE = 0;
                    System.out.println("Mode changed from 1 to 0 (Activated Blocking Awareness)");
                }
                else if (net.MODE == 0 && partial < 1.5) {
                    net.MODE = 1;
                    System.out.println("Mode changed from 0 to 1 (Activated Energy Awareness)");
                }
            }
            */
            
            ++step;
        //}
        System.out.println("Blocked connections: " + net.getBlockedConnections());
        System.out.println("Total connections: " + net.getTotalConnections());
        System.out.println("Percentaje of blocking: " + net.getBlockingPercentaje());
        System.out.println("Actual Network Consumption: " + net.getActualConsumption());
        System.out.println("Total Network Consumption: " + net.getTotalConsumption());
    }
}
