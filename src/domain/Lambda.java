package domain;

public class Lambda {

	private int id;
	private double residualBandwidth;
        private double energeticWeight;
        private double weight;
        private double longConsumption;
        
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
        private void setLongConsumption(double lon) {
            this.longConsumption = Math.floor(lon/500)*3;
        }
        public double getLongConsumption() {
            return this.longConsumption;
        }
        public void actualizeEnergeticWeight(double rs, double rd, double lon) {
            if (rs == 0 && rd == 0) {
                this.energeticWeight = Double.POSITIVE_INFINITY;
            }
            else {
                this.setLongConsumption(lon);
                this.energeticWeight = rs + rd + longConsumption;
            }
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
            if (this.residualBandwidth < 0) System.out.println("ERROR RESIDUAL NEGATIVE");
        }
        
        public void increaseBandwidth(double bandwidth) {
            this.residualBandwidth += bandwidth;
        }
}
