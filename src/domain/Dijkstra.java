package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
    private final Network net;
    private final List<Router> routers;
    private final List<Fiber> fibers;
    private Set<Router> settledRouters;
    private Set<Router> unsettledRouters;
    private Map<Router, Router> predecessors;
    private Map<Router, Double> distance;
    
    public Dijkstra(Network network) {
        net = network;
        routers = network.getRouters();
        fibers = network.getFibers();
    }
    
    public void execute(Router source) {
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

    // Introduce constraint about weight and lambda
    
    private List<Router> getNeighbors(Router node) {
        List<Router> neighbors = new ArrayList<>();
        List<Integer> attFibersId = node.getAttachedFibers();
        System.out.println(node.getName());
        System.out.println(attFibersId);
        List<Fiber> attFibers = new ArrayList<>();
        for (Integer fib : attFibersId) {
            attFibers.add(net.getFiber(fib));
        }
        for (Fiber fib : attFibers) {
            if (fib.getNode1() == node.getId()) {
                neighbors.add(net.getRouter(fib.getNode2()));
            }
            else if (fib.getNode2() == node.getId()) {
                neighbors.add(net.getRouter(fib.getNode1()));
            }
        }
        return neighbors;
    }
    
    // Change the constraint about the fibers to count the Lambdas

    private double getDistance(Router node, Router neighbor) {
        List<Integer> attFibersId = node.getAttachedFibers();
        List<Fiber> attFibers = new ArrayList<>();
        for (Integer fib : attFibersId) {
            attFibers.add(net.getFiber(fib));
        }
        for (Fiber fib : attFibers) {
            if ((fib.getNode1() == node.getId() && 
                 fib.getNode2() == neighbor.getId()) ||
                 (fib.getNode2() == node.getId() &&
                  fib.getNode1() == neighbor.getId())) {
                return fib.getWeight();
            }
        }
        return Integer.MAX_VALUE;
    }
    
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
