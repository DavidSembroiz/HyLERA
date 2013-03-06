/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author David
 */
public class Lightfiber {
    
        private Fiber lightpath;
        private LinkedList<Router> path;
        private List<Fiber> additionalLightpaths;
        
        public Lightfiber(Fiber f, LinkedList<Router> path) {
            this.lightpath = f;
            this.path = path;
            additionalLightpaths = new ArrayList<>();
        }
        
        public Lightfiber(Fiber f, LinkedList<Router> path, List<Fiber> additional) {
            this.lightpath = f;
            this.path = path;
            this.additionalLightpaths = additional;
        }
        
        public Fiber getFiber() {
            return lightpath;
        }
        
        public LinkedList<Router> getPath() {
            return path;
        }
        
        public void printPath() {
            if (this.getFiber().getLambdas().get(0).getId() == -3) {
                System.out.println("Path not found");
            }
            else {
                for (Iterator<Router> it = path.iterator(); it.hasNext();) {
                    System.out.println(it.next().getName());
                }
            }
        }
}
