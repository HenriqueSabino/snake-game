package entities;

import processing.core.PVector;

public final class Plane {
    
    private PVector[] points = new PVector[2];
    private float width;
    private float height;
    
    public Plane(PVector v1, PVector v2) {
        points[0] = v1;
        points[1] = v2;
        
        width = Math.abs(v2.x - v1.x);
        height = Math.abs(v2.y - v1.y);
    }
    
    public PVector[] getPoints() {
        return points;
    }
    
    public void setPoints(PVector[] points) {
        this.points = points;
        
        width = Math.abs(points[1].x - points[0].x);
        height = Math.abs(points[1].y - points[0].y);
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
}
