package domain;

import java.util.ArrayList;
import java.util.List;

public class Network {

	private List<Router> routers;
	private List<Fiber> fibers;
	private List<Connection> enrutedConnections;
	
	
        public Network() {
            generateNetwork();
        }
        
	private void generateNetwork() {
            enrutedConnections = new ArrayList<>();
            generateFibers();
            generateRouters();
            generateLambdas();
            //generateDumbNetwork();
	}
        
        public List<Router> getRouters() {
            return routers;
        }
        
        public List<Fiber> getFibers() {
            return fibers;
        }
        
        public Fiber getFiber(int id) {
            return fibers.get(id - 1);
        }
        
        Router getRouter(int id) {
            return routers.get(id - 1);
        }
        
        /*private void generateDumbNetwork() {
            routers = new ArrayList<>();
            List<Integer> attachedFibers = new ArrayList<>();
            attachedFibers.add(1);
            routers.add(new Router(1, "PT", attachedFibers));
            attachedFibers = new ArrayList<>();
            attachedFibers.add(1);
            attachedFibers.add(2);
            routers.add(new Router(2, "ES", attachedFibers));
            attachedFibers = new ArrayList<>();
            attachedFibers.add(2);
            routers.add(new Router(3, "IS", attachedFibers));
            
            fibers = new ArrayList<>();
            fibers.add(new Fiber(1, 1, 2, 4, 200, 1000));
            fibers.get(0).setWeight(10);
            fibers.add(new Fiber(2, 2, 3, 4, 40, 200));
            fibers.get(1).setWeight(20);
        }*/
	
	private void generateRouters() {
                routers = new ArrayList<>();
		routers.add(new Router(1, "PT", generateAttachedFibers(1)));
		routers.add(new Router(2, "ES", generateAttachedFibers(2)));
		routers.add(new Router(3, "IS", generateAttachedFibers(3)));
		routers.add(new Router(4, "IE", generateAttachedFibers(4)));
		routers.add(new Router(5, "UK", generateAttachedFibers(5)));
		routers.add(new Router(6, "FR", generateAttachedFibers(6)));
		routers.add(new Router(7, "NL", generateAttachedFibers(7)));
		routers.add(new Router(8, "BE", generateAttachedFibers(8)));
		routers.add(new Router(9, "LU", generateAttachedFibers(9)));
		routers.add(new Router(10, "CH", generateAttachedFibers(10)));
		
		routers.add(new Router(11, "NO", generateAttachedFibers(11)));
		routers.add(new Router(12, "DE", generateAttachedFibers(12)));
		routers.add(new Router(13, "IT", generateAttachedFibers(13)));
		routers.add(new Router(14, "DK", generateAttachedFibers(14)));
		routers.add(new Router(15, "MT", generateAttachedFibers(15)));
		routers.add(new Router(16, "SE", generateAttachedFibers(16)));
		routers.add(new Router(17, "CZ", generateAttachedFibers(17)));
		routers.add(new Router(18, "SI", generateAttachedFibers(18)));
		routers.add(new Router(19, "PL", generateAttachedFibers(19)));
		routers.add(new Router(20, "AT", generateAttachedFibers(20)));
		
		routers.add(new Router(21, "HR", generateAttachedFibers(21)));
		routers.add(new Router(22, "SK", generateAttachedFibers(22)));
		routers.add(new Router(23, "HU", generateAttachedFibers(23)));
		routers.add(new Router(24, "FI", generateAttachedFibers(24)));
		routers.add(new Router(25, "EE", generateAttachedFibers(25)));
		routers.add(new Router(26, "LV", generateAttachedFibers(26)));
		routers.add(new Router(27, "LT", generateAttachedFibers(27)));
		routers.add(new Router(28, "RO", generateAttachedFibers(28)));
		routers.add(new Router(29, "BG", generateAttachedFibers(29)));
		routers.add(new Router(30, "GR", generateAttachedFibers(30)));
		
		routers.add(new Router(31, "TR", generateAttachedFibers(31)));
		routers.add(new Router(32, "RU", generateAttachedFibers(32)));
		routers.add(new Router(33, "CY", generateAttachedFibers(33)));
		routers.add(new Router(34, "IL", generateAttachedFibers(34)));
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
		fibers.add(new Fiber(19, 4, 5, 16, 10000, 460));
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
                fibers.add(new Fiber(36, 12, 14, 48, 10000, 580));
                fibers.add(new Fiber(37, 7, 12, 32, 10000, 360));
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
                fibers.add(new Fiber(49, 12, 17, 64, 10000, 280));
                fibers.add(new Fiber(50, 11, 14, 32, 10000, 480));
                
                fibers.add(new Fiber(51, 14, 16, 32, 10000, 520));
                fibers.add(new Fiber(52, 16, 24, 64, 10000, 400));
                fibers.add(new Fiber(53, 11, 16, 32, 10000, 420));
	}
        
        private void generateLambdas() {
            for (Fiber fib : fibers) {
                List<Lambda> l = new ArrayList<>();
                for (int i = 0; i < fib.getNumLambdas(); ++i) {
                    l.add(new Lambda(i + 1, fib.getTotalBandwidth(), 
                                   assignWeight(fib.getTotalBandwidth(), 
                                                fib.getTotalBandwidth())));
                }
                fib.setLambdas(l);
            }
        }
        
        private double assignWeight(double residual, double total) {
            return 1./(residual*Math.log10(total));
        }

        private List<Integer> generateAttachedFibers(int i) {
            List<Integer> attFibersId = new ArrayList<>();
            for (Fiber fib : fibers) {
                if (fib.getNode1() == i || fib.getNode2() == i) {
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

        public int findFiber(int source, int destination) {
            for (Fiber fib : fibers) {
                if ((fib.getNode1() == source && fib.getNode2() == destination) ||
                    (fib.getNode2() == source && fib.getNode1() == destination))
                    return fib.getId();
            }
            return -10;
        }

        public void addEnrutedConnection(Connection c) {
            this.enrutedConnections.add(c);
        }
        
        public void decreaseTimesToLive() {
            for (Connection c : this.enrutedConnections) {
                c.setTimeToLive(c.getTimeToLive() - 1);
                if (c.getTimeToLive() == 0) {
                    this.enrutedConnections.remove(c);
                    // Add path actualization, increase residualBandwidth of
                    // connection path and remove possible lightpaths
                }
            }
        }
}
