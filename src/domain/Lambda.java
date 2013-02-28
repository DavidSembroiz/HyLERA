package domain;

public class Lambda {

	private int id;
	private double residualBandwidth;
        private double weight;
        
        public Lambda(int id, double residualBandwidth, double weight) {
            this.id = id;
            this.residualBandwidth = residualBandwidth;
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }
        public void setWeight(double weight) {
            this.weight = weight;
        }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getResidualBandwidth() {
		return residualBandwidth;
	}
	public void setResidualBandwidth(double residualBandwidth) {
		this.residualBandwidth = residualBandwidth;
	}

        public void decreaseBandwidth(double bandwidth) {
            this.residualBandwidth -= bandwidth;
        }
}
