package domain;

import java.util.List;

public class Connection {

	private int id;
	private int timeToLive;
	private double bandwidth;
	private int source;
	private int destination;
	private int fiber;
	private int lambda;
        private List<Router> path;
	
	public Connection(int id, int timeToLive, double bandwidth, int source, int destination, int lambda) {
		this.id = id;
		this.timeToLive = timeToLive;
		this.bandwidth = bandwidth;
		this.source = source;
		this.destination = destination;
		this.lambda = lambda;
	}
	
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

        public List<Router> getPath() {
            return path;
        }

        public void setPath(List<Router> path) {
            this.path = path;
        }
        
}
