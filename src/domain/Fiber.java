package domain;

import java.util.List;

public class Fiber {

	private int id;
	private int node1, node2;
	private int numLambdas;
	private int length;
	private List<Lambda> lambdas;
	private double totalBandwidth;
        
        /* 
         * Devuelve una nueva fibra que puede representar tanto a una fibra original
         * como a una fibra de un lightpath.
         * 
         * @param id numero de identificacion unico de cada fibra.
         * @param node1 identificador del Router source de la fibra.
         * @param node2 identificador del Router destination de la fibra.
         * @param numLambdas cantidad de lambdas que contiene la fibra.
         * @param Bandwidth ancho de banda total de cada una de las lambdas.
         * @param length longitud fisica de la fibra.
         */
	
	public Fiber(int id, int node1, int node2, int numLambdas, double Bandwidth, int length) {
		this.id = id;
		this.node1 = node1;
		this.node2 = node2;
		this.numLambdas = numLambdas;
		this.length = length;
		this.totalBandwidth = Bandwidth;
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
        
        /************************************************************/
        
        /* 
         * Devuelve la lambda con identificador id para las fibras originales
         * unicamente.
         * 
         * @param id identificador de la lambda dentro de la fibra.
         * @return lambda con el identificador id.
         */
        
        public Lambda getLambda(int id) {
            return this.lambdas.get(id - 1);
        }
        
        /*
         * Devuelve la unica lambda que contiene una fibra que representa
         * un lightpath.
         */
        
        public Lambda getLightLambda() {
            return this.lambdas.get(0);
        }
        
        /* 
         * Decrementa el ancho de banda en bandwidth unidades de la lambda
         * con identificador lambda en las fibras originales unicamente.
         *  
         * @param bandwidth unidades a decrementar.
         * @param lambda identificador de la lambda.
         */

        public void decreaseBandwidth(double bandwidth, int lambda) {
            lambdas.get(lambda - 1).decreaseBandwidth(bandwidth);
        }
        
        /* 
         * Decrementa el ancho de banda en bandwidth unidades de la unica
         * lambda que contiene una fibra que representa un lightpath.
         * 
         * @param bandwidth unidades a decrementar.
         */
        
        public void decreaseLightBandwidth(double bandwidth) {
            lambdas.get(0).decreaseBandwidth(bandwidth);
        }
        
        /* 
         * Incrementa el ancho de banda en bandwidth unidades de la lambda
         * con identificador lambda en las fibras originales unicamente.
         * 
         * @param bandwidth unidades a incrementar.
         * @param lambda identificador de la lambda.
         */
        
        public void increaseBandwidth(double bandwidth, int lambda) {
            lambdas.get(lambda - 1).increaseBandwidth(bandwidth);
        }
        
        /* 
         * Incrementa el ancho de banda en bw unidades de la unica lambda
         * que contiene una fibra que representa un lightpath.
         * 
         * @param bw unidades a incrementar.
         */
        
        public void increaseLightBandwidth(double bw) {
            lambdas.get(0).increaseBandwidth(bw);
        }
        
        /* 
         * Actualiza el peso referente al bloqueo de conexiones.
         * 
         * @param lambda identificador de la lambda a actualizar.
         * @param residual cantidad de ancho de banda restante en la lambda.
         * @param total cantidad de ancho de banda total de la lambda.
         */
        
        public void actualizeLambdaWeight(int lambda, double residual, double total) {
            this.lambdas.get(lambda - 1).actualizeWeight(residual, total);
        }
        
        /* 
         * Actualiza el peso referente al bloqueo de conexiones en una fibra
         * que representa un lightpath.
         * 
         * @param residual cantidad de ancho de banda restante de la lambda.
         * @param total cantidad de ancho de banda total de la lambda.
         */
        
        public void actualizeLightLambdaWeight(double residual, double total) {
            this.lambdas.get(0).actualizeWeight(residual, total);
        }
        
        /* 
         * Actualiza el peso referente a la energia consumida.
         * 
         * @param lambda identificador de la lambda a actualizar.
         * @param rs consumo del Router source de la fibra que contiene la lambda.
         * @param rd consumo del Router destination de la fibra que contiene la lambda.
         * @param lon longitud fisica de la fibra que contiene la lambda.
         */
        
        public void actualizeLambdaEnergeticWeight(int lambda, double rs, double rd, int lon) {
            this.lambdas.get(lambda - 1).actualizeEnergeticWeight(rs, rd, lon);
        }
        
        /* 
         * Asigna el valor infinito al peso referente a la energia consumida
         * de la lambda con identificador lambda.
         * 
         * @param lambda identificador de la lambda.
         */
        
        public void setInfinityLambdaEnergeticWeight(int lambda) {
            this.lambdas.get(lambda - 1).actualizeEnergeticWeight(0, 0, 0);
        }   
}
