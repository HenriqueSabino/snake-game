package entities;

import entities.screens.Plane;
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
    private MovementType movementType;
    private boolean dead;
    
    public Snake(Plane screen, float posX, float posY, MovementType movementType) {
        
        this.screen = screen;
        this.movementType = movementType;
        dead = false;
        
        parts.add(new PVector(posX, posY));
        
        for (int i = 1; i < size; i++) {
            
            float x = posX - i;
            parts.add(new PVector(x, posY));
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
        
        //If the next movement kill the snake do not update the positions
        //Will be refactored when the death by eating itself is implemented
        dead = dead || (movementType == MovementType.WALLS && checkBoundaries(PVector.add(parts.get(0), dir)));
        
        if (!dead) {
            for (int i = parts.size() - 1; i >= 0; i--) {
                
                if (i != 0) {
                    parts.set(i, parts.get(i - 1).copy());
                } else {
                    parts.get(i).x += dir.x;
                    parts.get(i).y += dir.y;
                    if (movementType == MovementType.WRAP)
                        wrap();
                }
            }
        }
    }
    
    public void grow() {
        size++;
        parts.add(parts.get(0).copy());
    }
    
    private boolean checkBoundaries(PVector head) {
        return head.x < 0 || head.x >= screen.getWidth() || head.y < 0 || head.y >= screen.getHeight();
    }
    
    private void wrap() {
        parts.get(0).x %= screen.getWidth();
        parts.get(0).y %= screen.getHeight();
    }
    
    public List<PVector> getParts() {
        
        List<PVector> copy = new ArrayList<>();
        
        for (PVector part : parts) {
            copy.add(part.copy());
        }
        
        return copy;
    }
    
    public enum MovementType {
        WRAP,
        WALLS
    }
}
