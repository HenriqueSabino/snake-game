package entities;

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
        
        float posX = (float) Math.floor((float) Math.random() * screen.getWidth());
        float posY = (float) Math.floor((float) Math.random() * screen.getHeight());
        
        pos = new PVector(posX, posY);
        
        for (PVector part : snake.getParts()) {
            if (part.hashCode() == pos.hashCode()) {
                randomPos();
                break;
            }
        }
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
