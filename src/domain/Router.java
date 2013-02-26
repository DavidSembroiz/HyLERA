package domain;

import java.util.List;

public class Router {

	private int id;
	private String name;
	private double totalConsumption;
	private double actualConsumption;
        private List<Integer> attachedFibers;
	
	
	public Router(int id, String name, List<Integer> attachedFibers) {
		this.id = id;
		this.name = name;
                this.attachedFibers = attachedFibers;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getTotalConsumption() {
		return totalConsumption;
	}
	public void setTotalConsumption(double totalConsumption) {
		this.totalConsumption = totalConsumption;
	}
	public double getActualConsumption() {
		return actualConsumption;
	}
	public void setActualConsumption(double actualConsumption) {
		this.actualConsumption = actualConsumption;
	}
        public List<Integer> getAttachedFibers() {
            return attachedFibers;
        }
}
