package entities;

import entities.screens.Plane;
import processing.core.PVector;

public class Food {
    
    private PVector pos;
    private Snake snake;
    private Plane screen;
    
    public Food(Plane screen, Snake snake) {
        
        this.screen = screen;
        this.snake = snake;
        
        randomPos();
    }
    
    private void randomPos() {
        
        float posX;
        float posY;
        
        do {
            posX = (float) Math.floor((float) Math.random() * screen.getWidth());
            posY = (float) Math.floor((float) Math.random() * screen.getHeight());
        } while (snake.getParts().indexOf(new PVector(posX, posY)) != -1);
        
        pos = new PVector(posX, posY);
    }
    
    public void check() {
        
        if (snake.getParts().get(0).hashCode() == pos.hashCode()) {
            snake.grow();
            randomPos();
        }
    }
    
    public PVector getPos() {
        return pos;
    }
    
    public void setPos(PVector pos) {
        this.pos = pos;
    }
}
