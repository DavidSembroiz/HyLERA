package domain;

public class Lambda {

	private int id;
	private double residualBandwidth;
        private double energeticWeight;
        private double weight;
        
        public Lambda(int id) {
            this.id = id;
        }
        
        public Lambda(int id, double residualBandwidth, double weight) {
            this.id = id;
            this.residualBandwidth = residualBandwidth;
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }
        public void setWeight(double weight) {
            this.energeticWeight = weight;
        }
        public double getEnergeticWeight() {
            return energeticWeight;
        }
        public void setEnergeticWeight(double weight) {
            this.weight = weight;
        }
        public void actualizeEnergeticWeight(double rs, double rd, double lon) {
            if (rs == 0 && rd == 0) {
                this.energeticWeight = Double.POSITIVE_INFINITY;
            }
            else this.energeticWeight = rs + rd + Math.floor(lon/500)*3;
        }
        public void actualizeWeight(double residual, double total) {
            this.weight = 1./(residual*Math.log10(total));
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
        
        public void increaseBandwidth(double bandwidth) {
            this.residualBandwidth += bandwidth;
        }
}
