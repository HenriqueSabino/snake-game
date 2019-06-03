package entities;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    
    boolean changedDir = false;
    private int size = 4;
    private List<PVector> parts = new ArrayList<>();
    //nextDir is used to fix the issue of reversing the direction
    //by changing to a valid direction quickly before the snake move,
    //like: up, (left, down [in the "same" frame])
    private Plane screen;
    private PVector dir, nextDir;
    
    public Snake(Plane screen, float posX, float posY) {
        
        this.screen = screen;
        
        //normalizing the snake position to [0, 1]
        parts.add(new PVector(posX / screen.getWidth(), posY / screen.getHeight()));
        
        for (int i = 1; i < size; i++) {
            
            float x = (posX - i) / screen.getWidth();
            parts.add(new PVector(x, posY / screen.getHeight()));
        }
        
        dir = new PVector(1, 0);
    }
    
    public void changeDir(PVector vector) {
        
        //Checking if the user is trying to go backwards
        if (vector.copy().mult(-1).hashCode() != dir.hashCode()) {
            nextDir = vector.copy();
            changedDir = true;
        }
    }
    
    private void confirmDir() {
        //Checking if the user is trying to go backwards
        if (nextDir.copy().mult(-1).hashCode() != dir.hashCode())
            dir = nextDir.copy();
    }
    
    public void update() {
        
        if (changedDir) {
            confirmDir();
            changedDir = false;
        }
        
        for (int i = parts.size() - 1; i >= 0; i--) {
            
            if (i != 0) {
                parts.set(i, parts.get(i - 1).copy());
            } else {
                parts.get(i).x += dir.x / screen.getWidth();
                parts.get(i).y += dir.y / screen.getHeight();
            }
        }
    }
    
    public void grow() {
        size++;
        parts.add(parts.get(0).copy());
    }
    
    public List<PVector> getParts() {
        
        List<PVector> copy = new ArrayList<>();
        
        for (PVector part : parts) {
            copy.add(part.copy());
        }
        
        copy.forEach(p -> {
            p.x *= screen.getWidth();
            p.x = (float) Math.round(p.x);
            p.y *= screen.getHeight();
            p.y = (float) Math.round(p.y);
        });
        
        return copy;
    }
}
