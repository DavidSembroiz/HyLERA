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
		/*generateRouters();
		generateFibers();*/
            generateDumbNetwork();
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
        
        private void generateDumbNetwork() {
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
        }
	
	private void generateRouters() {
		/*routers.add(new Router(1, "PT"));
		routers.add(new Router(2, "ES"));
		routers.add(new Router(3, "IS"));
		routers.add(new Router(4, "IE"));
		routers.add(new Router(5, "UK"));
		routers.add(new Router(6, "FR"));
		routers.add(new Router(7, "NL"));
		routers.add(new Router(8, "BE"));
		routers.add(new Router(9, "LU"));
		routers.add(new Router(10, "CH"));
		
		routers.add(new Router(11, "NO"));
		routers.add(new Router(12, "DE"));
		routers.add(new Router(13, "IT"));
		routers.add(new Router(14, "DK"));
		routers.add(new Router(15, "MT"));
		routers.add(new Router(16, "SE"));
		routers.add(new Router(17, "CZ"));
		routers.add(new Router(18, "SI"));
		routers.add(new Router(19, "PL"));
		routers.add(new Router(20, "AT"));
		
		routers.add(new Router(21, "HR"));
		routers.add(new Router(22, "SK"));
		routers.add(new Router(23, "HU"));
		routers.add(new Router(24, "FI"));
		routers.add(new Router(25, "EE"));
		routers.add(new Router(26, "LV"));
		routers.add(new Router(27, "LT"));
		routers.add(new Router(28, "RO"));
		routers.add(new Router(29, "BG"));
		routers.add(new Router(30, "GR"));
		
		routers.add(new Router(31, "TR"));
		routers.add(new Router(32, "RU"));
		routers.add(new Router(33, "CY"));
		routers.add(new Router(34, "IL"));*/
	}

	private void generateFibers() {
		fibers.add(new Fiber(1, 3, 14, 4, 310, 2100));
		fibers.add(new Fiber(2, 12, 34, 8, 2500, 2900));
		fibers.add(new Fiber(3, 7, 15, 1, 45, 1970));
		fibers.add(new Fiber(4, 28, 31, 8, 2500, 750));
		fibers.add(new Fiber(5, 14, 32, 8, 2500, 1560));
		fibers.add(new Fiber(6, 14, 25, 16, 10000, 830));
		fibers.add(new Fiber(7, 25, 26, 16, 10000, 280));
		fibers.add(new Fiber(8, 26, 27, 16, 10000, 260));
		fibers.add(new Fiber(9, 19, 27, 4, 10000, 395));
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
		
	}  
}
