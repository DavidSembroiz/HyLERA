package domain;

import java.util.ArrayList;
import java.util.List;

public class Connection {

	private int id;
	private int timeToLive;
	private double bandwidth;
	private int source;
	private int destination;
	private int fiber;
	private int lambda;
        private final List<Integer> lightpathFibers;
        private double consumption;
        
        /**
         * Devuelve una nueva conexion con los valores necesarios para intentar 
         * enrutarla dentro de la red.
         * 
         * @param id identificador unico de la conexion dentro de la red.
         * @param timeToLive unidades de tiempo que estara la conexion dentro de la red.
         * @param bandwidth cantidad de ancho de banda requerido para la conexion.
         * @param source router de inicio de la conexion.
         * @param destination router de destino de la conexion.
         */
        
        public Connection(int id, int timeToLive, double bandwidth, int source, int destination) {
		this.id = id;
		this.timeToLive = timeToLive;
		this.bandwidth = bandwidth;
		this.source = source;
		this.destination = destination;
                lightpathFibers = new ArrayList<>();
	}
        
        /************************************************************/
        /*                   Getters y setters                      */
        /************************************************************/
	
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
        public List<Integer> getLightpathFibers() {
            return lightpathFibers;
        }
        public void addLightpathFiber(int id) {
            this.lightpathFibers.add(id);
        }
        public void setConsumption(double c) {
            this.consumption = c;
        }
        public double getConsumption() {
            return this.consumption;
        }
        
        /************************************************************/
}
