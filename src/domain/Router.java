package domain;

import java.util.List;

public class Router {

	private int id;
	private String name;
        private double totalBandwidth;
        private double consumption;
        private List<Integer> attachedFibers;
        
        /*
         * Crea un nuevo Router que representara un nodo de la red.
         * 
         * @param id identificador unico del router.
         * @param name nombre del pais donde esta situado el router.
         * @param consumption consumo fijo del router por cada GB.
         * @param attachedFibers lista de identificadores de las fibras conectadas
         * al router.
         */
	
	public Router(int id, String name, double consumption, List<Integer> attachedFibers) {
		this.id = id;
		this.name = name;
                this.consumption = consumption;
                this.attachedFibers = attachedFibers;      
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
        public List<Integer> getAttachedFibers() {
            return attachedFibers;
        }
        public void addAttachedFiber(int id) {
            this.attachedFibers.add(id);
        }
        public double getConsumption() {
                return consumption;
        }
        public void setConsumption(double c) {
                this.consumption = c;
        }
        
        /************************************************************/
}
