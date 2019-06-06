package entities.screens;

import processing.core.PVector;

public final class Plane {
    
    private Plane parent;
    private PVector[] points = new PVector[2];
    private float width;
    private float height;
    private float pixelSize;
    
    public Plane(float width, float height) {
        
        this.width = width;
        this.height = height;
        
        points[0] = new PVector();
        points[1] = new PVector(width, height);
        
        parent = null;
        pixelSize = 1;
    }
    
    public Plane(PVector v1, PVector v2) {
        
        setPoints(new PVector[]{v1, v2});
        
        parent = null;
        pixelSize = 1;
    }
    
    public Plane(PVector v1, PVector v2, Plane parent) {
        points[0] = v1;
        points[1] = v2;
        
        width = Math.abs(v2.x - v1.x);
        height = Math.abs(v2.y - v1.y);
        
        //note: only support square planes for now
        this.parent = parent;
        this.pixelSize = parent.getWidth() / getWidth();
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
    
    public Plane getParent() {
        return parent;
    }
    
    public void setParent(Plane parent) {
        this.parent = parent;
        this.pixelSize = parent.getWidth() / getWidth();
    }
    
    public float getPixelSize() {
        return pixelSize;
    }
}
