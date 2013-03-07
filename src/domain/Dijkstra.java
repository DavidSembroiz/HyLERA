package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
    
    private final int PATH_NOT_FOUND = -3;
    private final int ORIGINAL_FIBERS = 53;
    
    private final Network net;
    private Set<Router> settledRouters;
    private Set<Router> unsettledRouters;
    private List<Integer> plausibleLambdas;
    private Map<Router, Router> predecessors;
    private Map<Router, Double> distance;
    private Connection c;
    private boolean found;
    
    public Dijkstra(Network network) {
        net = network;
    }
    
    // Right now, the assignation of the lambda follows this sequence:
    // we get all the possible lambdas for the fibers attached to the source router
    // we iterate through all these lambdas and we assign the first that gives us
    // a path to the destination. We may want to change that in order to get the
    // lambda with the lowest weight?
    
    /*
     * Igual es necesario cambiar algo del if inicial porque si una conexion es asignada
     * a un lightpath sin saber el camino real del lightpath, puede ser que cuando se
     * vayan eliminando las conexiones, al final se quede un lightpath con conexiones que
     * no contienen el camino real, sin posibilidad de aumentar los bws cuando se vacie.
     */
    
    
    public void execute(Router source, Connection con) {
        this.c = con;
        plausibleLambdas = net.getPlausibleLambdas(c);
        found = false;
        Fiber lp;
        lp = net.lightpathAvailable(c);
        if (lp != null) {
            net.assignLightpath(c, lp);
        }
        else {
            for (Iterator<Integer> it = plausibleLambdas.iterator(); !found && it.hasNext();) {
                c.setLambda(it.next());
                settledRouters = new HashSet<>();
                unsettledRouters = new HashSet<>();
                distance = new HashMap<>();
                predecessors = new HashMap<>();
                distance.put(source, 0.);
                unsettledRouters.add(source);
                while (!unsettledRouters.isEmpty()) {
                    Router node = getMinimum(unsettledRouters);
                    settledRouters.add(node);
                    unsettledRouters.remove(node);
                    findMinimalDistance(node);
                }
                if (this.getPath(net.getRouter(c.getDestination())) != null) {
                    found = true;
                    c.setPath(this.getPath(net.getRouter(c.getDestination())));
                }
            }
            if (!found) c.setLambda(PATH_NOT_FOUND);
            net.decreaseBandwidths(c);
        }
    }
    
    private void findMinimalDistance(Router node) {
        List<Router> adjacentRouters = getNeighbors(node);
        double dist, shortestDistance;
        for (Router neighbor : adjacentRouters) {
            shortestDistance = getShortestDistance(node);
            dist = getDistance(node, neighbor);
            if (getShortestDistance(neighbor) > shortestDistance + dist) {
                distance.put(neighbor, shortestDistance + dist);
                predecessors.put(neighbor, node);
                unsettledRouters.add(neighbor);
            }
        }
    }
    
    private Router getMinimum(Set<Router> r) {
        Router min = null;
        for (Router rou : r) {
            if (min == null) min = rou;
            else {
                if (getShortestDistance(rou) < getShortestDistance(min))
                    min = rou;
            }
        }
        return min;
    }

    private double getShortestDistance(Router rou) {
        Double d = distance.get(rou);
        if (d == null) return Integer.MAX_VALUE;
        return d;
    }

    // Might change neighbors to HashSet to delete insert variable
    
    private List<Router> getNeighbors(Router node) {
        List<Router> neighbors = new ArrayList<>();
        List<Integer> attFibersId = node.getAttachedFibers();
        List<Fiber> attFibers = new ArrayList<>();
        List<Lambda> lambdas;
        boolean insert;
        for (Integer fib : attFibersId) {
            if(fib <= ORIGINAL_FIBERS) attFibers.add(net.getFiber(fib));
            else attFibers.add(net.getLightfiber(fib));
        }
        for (Fiber fib : attFibers) {
            lambdas = fib.getLambdas();
            insert = false;
            for (Lambda lam : lambdas) {
                if ((-lam.getId() == c.getLambda() ||
                    lam.getId() == c.getLambda()) && 
                    lam.getResidualBandwidth() >= c.getBandwidth()) {
                    insert = true;
                }
            }
            if (insert) {
                if (fib.getNode1() == node.getId()) {
                    neighbors.add(net.getRouter(fib.getNode2()));
                }
                else if (fib.getNode2() == node.getId()) {
                    neighbors.add(net.getRouter(fib.getNode1()));
                }
            }
        }
        return neighbors;
    }
    

    private double getDistance(Router node, Router neighbor) {
        List<Integer> attFibersId = node.getAttachedFibers();
        List<Fiber> attFibers = new ArrayList<>();
        for (Integer fib : attFibersId) {
            if(fib <= ORIGINAL_FIBERS) attFibers.add(net.getFiber(fib));
            else attFibers.add(net.getLightfiber(fib));
        }
        for (Fiber fib : attFibers) {
            if ((fib.getNode1() == node.getId() && 
                 fib.getNode2() == neighbor.getId()) ||
                 (fib.getNode2() == node.getId() &&
                  fib.getNode1() == neighbor.getId())) {
                return fib.getLambda(c.getLambda()).getWeight();
            }
        }
        return Integer.MAX_VALUE;
    }
    
    // This method should be private. For now is public to test from the main
    
    public LinkedList<Router> getPath(Router node) {
        LinkedList<Router> path = new LinkedList<>();
        Router step = node;
        if (predecessors.get(step) == null) return null;
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        
        Collections.reverse(path);
        return path;
    }
}
