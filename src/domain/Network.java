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
    
        /**
         * Modo de actuacion de la red:
         * -- MODE 0: Blocking percentage aware
         * -- MODE 1: Energy aware
         */

        public int MODE = 0;
        
        /**
         * Consumo total de la red.
         */
        
        private double TOTAL_CONSUMPTION = 0;
        
        /**
         * Consumo actual de la red.
         */
        
        private double ACTUAL_CONSUMPTION = 0;
    
        public final int PATH_NOT_FOUND = -9999;
        public final int LAMBDA_NOT_SETTLED = -8888;
        public final int ORIGINAL_FIBERS = 53;
        private final double SMALL_ROUTER = 4.5;
        private final double MEDIUM_ROUTER = 3;
        private final double LARGE_ROUTER = 1.5;
        private final int[] CONNECTION_SLOPE = {1, 2, 3, 4, 5, 6, 5, 4, 3, 2};
        private int CONNECTION_SLOPE_IDX = 0;
        private int CONNECTION_N = 10;
        

	private List<Router> routers;
	private List<Fiber> fibers;
        private List<Lightpath> lightpaths;
	private Set<Connection> enrutedConnections;
        private int blockedConnections;
        private int totalConnections;
        private int numFibers;
        private int connectionIndex;
	
	
        public Network() {
            generateNetwork();
        }
        
	private void generateNetwork() {
            enrutedConnections = new HashSet<>();
            blockedConnections = 0;
            totalConnections = 0;
            connectionIndex = 0;
            numFibers = 53;
            generateFibers();
            generateRouters();
            setTotalRouterBandwidths();
            generateLambdas();
	}
        
        public double getActualConsumption() {
            return this.ACTUAL_CONSUMPTION;
        }
        
        public double getTotalConsumption() {
            return this.TOTAL_CONSUMPTION;
        }
        
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
        
        public void createConnectionsFile(int step) {
            File f = new File("connections.txt");
            if (!f.exists()) try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
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
            int slope = CONNECTION_SLOPE[(CONNECTION_SLOPE_IDX++)%CONNECTION_SLOPE.length];
            int lines = CONNECTION_N * slope;
            cons = this.readConnectionsFromFile(step, lines);
            return cons;
        }
        
        /** CONNECTION_SLOPE_IDX does not have increment here because it is executed always
         * before a generateConnectionsFromFile() function.
         */
        
        public ArrayList<Connection> generateConnections() {
            ArrayList<Connection> cons = new ArrayList<>();
            int slope = CONNECTION_SLOPE[(CONNECTION_SLOPE_IDX)%CONNECTION_SLOPE.length];
            for (int i = 0; i < CONNECTION_N * slope; ++i) {
                int ttl = 50;//(int) Math.ceil(Math.random()*100);
                int source = (int) Math.ceil(Math.random()*34);
                int destination = (int) Math.ceil(Math.random()*34);
                while (source == destination) {
                    destination = (int) Math.ceil(Math.random()*34);
                }
                double bw = 5;//Math.ceil(Math.random()*1000);
                //int index = getAvailableIndex();
                int index = getNextIndex();
                Connection c = new Connection(index, ttl, bw, source, destination);
                cons.add(c);
            }
            return cons;
        }
        
        private int getNextIndex() {
            return ++connectionIndex;
        }
        
        /*private int getAvailableIndex() {
            for (int i = 1; i < Integer.MAX_VALUE; ++i) {
                boolean found = false;
                for (Iterator<Connection> it = this.enrutedConnections.iterator(); !found && it.hasNext();) {
                    Connection c = it.next();
                    if (c.getId() == i) found = true;
                }
                if (!found) return i;
            }
            return Integer.MAX_VALUE;
        }*/
        
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
        
        public Fiber getLightfiber(int id) {
            for (Lightpath l : lightpaths) {
                if (l.getLightfiber().getId() == id)
                    return l.getLightfiber();
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
                                    if (id > fib.getId()) id = fib.getId();
                                }
                            }
                            else if (MODE == 1) {
                                if (res > lam.getEnergeticWeight()) {
                                    res = lam.getEnergeticWeight();
                                    id = fib.getId();
                                }
                                else if (res == lam.getEnergeticWeight()) {
                                    if (id > fib.getId()) id = fib.getId();
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
        
        public int getTotalConnections() {
            return totalConnections;
        }
        
        public void increaseTotalConnections() {
            this.totalConnections++;
        }
        
        public List<Fiber> getAttachedFibersById(int id) {
            Router source = getRouter(id);
            List<Integer> attFibersId = source.getAttachedFibers();
            List<Fiber> attFibers = new ArrayList<>();
            for (Integer fib : attFibersId) {
                if(fib <= ORIGINAL_FIBERS) attFibers.add(getFiber(fib));
                else attFibers.add(getLightfiber(fib));
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
                            if (!lambdas.contains(-l.getId())) lambdas.add(-l.getId());
                        }
                        else {
                            if (!lambdas.contains(l.getId())) lambdas.add(l.getId());
                        }
                    }
                }
            }
            Collections.reverse(lambdas);
            return lambdas;
        }
        
        public void decreaseTimesToLive() {
            if (this.enrutedConnections.isEmpty()) return;
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
            c.setConsumption(cons*c.getBandwidth());
            this.TOTAL_CONSUMPTION += c.getConsumption();
            this.ACTUAL_CONSUMPTION += c.getConsumption();
        }
        
        
        public void decreaseBandwidths(Connection c, LinkedList<Router> path) {
            LinkedList<Router> physicalPath = new LinkedList<>();
            Router source;
            boolean init = true;
            if (c.getLambda() == PATH_NOT_FOUND) ++blockedConnections;
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
                    && f.getLambdas().get(0).getResidualBandwidth() >= c.getBandwidth())
                    return f;
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
                    if (l.getLightfiber().getId() == i) found = true;
                }
                if (!found) return i;
            }
            return Integer.MAX_VALUE;
        }
        
        private int getNextNumFiber() {
            return ++this.numFibers;
        }
        
        
        public void createLightpath(Connection c, LinkedList<Router> p, double bw, int dist) {
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
        
        public void increaseLightpath(Connection c) {
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
        
        public void deleteLightpath(Connection c, int lightfiber) {
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
		routers.add(new Router(1, "PT", SMALL_ROUTER, generateAttachedFibers(1)));
		routers.add(new Router(2, "ES", MEDIUM_ROUTER, generateAttachedFibers(2)));
		routers.add(new Router(3, "IS", SMALL_ROUTER, generateAttachedFibers(3)));
		routers.add(new Router(4, "IE", SMALL_ROUTER, generateAttachedFibers(4)));
		routers.add(new Router(5, "UK", LARGE_ROUTER, generateAttachedFibers(5)));
		routers.add(new Router(6, "FR", LARGE_ROUTER, generateAttachedFibers(6)));
		routers.add(new Router(7, "NL", LARGE_ROUTER, generateAttachedFibers(7)));
		routers.add(new Router(8, "BE", MEDIUM_ROUTER, generateAttachedFibers(8)));
		routers.add(new Router(9, "LU", SMALL_ROUTER, generateAttachedFibers(9)));
		routers.add(new Router(10, "CH", LARGE_ROUTER, generateAttachedFibers(10)));
		
		routers.add(new Router(11, "NO", MEDIUM_ROUTER, generateAttachedFibers(11)));
		routers.add(new Router(12, "DE", LARGE_ROUTER, generateAttachedFibers(12)));
		routers.add(new Router(13, "IT", LARGE_ROUTER, generateAttachedFibers(13)));
		routers.add(new Router(14, "DK", LARGE_ROUTER, generateAttachedFibers(14)));
		routers.add(new Router(15, "MT", SMALL_ROUTER, generateAttachedFibers(15)));
		routers.add(new Router(16, "SE", LARGE_ROUTER, generateAttachedFibers(16)));
		routers.add(new Router(17, "CZ", LARGE_ROUTER, generateAttachedFibers(17)));
		routers.add(new Router(18, "SI", MEDIUM_ROUTER, generateAttachedFibers(18)));
		routers.add(new Router(19, "PL", MEDIUM_ROUTER, generateAttachedFibers(19)));
		routers.add(new Router(20, "AT", LARGE_ROUTER, generateAttachedFibers(20)));
		
		routers.add(new Router(21, "HR", MEDIUM_ROUTER, generateAttachedFibers(21)));
		routers.add(new Router(22, "SK", LARGE_ROUTER, generateAttachedFibers(22)));
		routers.add(new Router(23, "HU", MEDIUM_ROUTER, generateAttachedFibers(23)));
		routers.add(new Router(24, "FI", MEDIUM_ROUTER, generateAttachedFibers(24)));
		routers.add(new Router(25, "EE", SMALL_ROUTER, generateAttachedFibers(25)));
		routers.add(new Router(26, "LV", SMALL_ROUTER, generateAttachedFibers(26)));
		routers.add(new Router(27, "LT", SMALL_ROUTER, generateAttachedFibers(27)));
		routers.add(new Router(28, "RO", MEDIUM_ROUTER, generateAttachedFibers(28)));
		routers.add(new Router(29, "BG", MEDIUM_ROUTER, generateAttachedFibers(29)));
		routers.add(new Router(30, "GR", MEDIUM_ROUTER, generateAttachedFibers(30)));
		
		routers.add(new Router(31, "TR", SMALL_ROUTER, generateAttachedFibers(31)));
		routers.add(new Router(32, "RU", SMALL_ROUTER, generateAttachedFibers(32)));
		routers.add(new Router(33, "CY", SMALL_ROUTER, generateAttachedFibers(33)));
		routers.add(new Router(34, "IL", SMALL_ROUTER, generateAttachedFibers(34)));
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
        
        private void printRouter(Router r) {
            System.out.println("");
            System.out.println("Router " + r.getId() + " " + r.getName());
            System.out.println("--------------------------------");
        }
        
        private void printFiber(Fiber fib) {
            System.out.println("");
            System.out.println("Fiber " + fib.getId() + "  Total BW " + fib.getTotalBandwidth());
            System.out.println("------------------------- Lambdas");
        }
        
        private void printLambda(Lambda l) {
            System.out.println(l.getId() + "     Residual BW " + l.getResidualBandwidth() + "     Weight " + l.getWeight());
        }
}
