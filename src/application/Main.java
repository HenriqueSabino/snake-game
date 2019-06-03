package application;

import entities.Food;
import entities.Plane;
import entities.Snake;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    
    private Snake snake;
    private Food food;
    private Plane plane;
    private int pixelSize = 30;
    private int gameSpeed = 5;
    
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
        
        plane = new Plane(new PVector(), new PVector(width / pixelSize, height / pixelSize));
        snake = new Snake(plane, plane.getWidth() / 2, plane.getHeight() / 2);
        food = new Food(plane, snake);
    }
    
    @Override
    public void draw() {
        
        if (frameCount % gameSpeed == 0) {
            background(51);
            drawFood(food);
            drawSnake(snake);
            snake.update();
            food.check();
        }
    }
    
    @Override
    public void keyPressed() {
        if (keyCode == UP || key == 'w') {
            snake.changeDir(new PVector(0, -1));
        } else if (keyCode == DOWN || key == 's') {
            snake.changeDir(new PVector(0, 1));
        } else if (keyCode == LEFT || key == 'a') {
            snake.changeDir(new PVector(-1, 0));
        } else if (keyCode == RIGHT || key == 'd') {
            snake.changeDir(new PVector(1, 0));
        }
    }
    
    private void drawSnake(Snake snake) {
        strokeWeight(1);
        stroke(0);
        fill(255);
        
        for (PVector part : snake.getParts()) {
            rect(part.x * pixelSize, part.y * pixelSize, (pixelSize - 1), (pixelSize - 1));
        }
    }
    
    private void drawFood(Food food) {
        strokeWeight(1);
        stroke(0);
        fill(0xFFFF0000);
        
        
        rect(food.getPos().x * pixelSize, food.getPos().y * pixelSize, (pixelSize - 1), (pixelSize - 1));
    }
}
