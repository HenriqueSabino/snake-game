package entities.ai;

import entities.Food;
import entities.screens.Plane;
import io.github.henriquesabino.math.services.Function;
import io.github.henriquesabino.neunet.ga.GANeuralNetwork;
import processing.core.PVector;

public class Snake extends entities.Snake {
    
    private GANeuralNetwork brain;
    
    public Snake(Plane screen, float posX, float posY, MovementType movementType) {
        super(screen, posX, posY, movementType);
        brain = new GANeuralNetwork(24, new int[]{16}, 4, 0.1);
        brain.setActivationFunctionsForHiddenLayer(Function.RELU);
        brain.setActivationFunctionsForOutputLayer(Function.SOFTMAX);
    }
    
    public void predict(Food food) {
        
        for (double x : fov(food)) {
            System.out.println(x);
        }
        System.out.println();
        
        double[] prediction = brain.predict(fov(food));
        
        int maxIndex = 0;
        
        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] > prediction[maxIndex])
                maxIndex = i;
        }
        
        switch (maxIndex) {
            case 0:
                changeDir(new PVector(1, 0));
                break;
            case 1:
                changeDir(new PVector(0, -1));
                break;
            case 2:
                changeDir(new PVector(-1, 0));
                break;
            case 3:
                changeDir(new PVector(0, 1));
                break;
        }
    }
    
    /*
     * this is the method responsible for most of the inputs of the neural network
     * it will look in 8 direction looking for distance from the walls,
     * body
     *
     * the rays indices will be like this/:
     * 0 for the ray facing the snake's direction
     * and 1-7 from the 0th ray going counter-clockwise
     */
    private double[] fov(Food food) {
        
        double[] fov = new double[24];
        
        //Code responsible to rotate the inputs with the snake direction
        double dirAngle = (dir.heading() < 0) ? Math.PI - dir.heading() : dir.heading();
        int offset = (int) Math.floor((dirAngle % (2 * Math.PI)) / (Math.PI / 4));
        
        double[] temp = lookTo(new PVector(1, 0), food);
        fov[(offset) % 8] = temp[0];
        fov[((offset) % 8 + 8)] = temp[1];
        fov[((offset) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(1, -1), food);
        fov[(offset + 1) % 8] = temp[0];
        fov[((offset + 1) % 8 + 8)] = temp[1];
        fov[((offset + 1) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(0, -1), food);
        fov[(offset + 2) % 8] = temp[0];
        fov[((offset + 2) % 8 + 8)] = temp[1];
        fov[((offset + 2) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(-1, -1), food);
        fov[(offset + 3) % 8] = temp[0];
        fov[((offset + 3) % 8 + 8)] = temp[1];
        fov[((offset + 3) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(-1, 0), food);
        fov[(offset + 4) % 8] = temp[0];
        fov[((offset + 4) % 8 + 8)] = temp[1];
        fov[((offset + 4) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(-1, 1), food);
        fov[(offset + 5) % 8] = temp[0];
        fov[((offset + 5) % 8 + 8)] = temp[1];
        fov[((offset + 5) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(0, 1), food);
        fov[(offset + 6) % 8] = temp[0];
        fov[((offset + 6) % 8 + 8)] = temp[1];
        fov[((offset + 6) % 8 + 16)] = temp[2];
        
        temp = lookTo(new PVector(1, 1), food);
        fov[(offset + 7) % 8] = temp[0];
        fov[((offset + 7) % 8 + 8)] = temp[1];
        fov[((offset + 7) % 8 + 16)] = temp[2];
        
        return fov;
    }
    
    /* marches the position of the snake to one direction and check when it finds
     * a wall, the food or other body parts, this code is similar to the code bullet version
     * of this simulation, check its git repo at: https://github.com/Code-Bullet/SnakeFusion
     */
    private double[] lookTo(PVector dir, Food food) {
        
        double[] dists = new double[3];
        boolean foundFood = false, foundPart = false;
        //diagonal of the screen
        double norm = PVector.dist(new PVector(), new PVector(screen.getWidth(), screen.getHeight()));
        PVector pos = getParts().get(0);
        
        /* 0 - distance to wall
         * 1 - distance to food
         * 2 - distance to tail
         */
        
        pos.add(dir);
        
        for (int i = 0; i < dists.length; i++) {
            dists[i]++;
        }
        
        while (!(pos.x < 0 || pos.x >= screen.getWidth() || pos.y < 0 || pos.y >= screen.getHeight())) {
            
            if (!foundFood && pos.x == food.getPos().x && pos.y == food.getPos().y) {
                foundFood = true;
            }
            
            if (!foundPart) {
                for (PVector part : getParts()) {
                    if (pos.x == part.x && pos.y == part.y) {
                        foundPart = true;
                    }
                }
            }
            
            pos.add(dir);
            dists[0]++;
            if (!foundFood)
                dists[1]++;
            if (!foundPart)
                dists[2]++;
        }
        
        for (int i = 0; i < dists.length; i++) {
            dists[i] /= norm;
        }
        
        return dists;
    }
}
