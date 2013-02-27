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
    private List<Router> routers;
    private List<Fiber> fibers;
    private Set<Router> settledRouters;
    private Set<Router> unsettledRouters;
    private Map<Router, Router> predecessors;
    private Map<Router, Double> distance;
    private Connection c;
    
    public Dijkstra(Network network, Connection c) {
        net = network;
        this.c = c;
    }
    
    public Set<Integer> getPlausibleLambdas() {
        Set<Integer> lambdas = new HashSet<>();
        Router source = net.getRouter(c.getSource());
        List<Router> neighbors = this.getNeighbors(source);
        List<Integer> attFibersId = source.getAttachedFibers();
        List<Fiber> attFibers = new ArrayList<>();
        for (Integer fib : attFibersId) {
            attFibers.add(net.getFiber(fib));
        }
        for (Fiber fib : attFibers) {
            List<Lambda> lam = fib.getLambdas();
            for (Lambda l : lam) {
                if (l.getResidualBandwidth() > c.getBandwidth()) {
                    lambdas.add(l.getId());
                }
            }
        }
        return lambdas;

    }
    
    // Make a new LambdaConstraint Dijkstra, a loop for every possible lambda
    // of the attachedFibers of node source. For every one, we get the distance
    // to the destination and we finally choose the minimum one with
    // the path got from the algorithm.
    
    public void execute(Router source) {
        routers = net.getRouters();
        fibers = net.getFibers();
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

    // Might change neighbors to HashSet to delete insert variable
    
    private List<Router> getNeighbors(Router node) {
        List<Router> neighbors = new ArrayList<>();
        List<Integer> attFibersId = node.getAttachedFibers();
        List<Fiber> attFibers = new ArrayList<>();
        List<Lambda> lambdas;
        boolean insert;
        for (Integer fib : attFibersId) {
            attFibers.add(net.getFiber(fib));
        }
        for (Fiber fib : attFibers) {
            lambdas = fib.getLambdas();
            insert = false;
            for (Lambda lam : lambdas) {
                if (lam.getResidualBandwidth() >= c.getBandwidth()) {
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
            attFibers.add(net.getFiber(fib));
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
