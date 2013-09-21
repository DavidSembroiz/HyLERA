package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Network {
    
        /*
         * Parametros que controlan el numero de dias y de pasos diarios que va
         * a ejecutar el algoritmo.
         * El valor referente al numero de pasos diarios se explica de la siguiente
         * manera:
         *  - Como vemos mas abajo, el numero de escalones que tiene una ejecucion
         *    es de 24. Utilizando el valor de 8640 nos aseguramos de que cada
         *    vez que se creen conexiones, habran pasado exactamente 10 segundos,
         *    que es un valor razonable.
         */
    
        private int DAYS = 1;
        private int TOTAL_STEPS = 8640*DAYS;
   
        /*
         * Modo de actuacion de la red:
         * - MODE 0: Blocking percentage aware.
         * - MODE 1: Energy aware.
         */

        public int MODE = 1;
        
        /*
         * Consumo de la red.
         */
        
        private double TOTAL_CONSUMPTION = 0;
        private double ACTUAL_CONSUMPTION = 0;
        
        /*
         * Valores auxiliares para diferentes errores.
         */
    
        public final int PATH_NOT_FOUND = -9999;
        public final int LAMBDA_NOT_SETTLED = -8888;
        
        /*
         * Numero de fibras y lambdas totales de la red.
         */
        
        public final int ORIGINAL_FIBERS = 53;
        private final double NUM_LAMBDAS = 2772.0;
        
        /*
         * Lista que representa los diferentes consumos de los routers,
         * estos valores seran asignados por rangos una vez ordenados los routers
         * por tamaño. A menor consumo, el router sera mas grande. Este consumo
         * equivale a W/GB.
         */
        
        private final double[] ROUTER_CONSUMPTION = {1, 1.5, 2, 3.5, 5, 6, 8, 9, 10};
        
        /*
         * Parametros que representan el numero de conexiones que se deben intentar
         * enrutar en cada momento, cada valor de la lista equivale a una hora del dia
         * y ese valor sera multiplicado por N para obtener el numero de conexiones
         * por paso que deben intentar ser enrutadas-
         */
        
        private final int[] CONNECTION_SLOPE = 
            {1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        private int CONNECTION_SLOPE_IDX = 0;
        private int CONNECTION_N = 10;
        
        /*
         * Listas para guardar la probabilidad de aparacion de un nodo en una nueva
         * conexion. Con esto nos aseguramos una mejor gestion de la red al hacer
         * que los nodos con mayor numero de lambdas sean mas utilizados.
         */
        
        private double[] NODE_PROBABILITY;
        private double[] NODE_SUM;
        

	private List<Router> routers;
	private List<Fiber> fibers;
        private List<Lightpath> lightpaths;
	private Set<Connection> enrutedConnections;
        private int blockedConnections;
        private int partialBlockedConnections;
        private int totalConnections;
        private int partialTotalConnections;
        private int numFibers;
        private int connectionIndex;
	
	
        public Network() {
            generateNetwork();
        }
        
	private void generateNetwork() {
            enrutedConnections = new HashSet<>();
            blockedConnections = 0;
            partialBlockedConnections = 0;
            totalConnections = 0;
            partialTotalConnections = 0;
            connectionIndex = 0;
            numFibers = 53;
            generateFibers();
            generateRouters();
            setTotalRouterBandwidths();
            generateLambdas();
            calculateNumLambdas();
            calculateNodeSum();
	}
        
        /************************************************************/
        /*                   Getters y setters                      */
        /************************************************************/
        
        public double getActualConsumption() {
            return this.ACTUAL_CONSUMPTION;
        }
        public double getTotalConsumption() {
            return this.TOTAL_CONSUMPTION;
        }
        public double getBlockingPercentaje() {
            return ((double)blockedConnections/totalConnections)*100;
        }
        public List<Router> getRouters() {
            return routers;
        }
        public Router getRouter(int id) {
            return routers.get(id - 1);
        }
        public List<Fiber> getFibers() {
            return fibers;
        }
        public Fiber getFiber(int id) {
            return fibers.get(id - 1);
        }
        public int getDays() {
            return DAYS;
        }
        public int getTotalSteps() {
            return TOTAL_STEPS;
        }
       
        /************************************************************/
        
        private ArrayList<Connection> readConnectionsFromFile(int step, int lines) {
            ArrayList<Connection> cons = new ArrayList();
            try {
                FileInputStream fstream = new FileInputStream("connections.txt");
                try (DataInputStream in = new DataInputStream(fstream)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    boolean found = false;
                    while (!found && (strLine = br.readLine()) != null) {
                        String[] l = strLine.split(" ");
                        if ("STEP".equals(l[0])) {
                            if (Integer.parseInt(l[1]) == step) {
                                found = true;
                                while (lines-- > 0 && (strLine = br.readLine()) != null) {
                                    String[] c = strLine.split(" ");
                                    Connection con = new Connection(Integer.parseInt(c[0]),
                                                        Integer.parseInt(c[1]),
                                                        Double.parseDouble(c[2]),
                                                        Integer.parseInt(c[3]),
                                                        Integer.parseInt(c[4]));
                                    cons.add(con);
                                }
                            }
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                    System.err.println("Error: " + e.getMessage());
            }
            return cons;
        }
        
        private ArrayList<Connection> readRawConnectionsFromFile(int n) {
            ArrayList<Connection> cons = new ArrayList();
            try {
                FileInputStream fstream = new FileInputStream("raw.txt");
                try (DataInputStream in = new DataInputStream(fstream)) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        String[] c = strLine.split(" ");
                        Connection con = new Connection(Integer.parseInt(c[0]),
                                                        Integer.parseInt(c[1]),
                                                        Double.parseDouble(c[2]),
                                                        Integer.parseInt(c[3]),
                                                        Integer.parseInt(c[4]));
                        cons.add(con);
                   }
                }
            } catch (IOException | NumberFormatException e) {
                    System.err.println("Error: " + e.getMessage());
            }
            return cons;
        }
        
        
        private void writeConnectionsToFile(int step, ArrayList<Connection> c) throws IOException {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("connections.txt", true)))) {
                writer.println("STEP " + step);
                for (Connection con : c) {
                    writer.println(Integer.toString(con.getId()) + " " + Integer.toString(con.getTimeToLive()) + " " + 
                                   Double.toString(con.getBandwidth()) + " " + Integer.toString(con.getSource())+ " " +
                                   Integer.toString(con.getDestination()));
                }
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public ArrayList<Connection> generateRawConnectionsFromFile(int n) {
            ArrayList<Connection> cons;
            cons = this.readRawConnectionsFromFile(n);
            return cons;
        }
        
        private ArrayList<Connection> generateRawConnections(int n) {
            ArrayList<Connection> cons = new ArrayList<>();
            for (int i = 0; i < n; ++i) {
                int ttl = 1; // 250 for 12000 steps (half an hour)
                int source = getNode();
                int destination = getNode();
                while (source == destination) {
                    destination = getNode();
                }
                double bw = 310; // 45, 155, 310
                int idx = getNextIndex();
                Connection c = new Connection(idx, ttl, bw, source, destination);
                cons.add(c);
            }
            return cons;
        }
        
        private void writeRawConnectionsToFile(ArrayList<Connection> cons) throws IOException {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("raw.txt", true)))) {
                for (Connection con : cons) {
                    writer.println(Integer.toString(con.getId()) + " " + Integer.toString(con.getTimeToLive()) + " " + 
                                   Double.toString(con.getBandwidth()) + " " + Integer.toString(con.getSource())+ " " +
                                   Integer.toString(con.getDestination()));
                }
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void createRawConnectionsFile(int n) {
            File f = new File("raw.txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ArrayList<Connection> cons = generateRawConnections(n);
            try {
                writeRawConnectionsToFile(cons);
            } catch (IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void createConnectionsFile(int step) {
            File f = new File("connections.txt");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ArrayList<Connection> cons = generateConnections();
            try {
                this.writeConnectionsToFile(step, cons);
            } catch (IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public ArrayList<Connection> generateConnectionsFromFile(int step) {
            ArrayList<Connection> cons;
            //int slope = CONNECTION_SLOPE[(CONNECTION_SLOPE_IDX++)%CONNECTION_SLOPE.length];
            int slope = CONNECTION_SLOPE[((int)CONNECTION_SLOPE_IDX++/(TOTAL_STEPS/(24*DAYS)))%CONNECTION_SLOPE.length];
            int lines = CONNECTION_N * slope;
            cons = this.readConnectionsFromFile(step, lines);
            return cons;
        }
        
        private void calculateNumLambdas() {
            int sum;
            this.NODE_PROBABILITY = new double[34];
            for (int i = 0; i < this.routers.size(); ++i) {
                sum = 0;
                List<Integer>  fib = this.routers.get(i).getAttachedFibers();
                for (int j = 0; j < fib.size(); ++j) {
                    sum += this.getFiber(fib.get(j)).getNumLambdas();
                }
                this.NODE_PROBABILITY[i] = sum / NUM_LAMBDAS;
            }
        }
        
        private void calculateNodeSum() {
            this.NODE_SUM = new double[34];
            this.NODE_SUM[0] = this.NODE_PROBABILITY[0];
            for (int i = 1; i < this.NODE_PROBABILITY.length; ++i) {
                this.NODE_SUM[i] = this.NODE_SUM[i - 1] + this.NODE_PROBABILITY[i];
            }
        }
        
        public void printNodeDistribution() {
            for (int i = 0; i < this.NODE_PROBABILITY.length; ++i) {
                System.out.println(this.routers.get(i).getName() + " "
                                   + NODE_PROBABILITY[i] + " "
                                   + NODE_SUM[i]);
            }
        }
        
        private int getNode() {
            double rnd = Math.random();
            for (int i = 0; i < this.NODE_SUM.length; ++i) {
                if (rnd <= this.NODE_SUM[i]) {
                    return i + 1;
                }
            }
            return this.PATH_NOT_FOUND;
        }

        
        /** CONNECTION_SLOPE_IDX does not have increment here because it is executed always
         * before a generateConnectionsFromFile() function.
         */
        
        public ArrayList<Connection> generateConnections() {
            ArrayList<Connection> cons = new ArrayList<>();
            //int slope = CONNECTION_SLOPE[(CONNECTION_SLOPE_IDX++)%CONNECTION_SLOPE.length];
            int slope = CONNECTION_SLOPE[((int)CONNECTION_SLOPE_IDX/(TOTAL_STEPS/(24*DAYS)))%CONNECTION_SLOPE.length]; // steps/24h for division
            for (int i = 0; i < CONNECTION_N * slope; ++i) {
                int ttl = (TOTAL_STEPS/(24*DAYS))/2; // 250 for 12000 steps (half an hour)
                int source = getNode();
                int destination = getNode();
                while (source == destination) {
                    destination = getNode();
                }
                double bw = 310; // 45, 155, 310
                int idx = getNextIndex();
                Connection c = new Connection(idx, ttl, bw, source, destination);
                cons.add(c);
            }
            return cons;
        }
        
        private int getNextIndex() {
            return ++connectionIndex;
        }
        private int getNextNumFiber() {
            return ++this.numFibers;
        }
        
        
        public Fiber getLightfiber(int id) {
            for (Lightpath l : lightpaths) {
                if (l.getLightfiber().getId() == id) {
                    return l.getLightfiber();
                }
            }
            return null;
        }
        
        private Lightpath getLightpath(int id) {
            for (Lightpath l : this.lightpaths) {
                if (id == l.getLightfiber().getId()) {
                    return l;
                }
            }
            return null;
        }
        
        public Set<Fiber> getPlausibleFibers(List<Fiber> fibs, int source, int destination) {
            Set<Fiber> res = new HashSet();
            for (Fiber fib : fibs) {
                if ((fib.getNode1() == source && fib.getNode2() == destination) ||
                    (fib.getNode2() == source && fib.getNode1() == destination)) {
                    res.add(fib);
                }
            }
            return res;
        }
        
        public int getShortestFiberByWeight(Set<Fiber> plausibleFibers, Connection c) {
            double res = Double.MAX_VALUE;
            int id = Integer.MAX_VALUE;
            for (Fiber fib : plausibleFibers) {
                List<Lambda> lambdas = fib.getLambdas();
                for (Lambda lam : lambdas) {
                    if (-lam.getId() == c.getLambda() || lam.getId() == c.getLambda()) {
                        if (lam.getResidualBandwidth() >= c.getBandwidth())  {
                            if (MODE == 0) {
                                if (res > lam.getWeight()) {
                                    res = lam.getWeight();
                                    id = fib.getId();
                                }
                                else if (res == lam.getWeight()) {
                                    if (id > fib.getId()) {
                                        id = fib.getId();
                                    }
                                }
                            }
                            else if (MODE == 1) {
                                if (res > lam.getEnergeticWeight()) {
                                    res = lam.getEnergeticWeight();
                                    id = fib.getId();
                                }
                                else if (res == lam.getEnergeticWeight()) {
                                    if (id > fib.getId()) {
                                        id = fib.getId();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return id;
        }
	

        public int findFiber(int source, int destination, Connection c) {
            Set<Fiber> plausibleFibers = this.getPlausibleFibers(fibers, source, destination);
            return this.getShortestFiberByWeight(plausibleFibers, c);
        }
        
        public int findOriginalFiber(int source, int destination) {
            for (Fiber fib : fibers) {
                if ((fib.getNode1() == source && fib.getNode2() == destination) ||
                    (fib.getNode2() == source && fib.getNode1() == destination) &&
                     fib.getId() <= ORIGINAL_FIBERS) {
                    return fib.getId();
                }
            }
            return PATH_NOT_FOUND;
        }


        public Set<Connection> getEnrutedConnections() {
            return enrutedConnections;
        }

        public void setEnrutedConnections(Set<Connection> enrutedConnections) {
            this.enrutedConnections = enrutedConnections;
        }
        
        public int getBlockedConnections() {
            return blockedConnections;
        }
        
        public void setBlockedConnections(int s) {
            this.blockedConnections = s;
        }
        
        public int getPartialBlockedConnections() {
            return partialBlockedConnections;
        }
        
        public void setPartialBlockedConnections(int s) {
            this.partialBlockedConnections = s;
        }
        
        public void setTotalConnections(int s) {
            this.totalConnections = s;
        }
        
        public int getTotalConnections() {
            return totalConnections;
        }
        
        public void setPartialTotalConnections(int s) {
            this.partialTotalConnections = s;
        }
        
        public int getPartialTotalConnections() {
            return partialTotalConnections;
        }
        
        public void increaseTotalConnections() {
            this.totalConnections++;
        }
        
        public void increasePartialTotalConnections() {
            this.partialTotalConnections++;
        }
        
        public List<Fiber> getAttachedFibersById(int id) {
            Router source = getRouter(id);
            List<Integer> attFibersId = source.getAttachedFibers();
            List<Fiber> attFibers = new ArrayList<>();
            for (Integer fib : attFibersId) {
                if(fib <= ORIGINAL_FIBERS) {
                    attFibers.add(getFiber(fib));
                }
                else {
                    attFibers.add(getLightfiber(fib));
                }
            }
            return attFibers;
        }
        
        public List<Integer> getPlausibleLambdas(Connection c) {
            List<Integer> lambdas = new ArrayList<>();
            List<Fiber> attFibers = this.getAttachedFibersById(c.getSource());
            for (Fiber fib : attFibers) {
                List<Lambda> lam = fib.getLambdas();
                for (Lambda l : lam) {
                    if (l.getResidualBandwidth() >= c.getBandwidth()) {
                        if (l.getId() < 0) {
                            if (!lambdas.contains(-l.getId())) {
                                lambdas.add(-l.getId());
                            }
                        }
                        else {
                            if (!lambdas.contains(l.getId())) {
                                lambdas.add(l.getId());
                            }
                        }
                    }
                }
            }
            /**
             * Reverse the lambda list to get first the higher ones, seems
             * to work better without reversing.
             */
            
            Collections.reverse(lambdas);
            return lambdas;
        }
        
        public void decreaseTimesToLive() {
            if (this.enrutedConnections.isEmpty()) {
                return;
            }
            Connection con;
            Iterator<Connection> it = this.enrutedConnections.iterator(); 
            while (it.hasNext()){
                con = it.next();
                con.setTimeToLive(con.getTimeToLive() - 1);
                if (con.getTimeToLive() < 0) {
                    this.ACTUAL_CONSUMPTION -= con.getConsumption();
                    increaseLightpath(con);
                    it.remove();
                }
            }
        }

        private void computeConnectionConsumption(Connection c) {
            double cons = 0;
            for (Integer i : c.getLightpathFibers()) {
                LinkedList<Router> path = this.getLightpath(i).getPath();
                Router source = path.getFirst();
                for (Router r : path.subList(1, path.size())) {
                    Router destination = r;
                    int idx = this.findOriginalFiber(source.getId(), destination.getId());
                    
                    cons += source.getConsumption();
                    cons += this.getFiber(idx).getLambda(c.getLambda()).getLongConsumption();
                    source = destination;
                    
                    if (i == c.getLightpathFibers().get(c.getLightpathFibers().size() - 1) &&
                        destination.getId() == path.getLast().getId()) {
                        cons += destination.getConsumption();
                    }
                }
            }
            c.setConsumption(cons*c.getBandwidth()/1000);
            if (c.getConsumption() == 0) {
                System.out.println("Wrong consumption");
            }
            this.TOTAL_CONSUMPTION += c.getConsumption();
            this.ACTUAL_CONSUMPTION += c.getConsumption();
        }
        
        
        public void decreaseBandwidths(Connection c, LinkedList<Router> path) {
            LinkedList<Router> physicalPath = new LinkedList<>();
            Router source;
            boolean init = true;
            if (c.getLambda() == PATH_NOT_FOUND) {
                ++partialBlockedConnections;
                ++blockedConnections;
            }
            else {
                this.enrutedConnections.add(c);
                Iterator<Router> it = path.iterator();
                source = it.next();
                double minBW = Double.MAX_VALUE;
                int distance = 0;
                
                while (it.hasNext()) {
                    if (init) {
                        init = false;
                        physicalPath.add(source);
                    }
                    Router destination = it.next();
                    int f = this.findFiber(source.getId(), destination.getId(), c);
                    if (f > this.ORIGINAL_FIBERS) {
                        Fiber lf = this.getLightfiber(f);
                        lf.decreaseLightBandwidth(c.getBandwidth());
                        lf.actualizeLightLambdaWeight(
                                lf.getLightLambda().getResidualBandwidth(),
                                lf.getTotalBandwidth());
                        if (physicalPath.size() > 1) {
                            createLightpath(c, physicalPath, minBW, distance);
                            distance = 0;
                        }
                        init = true;
                        physicalPath.clear();
                        c.addLightpathFiber(f);
                    }
                    else {
                        Fiber fi = this.getFiber(f);
                        if (fi.getTotalBandwidth() < minBW) {
                            minBW = fi.getTotalBandwidth();
                        }
                        distance += fi.getLength();
                        physicalPath.add(destination);
                        fi.decreaseBandwidth(fi.getTotalBandwidth(), c.getLambda());
                        fi.actualizeLambdaWeight(c.getLambda(),
                            fi.getLambda(c.getLambda()).getResidualBandwidth(),
                            fi.getTotalBandwidth());
                        fi.setInfinityLambdaEnergeticWeight(c.getLambda());
                        if (fi.getLambda(c.getLambda()).getEnergeticWeight() != fi.getLambda(c.getLambda()).getWeight()) {
                            System.out.println("Wrong Weights");
                        }
                    }
                    source = destination;
                }
                if (physicalPath.size() > 1) {
                    createLightpath(c, physicalPath, minBW, distance);
                    physicalPath.clear();
                }
                this.computeConnectionConsumption(c);
            }
        }
        
        public void increaseBandwidths(Connection c, LinkedList<Router> path) {
            Iterator<Router> it = path.iterator();
            Router source = it.next();
            while (it.hasNext()) {
                Router destination = it.next();
                int f = this.findOriginalFiber(source.getId(), destination.getId());
                Fiber fi = this.getFiber(f);
                fi.increaseBandwidth(fi.getTotalBandwidth(), c.getLambda());
                fi.actualizeLambdaWeight(c.getLambda(),
                            fi.getLambda(c.getLambda()).getResidualBandwidth(),
                            fi.getTotalBandwidth());
                fi.actualizeLambdaEnergeticWeight(c.getLambda(),
                                                  getRouter(fi.getNode1()).getConsumption(), 
                                                  getRouter(fi.getNode2()).getConsumption(),
                                                  fi.getLength());
                    source = destination;
                }
        }

        
        public Fiber lightpathAvailable(Connection c) {
            for (Lightpath light : lightpaths) {
                Fiber f = light.getLightfiber();
                if ((f.getNode1() == c.getSource() && f.getNode2() == c.getDestination()
                    || (f.getNode2() == c.getSource() && f.getNode1() == c.getDestination()))
                    && f.getLambdas().get(0).getResidualBandwidth() >= c.getBandwidth()) {
                    return f;
                }
                    
            }
            return null;
        }
        
        public void assignLightpath(Connection c, Fiber f) {
            this.enrutedConnections.add(c);
            c.setLambda(-f.getLightLambda().getId());
            c.addLightpathFiber(f.getId());
            f.getLightLambda().decreaseBandwidth(c.getBandwidth());
            f.actualizeLightLambdaWeight(f.getLightLambda().getResidualBandwidth(), f.getTotalBandwidth());
        }
        
        private int getAvailableId() {
            for (int i = 54; i < Integer.MAX_VALUE; ++i) {
                boolean found = false;
                for (Iterator<Lightpath> it = this.lightpaths.iterator(); !found && it.hasNext();) {
                    Lightpath l = it.next();
                    if (l.getLightfiber().getId() == i) {
                        found = true;
                    }
                }
                if (!found) {
                    return i;
                }
            }
            return Integer.MAX_VALUE;
        }
        
        
        private void createLightpath(Connection c, LinkedList<Router> p, double bw, int dist) {
            LinkedList<Router> path = new LinkedList<>();
            for (Router r : p) {
                path.add(r);
            }
            int source = path.get(0).getId();
            int destination = path.get(path.size() - 1).getId();
            int numFiber = getAvailableId();
            //int numFiber = getNextNumFiber();
            Fiber f = new Fiber(numFiber, source, destination,
                                1, bw, dist);
            
            Lambda l = new Lambda(-c.getLambda(), bw - c.getBandwidth(), 0);
            l.actualizeWeight(bw - c.getBandwidth(), bw);
            l.actualizeEnergeticWeight(getRouter(source).getConsumption(), getRouter(destination).getConsumption(), dist);
            List<Lambda> lams = new ArrayList<>();
            lams.add(l);
            f.setLambdas(lams);
            routers.get(source - 1).addAttachedFiber(numFiber);
            routers.get(destination - 1).addAttachedFiber(numFiber);
            Lightpath light = new Lightpath(path, f);
            c.addLightpathFiber(numFiber);
            fibers.add(f);
            lightpaths.add(light);
        }
        
        private void increaseLightpath(Connection c) {
            boolean delete;
            Iterator<Integer> ids = c.getLightpathFibers().iterator();
            while(ids.hasNext()) {
                delete = false;
                int id = ids.next();
                Iterator<Lightpath> lps = lightpaths.iterator();
                while (!delete && lps.hasNext()) {
                    Fiber f = lps.next().getLightfiber();
                    if (id == f.getId()) {
                        f.increaseLightBandwidth(c.getBandwidth());
                        f.actualizeLightLambdaWeight(f.getLightLambda().getResidualBandwidth(), f.getTotalBandwidth());
                        if (f.getLightLambda().getResidualBandwidth() == f.getTotalBandwidth()) {
                            delete = true;
                        }
                    }
                }
                if (delete) {
                    deleteLightpath(c, id);
                }
            }
        }
        
        private void deleteLightpath(Connection c, int lightfiber) {
            Lightpath lf = null;
            for (Lightpath i : lightpaths) {
                if (i.getLightfiber().getId() == lightfiber) {
                    lf = i;
                }
            }
            if (lf != null) {
                Fiber rem = lf.getLightfiber();
                Router source = this.getRouter(rem.getNode1());
                Router destination = this.getRouter(rem.getNode2());
                source.getAttachedFibers().remove((Integer) rem.getId());
                destination.getAttachedFibers().remove((Integer) rem.getId());
                increaseBandwidths(c, lf.getPath());

                fibers.remove(rem);
                lightpaths.remove(lf);
            }
            else {
                System.out.println("Should not happen");
            }
        }
        
        private void generateRouters() {
                routers = new ArrayList<>();
                lightpaths = new ArrayList<>();
		routers.add(new Router(1, "PT", ROUTER_CONSUMPTION[7], generateAttachedFibers(1)));
		routers.add(new Router(2, "ES", ROUTER_CONSUMPTION[3], generateAttachedFibers(2)));
		routers.add(new Router(3, "IS", ROUTER_CONSUMPTION[8], generateAttachedFibers(3)));
		routers.add(new Router(4, "IE", ROUTER_CONSUMPTION[8], generateAttachedFibers(4)));
		routers.add(new Router(5, "UK", ROUTER_CONSUMPTION[0], generateAttachedFibers(5)));
		routers.add(new Router(6, "FR", ROUTER_CONSUMPTION[2], generateAttachedFibers(6)));
		routers.add(new Router(7, "NL", ROUTER_CONSUMPTION[1], generateAttachedFibers(7)));
		routers.add(new Router(8, "BE", ROUTER_CONSUMPTION[4], generateAttachedFibers(8)));
		routers.add(new Router(9, "LU", ROUTER_CONSUMPTION[7], generateAttachedFibers(9)));
		routers.add(new Router(10, "CH", ROUTER_CONSUMPTION[0], generateAttachedFibers(10)));
		
		routers.add(new Router(11, "NO", ROUTER_CONSUMPTION[3], generateAttachedFibers(11)));
		routers.add(new Router(12, "DE", ROUTER_CONSUMPTION[0], generateAttachedFibers(12)));
		routers.add(new Router(13, "IT", ROUTER_CONSUMPTION[1], generateAttachedFibers(13)));
		routers.add(new Router(14, "DK", ROUTER_CONSUMPTION[0], generateAttachedFibers(14)));
		routers.add(new Router(15, "MT", ROUTER_CONSUMPTION[8], generateAttachedFibers(15)));
		routers.add(new Router(16, "SE", ROUTER_CONSUMPTION[0], generateAttachedFibers(16)));
		routers.add(new Router(17, "CZ", ROUTER_CONSUMPTION[1], generateAttachedFibers(17)));
		routers.add(new Router(18, "SI", ROUTER_CONSUMPTION[3], generateAttachedFibers(18)));
		routers.add(new Router(19, "PL", ROUTER_CONSUMPTION[4], generateAttachedFibers(19)));
		routers.add(new Router(20, "AT", ROUTER_CONSUMPTION[0], generateAttachedFibers(20)));
		
		routers.add(new Router(21, "HR", ROUTER_CONSUMPTION[3], generateAttachedFibers(21)));
		routers.add(new Router(22, "SK", ROUTER_CONSUMPTION[2], generateAttachedFibers(22)));
		routers.add(new Router(23, "HU", ROUTER_CONSUMPTION[3], generateAttachedFibers(23)));
		routers.add(new Router(24, "FI", ROUTER_CONSUMPTION[4], generateAttachedFibers(24)));
		routers.add(new Router(25, "EE", ROUTER_CONSUMPTION[8], generateAttachedFibers(25)));
		routers.add(new Router(26, "LV", ROUTER_CONSUMPTION[8], generateAttachedFibers(26)));
		routers.add(new Router(27, "LT", ROUTER_CONSUMPTION[8], generateAttachedFibers(27)));
		routers.add(new Router(28, "RO", ROUTER_CONSUMPTION[4], generateAttachedFibers(28)));
		routers.add(new Router(29, "BG", ROUTER_CONSUMPTION[4], generateAttachedFibers(29)));
		routers.add(new Router(30, "GR", ROUTER_CONSUMPTION[3], generateAttachedFibers(30)));
		
		routers.add(new Router(31, "TR", ROUTER_CONSUMPTION[7], generateAttachedFibers(31)));
		routers.add(new Router(32, "RU", ROUTER_CONSUMPTION[7], generateAttachedFibers(32)));
		routers.add(new Router(33, "CY", ROUTER_CONSUMPTION[7], generateAttachedFibers(33)));
		routers.add(new Router(34, "IL", ROUTER_CONSUMPTION[8], generateAttachedFibers(34)));
	}

	private void generateFibers() {
                fibers = new ArrayList<>();
		fibers.add(new Fiber(1, 3, 14, 4, 310, 2100));
		fibers.add(new Fiber(2, 12, 34, 8, 2500, 2900));
		fibers.add(new Fiber(3, 7, 15, 1, 45, 1970));
		fibers.add(new Fiber(4, 28, 31, 8, 2500, 750));
		fibers.add(new Fiber(5, 14, 32, 8, 2500, 1560));
		fibers.add(new Fiber(6, 14, 25, 16, 10000, 830));
		fibers.add(new Fiber(7, 25, 26, 16, 10000, 280));
		fibers.add(new Fiber(8, 26, 27, 16, 10000, 260));
		fibers.add(new Fiber(9, 19, 27, 16, 10000, 395));
		fibers.add(new Fiber(10, 17, 19, 16, 10000, 515));
		
		fibers.add(new Fiber(11, 12, 19, 16, 10000, 506));
		fibers.add(new Fiber(12, 12, 32, 8, 2500, 1600));
		fibers.add(new Fiber(13, 12, 20, 16, 10000, 525));
		fibers.add(new Fiber(14, 9, 12, 16, 10000, 600));
		fibers.add(new Fiber(15, 6, 9, 16, 10000, 280));
		fibers.add(new Fiber(16, 2, 6, 16, 10000, 1050));
		fibers.add(new Fiber(17, 1, 2, 16, 10000, 505));
		fibers.add(new Fiber(18, 1, 5, 8, 2500, 1580));
		fibers.add(new Fiber(19, 4, 5, 16, 0, 460));
		fibers.add(new Fiber(20, 29, 30, 16, 10000, 595));
                
                fibers.add(new Fiber(21, 29, 31, 8, 2500, 850));
                fibers.add(new Fiber(22, 13, 33, 2, 155, 2000));
                fibers.add(new Fiber(23, 28, 29, 16, 10000, 301));
                fibers.add(new Fiber(24, 13, 15, 1, 45, 685));
                fibers.add(new Fiber(25, 13, 30, 16, 10000, 1060));
                fibers.add(new Fiber(26, 20, 30, 16, 10000, 1280));
                fibers.add(new Fiber(27, 23, 28, 16, 10000, 650));
                fibers.add(new Fiber(28, 23, 29, 16, 10000, 630));
                fibers.add(new Fiber(29, 30, 33, 2, 155, 910));
                fibers.add(new Fiber(30, 2, 13, 16, 10000, 1360));
                
                fibers.add(new Fiber(31, 4, 5, 16, 10000, 460));
                fibers.add(new Fiber(32, 5, 6, 48, 10000, 350));
                fibers.add(new Fiber(33, 5, 8, 48, 10000, 325));
                fibers.add(new Fiber(34, 7, 8, 64, 10000, 170));
                fibers.add(new Fiber(35, 7, 14, 32, 10000, 620));
                fibers.add(new Fiber(36, 7, 12, 48, 10000, 580));
                fibers.add(new Fiber(37, 12, 14, 32, 10000, 360));
                fibers.add(new Fiber(38, 10, 12, 64, 10000, 750));
                fibers.add(new Fiber(39, 6, 10, 48, 10000, 440));
                fibers.add(new Fiber(40, 2, 10, 32, 10000, 1150));
                
                fibers.add(new Fiber(41, 10, 13, 48, 10000, 390));
                fibers.add(new Fiber(42, 13, 20, 32, 10000, 765));
                fibers.add(new Fiber(43, 18, 20, 64, 10000, 280));
                fibers.add(new Fiber(44, 18, 21, 48, 10000, 120));
                fibers.add(new Fiber(45, 21, 23, 32, 10000, 300));
                fibers.add(new Fiber(46, 22, 23, 48, 10000, 165));
                fibers.add(new Fiber(47, 20, 22, 64, 10000, 60));
                fibers.add(new Fiber(48, 17, 22, 64, 10000, 450));
                fibers.add(new Fiber(49, 12, 17, 32, 10000, 280));
                fibers.add(new Fiber(50, 11, 14, 32, 10000, 480));
                
                fibers.add(new Fiber(51, 14, 16, 32, 10000, 520));
                fibers.add(new Fiber(52, 16, 24, 64, 10000, 400));
                fibers.add(new Fiber(53, 11, 16, 32, 10000, 420));
	}
        
        private void generateLambdas() {
            for (Fiber fib : fibers) {
                List<Lambda> l = new ArrayList<>();
                for (int i = 0; i < fib.getNumLambdas(); ++i) {
                    Lambda lamb = new Lambda(i + 1);
                    lamb.setResidualBandwidth(fib.getTotalBandwidth());
                    actualizeWeights(fib, lamb);
                    l.add(lamb);
                }
                fib.setLambdas(l);
            }
        }
        
        private void actualizeWeights(Fiber f, Lambda l) {
            l.actualizeWeight(f.getTotalBandwidth(), f.getTotalBandwidth());
            l.actualizeEnergeticWeight(getRouter(f.getNode1()).getConsumption(),
                                       getRouter(f.getNode2()).getConsumption(),
                                       f.getLength());
        }
        
        
        private void setTotalRouterBandwidths() {
            for (Router r : routers) {
                for (Integer b : r.getAttachedFibers()) {
                    Fiber f = this.getFiber(b);
                    r.increaseTotalBandwidth(f.getTotalBandwidth()*f.getNumLambdas());
                }
            }
        }
        

        private List<Integer> generateAttachedFibers(int source) {
            List<Integer> attFibersId = new ArrayList<>();
            for (Fiber fib : fibers) {
                if (fib.getNode1() == source || fib.getNode2() == source) {
                    attFibersId.add(fib.getId());
                }
            }
            return attFibersId;
        }
        
        public void printNetwork() {
            for (Router r : routers) {
                printRouter(r);
                for (Integer i : r.getAttachedFibers()) {
                    printFiber(getFiber(i));
                    for (Lambda l : getFiber(i).getLambdas()) {
                        printLambda(l);
                    }
                }
            }
        }
        
        public void deleteFiles() throws IOException {
            File f = new File("result.txt");
            f.delete();
        }
        
        public void printConnectionToFile(Connection c) throws IOException {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("result.txt", true)))) {
                writer.println("------------------------------------------");
                writer.println("Connection id: " + Integer.toString(c.getId()));
                writer.println("Time to live: " + Integer.toString(c.getTimeToLive()));
                writer.println("Source: " + Integer.toString(c.getSource()));
                writer.println("Destination: " + Integer.toString(c.getDestination()));
                writer.println("Lambda: " + Integer.toString(c.getLambda()));
                writer.println("Bandwidth: " + Double.toString(c.getBandwidth()));
                writer.println("LightPath Fibers: " + c.getLightpathFibers());
                for (Integer i : c.getLightpathFibers()) {
                    writer.println(i);
                    writer.println(this.getRouter(this.getLightpath(i).getLightfiber().getNode1()).getName());
                    LinkedList<Router> path = this.getLightpath(i).getPath();
                    for (Router r : path) {
                        writer.println("-----" + r.getName());
                    }
                    writer.println(this.getRouter(this.getLightpath(i).getLightfiber().getNode2()).getName());
                }
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void printConnection(Connection c) {
            System.out.println("------------------------------------------");
            System.out.println("Connection id: " + c.getId());
            System.out.println("Time to live: " + c.getTimeToLive());
            System.out.println("Source: " + c.getSource());
            System.out.println("Destination: " + c.getDestination());
            System.out.println("Lambda: " + c.getLambda());
            System.out.println("Bandwidth: " + c.getBandwidth());
            System.out.println("LightPath Fibers: " + c.getLightpathFibers());
            for (Integer i : c.getLightpathFibers()) {
                System.out.println(i);
                System.out.println(this.getRouter(this.getLightpath(i).getLightfiber().getNode1()).getName());
                LinkedList<Router> path = this.getLightpath(i).getPath();
                for (Router r : path) {
                    System.out.println("-----" + r.getName());
                }
                System.out.println(this.getRouter(this.getLightpath(i).getLightfiber().getNode2()).getName());
            }
        }
        
        public void printRouter(Router r) {
            System.out.println("");
            System.out.println("Router " + r.getId() + " " + r.getName());
            System.out.println("--------------------------------");
        }
        
        public void printFiber(Fiber fib) {
            System.out.println("");
            System.out.println("Fiber " + fib.getId() + "  Total BW " + fib.getTotalBandwidth());
            System.out.println("------------------------- Lambdas");
        }
        
        public void printLambda(Lambda l) {
            System.out.println(l.getId() + "     Residual BW " + l.getResidualBandwidth() + "     Weight " + l.getWeight());
        }
        
        int tam = (TOTAL_STEPS/(24*DAYS)); // steps/24h (500 for 12000)
        
        int[] bloqueos = new int[tam];
        int[] totales = new int[tam];
        boolean full = false;
        int index = 0;
        
        public double calculateBlock(int act, int tot) {
            if (!full) {
                bloqueos[index] = act;
                totales[index++] = tot;
                if (index == tam) full = true;
            }
            else {
                for (int i = 1; i < bloqueos.length; ++i) {
                    bloqueos[i - 1] = bloqueos[i];
                    totales[i - 1] = totales[i];
                }
                bloqueos[bloqueos.length - 1] = act;
                totales[totales.length - 1] = tot;
            }
            double num = 0;
            double den = 0;
            for (int i = 0; i < index; ++i) {
                num += bloqueos[i];
                den += totales[i];
            }
            return (num/den)*100;
        }
        
        public void insertPartialData(int act, int tot) {
            if (!full) {
                bloqueos[index] = act;
                totales[index++] = tot;
                if (index == tam) full = true;
            }
            else {
                for (int i = 1; i < bloqueos.length; ++i) {
                    bloqueos[i - 1] = bloqueos[i];
                    totales[i - 1] = totales[i];
                }
                bloqueos[bloqueos.length - 1] = act;
                totales[totales.length - 1] = tot;
            }
        }
        
        
}
