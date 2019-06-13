package entities.ai;

import entities.Food;
import entities.screens.Plane;
import io.github.henriquesabino.math.services.Function;
import io.github.henriquesabino.neunet.ga.GANeuralNetwork;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Snake extends entities.Snake {
    
    private GANeuralNetwork brain;
    private double fitness;
    private int movesLeft, lifeTime = 0;
    
    public Snake(Plane screen, float posX, float posY, MovementType movementType, int movesLeft) {
        super(screen, posX, posY, movementType);
        brain = new GANeuralNetwork(24, new int[]{16}, 4, 0.10);
        this.movesLeft = movesLeft;
        brain.setActivationFunctionsForHiddenLayer(Function.RELU);
        brain.setActivationFunctionsForOutputLayer(Function.SOFTMAX);
    }
    
    public Snake(List<Snake> prevGen, float posX, float posY, int movesLeft, int parentNum) {
        super(prevGen.get(0).screen, posX, posY, prevGen.get(0).getMovementType());
        
        List<Snake> parents = new ArrayList<>();
        
        List<Snake> copyPrevGen = new ArrayList<>(prevGen);
        
        for (int i = 0; i < parentNum; i++) {
            
            Snake selected = poolSelection(copyPrevGen);
            parents.add(selected);
            copyPrevGen.remove(selected);
        }
        
        List<GANeuralNetwork> brains = parents.stream().map(s -> s.brain).collect(Collectors.toList());
        
        //generates a brain based on the parents's brains
        brain = new GANeuralNetwork(brains);
        
        brain.setActivationFunctionsForHiddenLayer(Function.RELU);
        brain.setActivationFunctionsForOutputLayer(Function.SOFTMAX);
        
        this.movesLeft = movesLeft;
    }
    
    public void predict(Food food) {
        
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
        
        //It performed better without rotating the inputs with the head
        double[] temp = lookTo(new PVector(1, 0), food);
        fov[0] = temp[0];
        fov[8] = temp[1];
        fov[16] = temp[2];
        
        temp = lookTo(new PVector(1, -1), food);
        fov[1] = temp[0];
        fov[9] = temp[1];
        fov[17] = temp[2];
        
        temp = lookTo(new PVector(0, -1), food);
        fov[2] = temp[0];
        fov[10] = temp[1];
        fov[18] = temp[2];
        
        temp = lookTo(new PVector(-1, -1), food);
        fov[3] = temp[0];
        fov[11] = temp[1];
        fov[19] = temp[2];
        
        temp = lookTo(new PVector(-1, 0), food);
        fov[4] = temp[0];
        fov[12] = temp[1];
        fov[20] = temp[2];
        
        temp = lookTo(new PVector(-1, 1), food);
        fov[5] = temp[0];
        fov[13] = temp[1];
        fov[21] = temp[2];
        
        temp = lookTo(new PVector(0, 1), food);
        fov[6] = temp[0];
        fov[14] = temp[1];
        fov[22] = temp[2];
        
        temp = lookTo(new PVector(1, 1), food);
        fov[7] = temp[0];
        fov[15] = temp[1];
        fov[23] = temp[2];
        
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
        dists[0]++;
        
        while (!(pos.x < 0 || pos.x >= screen.getWidth() || pos.y < 0 || pos.y >= screen.getHeight())) {
            
            if (!foundFood && pos.x == food.getPos().x && pos.y == food.getPos().y) {
                foundFood = true;
                dists[1]++;
            }
            
            if (!foundPart) {
                for (PVector part : getParts()) {
                    if (pos.x == part.x && pos.y == part.y) {
                        foundPart = true;
                        dists[2]++;
                    }
                }
            }
            
            pos.add(dir);
            dists[0]++;
        }
        
        dists[0] /= norm;
        
        /* If the food distance inputs are not zero when the food is not in the direction
         * It would be the same value if the wall or the food were close to the snake,
         * same for the body parts
         */
        if (!foundFood)
            dists[1] = 0;
        if (!foundPart)
            dists[2] = 0;
        
        return dists;
    }
    
    @Override
    public void update() {
        super.update();
        if (!isDead()) {
            lifeTime++;
            movesLeft--;
        }
        
        if (movesLeft == 0)
            dead = true;
    }
    
    @Override
    public void grow() {
        super.grow();
        //longer snakes can move more than shorter
        movesLeft += screen.getWidth() * getParts().size() / 2;
    }
    
    //Code-Bullet's calcFitness function altered
    public void calcFitness() {
        
        int len = getParts().size();
        
        //fitness is based on length and lifetime
        if (len < 10) {
            fitness = Math.pow(lifeTime, 2) * Math.pow(len, 3);
        } else {
            //grows slower after 10 to stop fitness from getting stupidly big
            //ensure greater than len = 9
            fitness = lifeTime * lifeTime;
            fitness *= Math.pow(3, 7);
            fitness *= (len - 9);
        }
    }
    
    //Coding train pool selection
    private Snake poolSelection(List<Snake> prevGen) {
        // Start at 0
        int index = 0;
        
        // Pick a random number between 0 and 1
        double random = Math.random();
        
        // Keep subtracting probabilities until you get less than zero
        // Higher probabilities will be more likely to be fixed since they will
        // subtract a larger number towards zero
        while (random > 0 && index < prevGen.size()) {
            random -= prevGen.get(index).getFitness();
            // And move on to the next
            index++;
        }
        
        // Go back one
        index--;
        return prevGen.get(index);
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
