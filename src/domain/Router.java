package domain;

public class Router {

	private int id;
	private String name;
	private double totalConsumption;
	private double actualConsumption;
	
	
	public Router(int id, String name) {
		this.id = id;
		this.name = name;
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
}
