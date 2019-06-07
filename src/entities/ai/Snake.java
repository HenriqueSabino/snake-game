package entities.ai;

import entities.screens.Plane;
import io.github.henriquesabino.math.services.Function;
import io.github.henriquesabino.neunet.ga.GANeuralNetwork;
import processing.core.PVector;

public class Snake extends entities.Snake {
    
    GANeuralNetwork brain;
    
    public Snake(Plane screen, float posX, float posY, MovementType movementType) {
        super(screen, posX, posY, movementType);
        brain = new GANeuralNetwork(24, new int[]{16}, 4, 0.1);
        brain.setActivationFunctionsForHiddenLayer(Function.RELU);
        brain.setActivationFunctionsForOutputLayer(Function.SOFTMAX);
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
    private double[] fov() {
        
        double[] fov = new double[24];
        
        double[] walls = lookWalls();
        System.arraycopy(walls, 0, fov, 0, 8);
        
        return fov;
    }
    
    private double[] lookWalls() {
        
        double[] dists = new double[8];
        
        //normalizing the position values
        PVector pos = getParts().get(0).copy();
        pos.x /= screen.getWidth();
        pos.y /= screen.getHeight();
        
        PVector newDir = new PVector(dir.x, -dir.y);
        
        int init = (int) Math.floor((newDir.heading() / 2 * Math.PI) / (Math.PI / 4));
        
        /*
         * Above this comment is the math to rotate the rays with the snake
         *
         * up diagonal passing through snake's head: y = x - pos.x + pos.y, so x = y + pos.x - pos.y
         * down diagonal passing through snake's head: y = -x + pos.x + pos.y, so x = -y + pos.x + pos.y
         * NOTE: the diagonal calculation can be a little bit tricky because the Y is inverted on processing,
         *          I might change the code of the class Plane to fix that
         */
        
        dists[init % 8] = 1 - pos.x;
        
        //Calculating diagonal 1
        PVector interX = intersection(-1, pos.x + pos.y, 0, 1, true);
        PVector interY = intersection(-1, pos.x + pos.y, 0, 1, false);
        
        dists[(1 + init) % 8] = (!(interX.x < 0)) ? PVector.dist(pos, interX) : PVector.dist(pos, interY);
        //end of calculation
        
        dists[(2 + init) % 8] = pos.y;
        
        //Calculating diagonal 2
        interX = intersection(1, -pos.x + pos.y, 0, 1, true);
        interY = intersection(1, pos.x - pos.y, 0, 0, false);
        
        dists[(3 + init) % 8] = (!(interX.x < 0)) ? PVector.dist(pos, interX) : PVector.dist(pos, interY);
        //end of calculation
        
        dists[(4 + init) % 8] = pos.x;
        
        //Calculating diagonal 3
        interX = intersection(-1, pos.x + pos.y, 0, 0, true);
        interY = intersection(-1, pos.x + pos.y, 0, 0, false);
        
        dists[(5 + init) % 8] = (!(interX.x < 0)) ? PVector.dist(pos, interX) : PVector.dist(pos, interY);
        //end of calculation
        
        dists[(6 + init) % 8] = 1 - pos.y;
        
        //Calculating diagonal 4
        interX = intersection(1, -pos.x + pos.y, 0, 0, true);
        interY = intersection(1, pos.x - pos.y, 0, 1, false);
        
        dists[(7 + init) % 8] = (!(interX.x < 0)) ? PVector.dist(pos, interX) : PVector.dist(pos, interY);
        //end of calculation
        
        return dists;
    }
    
    private PVector intersection(float m, float b, float n, float c, boolean horizontal) {
        /*
         * the x coordinate of the intersection of two distinct lines described as
         * f(x) = mx + b and g(x) = nx + c is equal to px = (c - b) / (m - n)
         * the y coordinated is just the evaluation of f(px) or g(px)
         * desmos link to the proof https://www.desmos.com/calculator/ng2frqkwcz
         * same concept can be used when y is shared by two lines, like a slope and a vertical line
         */
        
        float point = (c - b) / (m - n);
        PVector intersection = new PVector();
        intersection.x = (horizontal) ? point : (m * point + b);
        intersection.y = (horizontal) ? (m * point + b) : point;
        
        return intersection;
    }
}
