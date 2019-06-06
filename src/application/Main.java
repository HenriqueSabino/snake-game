package application;

import entities.Food;
import entities.Snake;
import entities.screens.Plane;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    
    private Snake snake;
    private Food food;
    private Plane gameScreen, screen;
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
        screen = new Plane(width, height);
    }
    
    @Override
    public void setup() {
        
        gameScreen = new Plane(new PVector(), new PVector(20, 20), screen);
        snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2, Snake.MovementType.WALLS);
        food = new Food(gameScreen, snake);
    }
    
    @Override
    public void draw() {
        
        if (snake.isDead()) {
            snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2, snake.getMovementType());
            food = new Food(gameScreen, snake);
        }
        
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
        noStroke();
        fill(255);
        
        for (PVector part : snake.getParts()) {
            rect(part.x * gameScreen.getPixelSize(), part.y * gameScreen.getPixelSize(),
                    gameScreen.getPixelSize(), gameScreen.getPixelSize());
        }
    }
    
    private void drawFood(Food food) {
        noStroke();
        fill(0xFFFF0000);//red
        rect(food.getPos().x * gameScreen.getPixelSize(), food.getPos().y * gameScreen.getPixelSize(),
                gameScreen.getPixelSize(), gameScreen.getPixelSize());
    }
}
