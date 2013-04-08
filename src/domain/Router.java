package domain;

import java.util.List;

public class Router {

	private int id;
	private String name;
        private double totalBandwidth;
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
        public double getTotalBandwidth() {
                return totalBandwidth;
        }
        public void setTotalBandwidth(double bw) {
                this.totalBandwidth = bw;
        }
        public void increaseTotalBandwidth(double inc) {
                this.totalBandwidth += inc;
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
        
        public void addAttachedFiber(int id) {
            this.attachedFibers.add(id);
        }
        
        
        // Function need to be changed, just created to test
        public void increaseConsumption(double bw) {
            this.actualConsumption += 3*bw;
        }
        
        public void decreaseConsumption(double bw) {
            this.actualConsumption -= 3*bw;
        }
}
