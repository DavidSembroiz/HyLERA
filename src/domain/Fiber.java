package domain;

import java.util.List;

public class Fiber {

	private int id;
	private int node1, node2;
	private int numLambdas;
	private int length;
	private List<Lambda> lambdas;
	private double totalBandwidth;
	
	public Fiber(int id, int node1, int node2, int numLambdas, double totalBandwidth, int length) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
		this.numLambdas = numLambdas;
		this.length = length;
		this.totalBandwidth = totalBandwidth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNode1() {
		return node1;
	}
	public void setNode1(int node1) {
		this.node1 = node1;
	}
	public int getNode2() {
		return node2;
	}
	public void setNode2(int node2) {
		this.node2 = node2;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public List<Lambda> getLambdas() {
		return lambdas;
	}
	public void setLambdas(List<Lambda> lambdas) {
		this.lambdas = lambdas;
	}
	public double getTotalBandwidth() {
		return totalBandwidth;
	}
	public void setTotalBandwidth(double totalBandwidth) {
		this.totalBandwidth = totalBandwidth;
	}
	public int getNumLambdas() {
		return numLambdas;
	}
	public void setNumLambdas(int numLambdas) {
		this.numLambdas = numLambdas;
	}
        public Lambda getLambda(int id) {
            return this.lambdas.get(id - 1);
        }

        public void decreaseWeight(double bandwidth, int lambda) {
            lambdas.get(lambda - 1).decreaseWeight(bandwidth);
        }
}
