package domain;

public class Lambda {

	private int id;
	private double residualBandwidth;
        private double energeticWeight;
        private double weight;
        private double longConsumption;
        
        /*
         * Devuelve una nueva lambda a la que solo se le asigna el identificador 
         * unico por el momento.
         * 
         * @param id identificador unico de la lambda dentro de la fibra.
         */
        
        public Lambda(int id) {
            this.id = id;
        }
        
        /*
         * Devuelve una nueva lambda.
         * 
         * @param id identificador unico de la lambda dentro de la fibra.
         * @param residualBandwidth cantidad de ancho de banda restante de la lambda.
         * @param weight peso actual de la lambda.
         */
        
        public Lambda(int id, double residualBandwidth, double weight) {
            this.id = id;
            this.residualBandwidth = residualBandwidth;
            this.weight = weight;
        }
        
        /************************************************************/
        /*                   Getters y setters                      */
        /************************************************************/

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
        
        /************************************************************/
        
        /*
         * Asigna el consumo referente a la longitud de la fibra que contiene la 
         * lambda utilizando la formula floor(longitud/500)*3, que representa 
         * un regenerador de señal que consume 3 W/GB cada 500 Km.
         * 
         * @param lon longitud de la fibra que contiene la lambda.
         */
        
        private void setLongConsumption(double lon) {
            this.longConsumption = Math.floor(lon/500)*3;
        }
        
        public double getLongConsumption() {
            return this.longConsumption;
        }
        
        /*
         * Actualiza el peso de la lambda referente al consumo energetico. Si
         * el consumo de source y destination es 0, se asigna peso infinito.
         * 
         * @param rs consumo del router source.
         * @param rd consumo del router destination.
         * @param lon longitud fisica de la fibra.
         */
        
        public void actualizeEnergeticWeight(double rs, double rd, double lon) {
            if (rs == 0 && rd == 0) {
                this.energeticWeight = Double.POSITIVE_INFINITY;
            }
            else {
                this.setLongConsumption(lon);
                this.energeticWeight = rs + rd + longConsumption;
            }
        }
        
        /*
         * Actualiza el peso de la lambda referente al bloqueo de conexiones 
         * siguiendo la formula 1/(residualBw * Log10(totalBw)).
         * 
         * @param residual cantidad de ancho de banda restante en la lambda.
         * @param total cantidad de ancho de banda total de la lambda.
         */
        
        public void actualizeWeight(double residual, double total) {
            this.weight = 1./(residual*Math.log10(total));
        }
        
        /*
         * Decrementa el ancho de banda de la lambda en bandwidth unidades.
         * 
         * @param bandwidth unidades a decrementar.
         */

        public void decreaseBandwidth(double bandwidth) {
            this.residualBandwidth -= bandwidth;
        }
        
        /*
         * Incrementa el ancho de banda de la lambda en bandwidth unidades.
         * 
         * @param bandwidth unidades a incrementar.
         */
        
        public void increaseBandwidth(double bandwidth) {
            this.residualBandwidth += bandwidth;
        }
}
