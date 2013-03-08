package domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Connection {

	private int id;
	private int timeToLive;
	private double bandwidth;
	private int source;
	private int destination;
	private int fiber;
	private int lambda;
        private LinkedList<Router> path;
        private List<Integer> lightpathFibers;
        
        public Connection(int id, int timeToLive, double bandwidth, int source, int destination) {
		this.id = id;
		this.timeToLive = timeToLive;
		this.bandwidth = bandwidth;
		this.source = source;
		this.destination = destination;
                lightpathFibers = new ArrayList<>();
	}
	
	public Connection(int id, int timeToLive, double bandwidth, int source, int destination, int lambda) {
		this.id = id;
		this.timeToLive = timeToLive;
		this.bandwidth = bandwidth;
		this.source = source;
		this.destination = destination;
		this.lambda = lambda;
                lightpathFibers = new ArrayList<>();
	}
        
        /*public void printPath() {
            if (this.lambda == -3) {
                System.out.println("Path not found");
            }
            else {
                for (Iterator<Router> it = path.iterator(); it.hasNext();) {
                    System.out.println(it.next().getName());
                }
            }
        }*/
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getDestination() {
		return destination;
	}
	public void setDestination(int destination) {
		this.destination = destination;
	}
	public int getFiber() {
		return fiber;
	}
	public void setFiber(int fiber) {
		this.fiber = fiber;
	}
	public int getLambda() {
		return lambda;
	}
	public void setLambda(int lambda) {
		this.lambda = lambda;
	}

        public LinkedList<Router> getPath() {
            return path;
        }

        public void setPath(LinkedList<Router> path) {
            this.path = path;
        }
        public List<Integer> getLightpathFibers() {
            return lightpathFibers;
        }
        public void addLightpathFiber(int id) {
            this.lightpathFibers.add(id);
        }
        
        public void printConnection() {
            System.out.println("Connection id: " + this.id);
            System.out.println("Lambda: " + this.lambda);
            System.out.println("LightPath Fiber: " + this.lightpathFibers);
        }
}
