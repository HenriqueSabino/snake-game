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
    private int initSize;
    private int gameSpeed = 5;
    private int score;
    private boolean start = false;
    
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
        size(500, 600);
        screen = new Plane(new PVector(0, 100), new PVector(width, height));
    }
    
    @Override
    public void setup() {
        
        gameScreen = new Plane(new PVector(0, 0), new PVector(20, 20), screen);
        snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2, Snake.MovementType.WALLS);
        food = new Food(gameScreen, snake);
        initSize = snake.getParts().size();
    }
    
    @Override
    public void draw() {
        
        if (snake.isDead()) {
            snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2, snake.getMovementType());
            food = new Food(gameScreen, snake);
        }
        
        if (frameCount % gameSpeed == 0) {
            background(51);
            
            //drawing the text gap between the top and the playable screen
            noStroke();
            fill(0xFF555555);
            rect(0, 0, screen.getWidth(), screen.getY(0));
            
            
            food.check();
            drawFood(food);
            drawSnake(snake);
            
            if (start) {
                snake.update();
            }
            
            fill(255);
            score = (snake.getParts().size() - initSize) * 10;
            textSize(20);
            textAlign(LEFT, TOP);
            text("Score: " + score, 5, 5);
            textAlign(RIGHT, TOP);
            text("Enter to start/play", width - 5, 5);
            text("P to pause", width - 5, 30);
            text("Mode (M) - " + snake.getMovementType(), width - 5, 55);
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
        } else if (keyCode == ENTER) {
            start = true;
        } else if (key == 'p' || key == 'P') {
            start = false;
        } else if ((key == 'm' || key == 'M') && !start) {
            if (snake.getMovementType() == Snake.MovementType.WALLS) {
                snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2,
                        Snake.MovementType.WRAP);
                
                food = new Food(gameScreen, snake);
            } else {
                snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2,
                        Snake.MovementType.WRAP);
                food = new Food(gameScreen, snake);
            }
        }
    }
    
    private void drawSnake(Snake snake) {
        noStroke();
        fill(255);
        
        for (PVector part : snake.getParts()) {
            rect(gameScreen.getX(part.x), gameScreen.getY(part.y), gameScreen.getPixelSize(), gameScreen.getPixelSize());
        }
    }
    
    private void drawFood(Food food) {
        noStroke();
        fill(0xFFFF0000);//red
        rect(gameScreen.getX(food.getPos().x), gameScreen.getY(food.getPos().y),
                gameScreen.getPixelSize(), gameScreen.getPixelSize());
    }
}
