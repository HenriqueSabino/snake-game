package application;

import entities.Plane;
import entities.Snake;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    
    private Snake snake;
    private Plane plane;
    private int pixelSize = 30;
    
    public static void main(String[] args) {
        
        String className = "application.Main";
        
        try {
            PApplet.main(Class.forName(className));
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class " + className);
            e.printStackTrace();
        }
    }
    
    @Override
    public void settings() {
        
        size(600, 600);
    }
    
    @Override
    public void setup() {
        
        background(51);
        
        plane = new Plane(new PVector(), new PVector(width / pixelSize, height / pixelSize));
        snake = new Snake(plane, plane.getWidth() / 2, plane.getHeight() / 2);
        
        drawSnake(snake);
    }
    
    private void drawSnake(Snake snake) {
        strokeWeight(1);
        stroke(0);
        fill(255);
        
        for (PVector part : snake.getParts()) {
            println(part);
        }
        
        for (PVector part : snake.getParts()) {
            rect(part.x * (pixelSize - 1), part.y * (pixelSize - 1), (pixelSize - 1), (pixelSize - 1));
        }
        
        for (PVector part : snake.getParts()) {
            println(part);
        }
    }
}
