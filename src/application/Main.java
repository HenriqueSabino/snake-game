package application;

import entities.Food;
import entities.ai.Snake;
import entities.screens.Plane;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    
    private List<Snake> population = new ArrayList<>();
    private int populationSize = 300;
    private List<Food> foods = new ArrayList<>();
    private Plane gameScreen, screen;
    private int initSize;
    private int gameSpeed = 5;
    private int highscore = 0, generation = 1;
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
        for (int i = 0; i < populationSize; i++) {
            population.add(new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2,
                    entities.Snake.MovementType.WALLS, 100));
            foods.add(new Food(gameScreen, population.get(i)));
        }
        initSize = population.get(0).getParts().size();
    }
    
    @Override
    public void draw() {
        
        background(51);
        
        //drawing the text gap between the top and the playable screen
        noStroke();
        fill(0xFF555555);
        rect(0, 0, screen.getWidth(), screen.getY(0));
        
        for (int i = 0; i < populationSize; i++) {
            foods.get(i).check();
            drawFood(foods.get(i));
            drawSnake(population.get(i));
            int score = population.get(i).getParts().size() - initSize;
            highscore = (score > highscore) ? score : highscore;
        }
        
        if (start) {
            for (int i = 0; i < populationSize; i++) {
                
                population.get(i).predict(foods.get(i));
                population.get(i).update();
            }
        }
        
        fill(255);
        textSize(20);
        
        textAlign(LEFT, TOP);
        text("Generation: " + generation, 5, 5);
        text("HighScore: " + highscore, 5, 30);
        
        textAlign(RIGHT, TOP);
        text("Enter to start/play", width - 5, 5);
        text("P to pause", width - 5, 30);
        text("Mode (M) - " + population.get(0).getMovementType(), width - 5, 55);
        
    }
    
    @Override
    public void keyPressed() {
        if (keyCode == ENTER) {
            start = true;
        } else if (key == 'p' || key == 'P') {
            start = false;
        } else if ((key == 'm' || key == 'M') && !start) {
            
            for (int i = 0; i < populationSize; i++) {
                
                Snake snake = population.get(i);
                Food food = foods.get(i);
                
                if (snake.getMovementType() == Snake.MovementType.WALLS) {
                    snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2,
                            Snake.MovementType.WRAP, 100);
                    
                    food = new Food(gameScreen, snake);
                } else {
                    snake = new Snake(gameScreen, gameScreen.getWidth() / 2, gameScreen.getHeight() / 2,
                            Snake.MovementType.WALLS, 100);
                    food = new Food(gameScreen, snake);
                }
                highscore = 0;
                
            }
        }
    }
    
    private void drawSnake(Snake snake) {
        noStroke();
        fill(255, 100);
        
        for (PVector part : snake.getParts()) {
            rect(gameScreen.getX(part.x), gameScreen.getY(part.y), gameScreen.getPixelSize(), gameScreen.getPixelSize());
        }
    }
    
    private void drawFood(Food food) {
        noStroke();
        fill(0x64FF0000);//red
        rect(gameScreen.getX(food.getPos().x), gameScreen.getY(food.getPos().y),
                gameScreen.getPixelSize(), gameScreen.getPixelSize());
    }
}
