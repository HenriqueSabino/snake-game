package entities;

import entities.screens.Plane;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    
    private boolean changedDir = false;
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
        if (!(vector.copy().mult(-1).x == dir.x && vector.copy().mult(-1).y == dir.y)) {
            nextDir = vector.copy();
            changedDir = true;
        }
    }
    
    private void confirmDir() {
        //Checking if the user is trying to go backwards
        if (!(nextDir.copy().mult(-1).x == dir.x && nextDir.copy().mult(-1).y == dir.y))
            dir = nextDir.copy();
    }
    
    public void update() {
        if (changedDir) {
            confirmDir();
            changedDir = false;
        }
        
        //gets a deep copy of the list
        List<PVector> temp = getParts();
        
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
            
            //If in the next position the snake is dead
            //rollback the positions
            checkDeath(parts.get(0));
            dead = dead || (movementType == MovementType.WALLS && checkBoundaries(parts.get(0)));
            parts = (dead) ? temp : parts;
        }
    }
    
    public void grow() {
        size++;
        parts.add(parts.get(0).copy());
    }
    
    private boolean checkBoundaries(PVector head) {
        return head.x < 0 || head.x >= screen.getWidth() || head.y < 0 || head.y >= screen.getHeight();
    }
    
    private void checkDeath(PVector head) {
        //The loop starts at position 4 because it is impossible
        //for the snake head to get to position 3 positions
        for (int i = 4; i < parts.size(); i++) {
            if (head.x == parts.get(i).x && head.y == parts.get(i).y)
                dead = true;
        }
    }
    
    private void wrap() {
        
        if (parts.get(0).x >= 0)
            parts.get(0).x %= screen.getWidth();
        else
            parts.get(0).x = screen.getWidth() - 1;
        
        if (parts.get(0).y >= 0)
            parts.get(0).y %= screen.getHeight();
        else
            parts.get(0).y = screen.getHeight() - 1;
    }
    
    public List<PVector> getParts() {
        
        List<PVector> copy = new ArrayList<>();
        
        for (PVector part : parts) {
            copy.add(part.copy());
        }
        
        return copy;
    }
    
    public MovementType getMovementType() {
        return movementType;
    }
    
    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
    
    public boolean isDead() {
        return dead;
    }
    
    public enum MovementType {
        WRAP,
        WALLS
    }
}
