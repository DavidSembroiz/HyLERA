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
   
    private final Network net;
    private Set<Router> settledRouters;
    private Set<Router> unsettledRouters;
    private List<Integer> plausibleLambdas;
    private Map<Router, Router> predecessors;
    private Map<Router, Double> distance;
    private Connection c;
    private boolean found;
    
    /**
     * Crea una nueva instancia de la clase Dijkstra que es la encargada
     * de toda la ejecucion del algoritmo para enrutar conexiones.
     * 
     * @param network red en la que se enrutaran las conexiones que vayan
     * entrando.
     */
    
    
    public Dijkstra(Network network) {
        net = network;
    }
    
    /**
     * Nucleo de todo el algoritmo, es la funcion encargada de iniciar la
     * busqueda de un camino para cada conexion.
     * 
     * @param source router de inicio de la conexion que se esta intentando
     * enrutar.
     * @param con conexion para la que se esta buscando un camino.
     */
    
    public void execute(Router source, Connection con) {
        this.c = con;
        net.increaseTotalConnections();
        net.increasePartialTotalConnections();
        LinkedList<Router> path = null;
        double minDistance = Double.MAX_VALUE;
        int finalLambda = net.LAMBDA_NOT_SETTLED;
        found = false;
        Fiber lp;
        lp = net.lightpathAvailable(c);
        if (lp != null) {
            net.assignLightpath(c, lp);
        }
        else {
            plausibleLambdas = net.getPlausibleLambdas(c);
            for (Iterator<Integer> it = plausibleLambdas.iterator(); it.hasNext();) {
                c.setLambda(it.next());
                settledRouters = new HashSet<>();
                unsettledRouters = new HashSet<>();
                distance = new HashMap<>();
                predecessors = new HashMap<>();
                distance.put(source, 0.0);
                unsettledRouters.add(source);
                while (!unsettledRouters.isEmpty()) {
                    Router node = getMinimum(unsettledRouters);
                    settledRouters.add(node);
                    unsettledRouters.remove(node);
                    findMinimalDistance(node);
                }
                if (this.getPath(net.getRouter(c.getDestination())) != null) {
                    if (minDistance > distance.get(net.getRouter(c.getDestination()))) {
                        found = true;
                        finalLambda = c.getLambda();
                        minDistance = distance.get(net.getRouter(c.getDestination()));
                        path = this.getPath(net.getRouter(c.getDestination()));
                    }
                }
            }
            if (!found) {
                c.setLambda(net.PATH_NOT_FOUND);
            }
            else {
                c.setLambda(finalLambda);
            }
            net.decreaseBandwidths(c, path);
        }
    }
    
    /**
     * Funcion auxiliar que actualiza las distancias de todos los nodos vecinos
     * al nodo de entrada.
     * 
     * @param node nodo actual al que se le estan calculando las distancias hasta
     * todos sus vecinos.
     */
    
    private void findMinimalDistance(Router node) {
        List<Router> adjacentRouters = getNeighbors(node);
        double dist, shortestDistance;
        for (Router neighbor : adjacentRouters) {
            shortestDistance = getShortestDistance(node);
            dist = getDistance(node, neighbor);
            if (shortestDistance < 0 || dist < 0 || getShortestDistance(neighbor) < 0) {
                System.out.println("Negative Distance");
            }
            if (getShortestDistance(neighbor) > shortestDistance + dist) {
                distance.put(neighbor, shortestDistance + dist);
                predecessors.put(neighbor, node);
                unsettledRouters.add(neighbor);
            }
        }
    }
    
    /**
     * Funcion auxiliar que devuelve el nodo con menor distancia de la lista
     * de posibles candidatos.
     * 
     * @param r conjunto de routers candidatos a formar parte del camino que se
     * esta buscando, el que tenga la distancia mas corta sera el elegido.
     */
    
    private Router getMinimum(Set<Router> r) {
        Router min = null;
        for (Router rou : r) {
            if (min == null) {
                min = rou;
            }
            else {
                if (getShortestDistance(rou) < getShortestDistance(min)) {
                    min = rou;
                }
            }
        }
        return min;
    }
    
    /**
     * Funcion auxiliar que devuelve la menor distancia obtenida hasta el momento
     * para el nodo de entrada.
     * 
     * @param rou nodo del que se obtiene la minima distancia para llegar a el
     * hasta el momento.
     */

    private double getShortestDistance(Router rou) {
        Double d = distance.get(rou);
        if (d == null) {
            return Double.MAX_VALUE;
        }
        return d;
    }
    
    /**
     * Funcion auxiliar que devuelve el extremo de la fibra no usado hasta el
     * momento.
     * 
     * @param f fibra utilzada para formar el camino de la conexion.
     * @param node nodo de inicio de la fibra utilizada.
     */
    
    private Router insertNeighbor(Fiber f, int node) {
        if (f.getNode1() == node) {
            return net.getRouter(f.getNode2());
        }
        else if (f.getNode2() == node) {
            return net.getRouter(f.getNode1());
        }
        return null;
    }
    
    /**
     * Funcion auxiliar que nos muestra si un nodo ya ha sido visitado o no.
     * 
     * @param f fibra que esta siendo analizada.
     * @param node uno de los dos extremos de la fibra analizada.
     */
    
    private boolean isSettled(Fiber f, int node) {
        if (f.getNode1() == node) {
            if (settledRouters.contains(net.getRouter(f.getNode2()))) {
                return true;
            }
        }
        else if (f.getNode2() == node) {
            if (settledRouters.contains(net.getRouter(f.getNode1()))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Funcion auxiliar que devuelve todos los vecinos del nodo de entrada
     * teniendo en cuenta una serie de restricciones:
     *  - No ha sido visitado todavia.
     *  - Podemos llegar a el pasando por una fibra con la lambda que estamos
     *    analizando en ese momento.
     *  - Tiene ancho de banda suficiente en esa lambda como para que la
     *    conexion actual pueda ser enrutada.
     * 
     * @param node nodo del que se estan buscando todos sus vecinos accesibles.
     */
    
    private List<Router> getNeighbors(Router node) {
        List<Lambda> lambdas;
        boolean insert;
        List<Router> neighbors = new ArrayList<>();
        List<Fiber> attFibers = net.getAttachedFibersById(node.getId());
        for (Fiber fib : attFibers) {
            lambdas = fib.getLambdas();
            insert = false;
            for (Iterator<Lambda> it = lambdas.iterator(); !insert && it.hasNext();) {
                Lambda lam = it.next();
                if (!isSettled(fib, node.getId()) && (-lam.getId() == c.getLambda() ||
                    lam.getId() == c.getLambda()) && 
                    lam.getResidualBandwidth() >= c.getBandwidth()) {
                    neighbors.add(insertNeighbor(fib, node.getId()));
                    insert = true;
                }
            }
        }
        return neighbors;
    }
    
    /**
     * Funcion auxiliar que devuelve la distancia entre los dos nodos de entrada
     * siguiendo el siguiente patron:
     *  - Se obtienen todas las posibles fibras que conectan los dos nodos.
     *  - Se descartan todas aquellas que no cumplen la restriccion de lambda
     *    y ancho de banda requeridos.
     *  - Entre las posibles candidatas restantes, se busca la que tiene
     *    menor distancia dependiendo del modo que se este utilizando.
     *  - Se devuelve la distancia final utilizada dependiendo del modo que se
     *    este utilizando.
     */
    
    private double getDistance(Router node, Router neighbor) {
        List<Integer> attFibersId = node.getAttachedFibers();
        Set<Fiber> plausibleFibers;
        List<Fiber> attFibers = new ArrayList<>();
        for (Integer fib : attFibersId) {
            if(fib <= net.ORIGINAL_FIBERS) {
                attFibers.add(net.getFiber(fib));
            }
            else {
                attFibers.add(net.getLightfiber(fib));
            }
        }
        plausibleFibers = net.getPlausibleFibers(attFibers, node.getId(), neighbor.getId());
        int id = net.getShortestFiberByWeight(plausibleFibers, c);
        
        if (net.MODE == 0) {
            if (id <= net.ORIGINAL_FIBERS) {
                return net.getFiber(id).getLambda(c.getLambda()).getWeight();
            }
            return net.getLightfiber(id).getLightLambda().getWeight();
        }
        else if (net.MODE == 1) {
            if (id <= net.ORIGINAL_FIBERS) {
                return net.getFiber(id).getLambda(c.getLambda()).getEnergeticWeight();
            }
            if (net.getLightfiber(id) == null) {
                System.out.println("Lightpath not found");
            }
            return net.getLightfiber(id).getLightLambda().getEnergeticWeight();
        }
        return Double.MAX_VALUE;
    }
    
    /**
     * Funcion auxiliar que devuelve el camino final obtenido para enrutar
     * la conexion.
     * 
     * @param node ultimo nodo que forma parte del camino de la conexion.
     */

    private LinkedList<Router> getPath(Router node) {
        LinkedList<Router> path = new LinkedList<>();
        Router step = node;
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        
        Collections.reverse(path);
        return path;
    }
}
