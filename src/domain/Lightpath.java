/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.LinkedList;

/**
 *
 * @author David Sembroiz
 */
public class Lightpath {
    
    private LinkedList<Router> path;
    private Fiber lightfiber;
    
    public Lightpath(LinkedList<Router> path, Fiber f) {
        this.path = path;
        this.lightfiber = f;
    }

    public LinkedList<Router> getPath() {
        return path;
    }

    public Fiber getLightfiber() {
        return lightfiber;
    }

    public void setPath(LinkedList<Router> path) {
        this.path = path;
    }

    public void setLightfiber(Fiber lightfiber) {
        this.lightfiber = lightfiber;
    }
}
