package entities;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    
    private int size = 4;
    private List<PVector> parts = new ArrayList<>();
    private Plane screen;
    
    public Snake(Plane screen, float posX, float posY) {
        
        this.screen = screen;
        
        //normalizing the snake position to [0, 1]
        parts.add(new PVector(posX / screen.getWidth(), posY / screen.getHeight()));
        
        for (int i = 1; i < size; i++) {
            
            float x = (posX - i) / screen.getWidth();
            parts.add(new PVector(x, posY / screen.getHeight()));
        }
    }
    
    public List<PVector> getParts() {
        
        List<PVector> copy = new ArrayList<>();
        
        for (PVector part : parts) {
            copy.add(part.copy());
        }
        
        copy.forEach(p -> {
            p.x *= screen.getWidth();
            p.y *= screen.getHeight();
        });
        
        return copy;
    }
}
