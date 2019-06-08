package entities.ai;

import entities.Food;
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
    private double[] fov(Food food) {
        
        double[] fov = new double[24];
        
        double[] temp = lookWalls();
        System.arraycopy(temp, 0, fov, 0, 8);
        
        temp = lookFood(food);
        System.arraycopy(temp, 0, fov, 8, 8);
        
        temp = lookParts();
        System.arraycopy(temp, 0, fov, 16, 8);
        
        return fov;
    }
    
    private double[] lookWalls() {
        
        double[] dists = new double[8];
        
        //normalizing the position values
        PVector pos = getParts().get(0).copy();
        pos.x /= screen.getWidth();
        pos.y /= screen.getHeight();
        //To normalize the array
        double invSqrt2 = Math.sqrt(2) / 2;
        
        double angle = (dir.heading() < 0) ? Math.PI - dir.heading() : dir.heading();
        int offset = (int) Math.floor(angle % (2 * Math.PI) / (Math.PI / 4));
        
        /*
         * Above this comment is the math to rotate the rays with the snake
         *
         * up diagonal passing through snake's head: y = x - pos.x + pos.y, so x = y + pos.x - pos.y
         * down diagonal passing through snake's head: y = -x + pos.x + pos.y, so x = -y + pos.x + pos.y
         * NOTE: the diagonal calculation can be a little bit tricky because the Y is inverted on processing,
         *          I might change the code of the class Plane to fix that
         *
         * link to p5 simulation of this code with random points and user input of direction
         * https://editor.p5js.org/h.sabinocosta/full/hoyumx-5E
         */
        
        dists[offset % 8] = 1 - pos.x;
        
        //Calculating diagonal 1
        PVector interX = intersection(-1, pos.x + pos.y, 0, 0, true);
        PVector interY = intersection(-1, pos.x + pos.y, 0, 1, false);
        
        dists[(1 + offset) % 8] = (interX.x > 1) ? PVector.dist(pos, interY) : PVector.dist(pos, interX);
        //end of calculation
        
        dists[(2 + offset) % 8] = pos.y;
        
        //Calculating diagonal 2
        interX = intersection(1, -pos.x + pos.y, 0, 0, true);
        interY = intersection(1, pos.x - pos.y, 0, 0, false);
        
        dists[(3 + offset) % 8] = (interX.x < 0) ? PVector.dist(pos, interY) : PVector.dist(pos, interX);
        //end of calculation
        
        dists[(4 + offset) % 8] = pos.x;
        
        //Calculating diagonal 3
        interX = intersection(-1, pos.x + pos.y, 0, 1, true);
        interY = intersection(-1, pos.x + pos.y, 0, 0, false);
        
        dists[(5 + offset) % 8] = (interX.x < 0) ? PVector.dist(pos, interY) : PVector.dist(pos, interX);
        //end of calculation
        
        dists[(6 + offset) % 8] = 1 - pos.y;
        
        //Calculating diagonal 4
        interX = intersection(1, -pos.x + pos.y, 0, 1, true);
        interY = intersection(1, pos.x - pos.y, 0, 1, false);
        
        dists[(7 + offset) % 8] = (interX.x > 1) ? PVector.dist(pos, interY) : PVector.dist(pos, interX);
        //end of calculation
        
        for (int i = 0; i < dists.length; i++) {
            dists[i] *= invSqrt2;
        }
        
        return dists;
    }
    
    private double[] lookFood(Food food) {
        
        double[] dists = new double[8];
        
        //normalizing the position values
        PVector pos = getParts().get(0).copy();
        pos.x /= screen.getWidth();
        pos.y /= screen.getHeight();
        
        PVector foodPos = food.getPos().copy();
        foodPos.x /= screen.getWidth();
        foodPos.y /= screen.getHeight();
        //To normalize the array
        double invSqrt2 = Math.sqrt(2) / 2;
        
        double angle = (dir.heading() < 0) ? Math.PI - dir.heading() : dir.heading();
        int offset = (int) Math.floor(angle % (2 * Math.PI) / (Math.PI / 4));
        
        dists[offset % 8] = (foodPos.y == pos.y && foodPos.x > pos.x) ? foodPos.x - pos.x : 0;
        
        //Calculating diagonal 1
        
        //Checking if the diagonal line contains the food position
        boolean inDiag = foodPos.y == -foodPos.x + pos.x + pos.y;
        
        dists[(1 + offset) % 8] = (inDiag && foodPos.x > pos.x) ? PVector.dist(pos, foodPos) : 0;
        //end of calculation
        
        dists[(2 + offset) % 8] = (foodPos.x == pos.x && foodPos.y > pos.y) ? foodPos.y - pos.x : 0;
        
        //Calculating diagonal 2
        
        inDiag = foodPos.y == foodPos.x - pos.x + pos.y;
        
        dists[(3 + offset) % 8] = (inDiag && foodPos.x < pos.x) ? PVector.dist(pos, foodPos) : 0;
        //end of calculation
        
        dists[(4 + offset) % 8] = (foodPos.y == pos.y && foodPos.x < pos.x) ? pos.x - foodPos.x : 0;
        
        //Calculating diagonal 3
        
        inDiag = foodPos.y == -foodPos.x + pos.x + pos.y;
        
        dists[(5 + offset) % 8] = (inDiag && foodPos.x < pos.x) ? PVector.dist(pos, foodPos) : 0;
        //end of calculation
        
        dists[(6 + offset) % 8] = (foodPos.x == pos.x && foodPos.y < pos.y) ? pos.y - foodPos.y : 0;
        
        //Calculating diagonal 4
        
        inDiag = foodPos.y == foodPos.x - pos.x + pos.y;
        
        dists[(7 + offset) % 8] = (inDiag && foodPos.x > pos.x) ? PVector.dist(pos, foodPos) : 0;
        //end of calculation
        
        for (int i = 0; i < dists.length; i++) {
            dists[i] *= invSqrt2;
        }
        
        return dists;
    }
    
    private double[] lookParts() {
        
        double[] dists = new double[8];
        //To normalize the array
        double invSqrt2 = Math.sqrt(2) / 2;
        
        
        for (PVector part : getParts()) {
            
            //normalizing the position values
            PVector pos = getParts().get(0).copy();
            pos.x /= screen.getWidth();
            pos.y /= screen.getHeight();
            
            PVector partPos = part.copy();
            partPos.x /= screen.getWidth();
            partPos.y /= screen.getHeight();
            
            double angle = (dir.heading() < 0) ? Math.PI - dir.heading() : dir.heading();
            int offset = (int) Math.floor(angle % (2 * Math.PI) / (Math.PI / 4));
            
            float temp = (partPos.y == pos.y && partPos.x > pos.x) ? partPos.x - pos.x : 2;
            dists[offset % 8] = (temp < dists[offset % 8]) ? temp : dists[offset % 8];
            
            //Calculating diagonal 1
            
            //Checking if the diagonal line contains the food position
            boolean inDiag = partPos.y == -partPos.x + pos.x + pos.y;
            
            temp = (inDiag && partPos.x > pos.x) ? PVector.dist(pos, partPos) : 2;
            dists[(1 + offset) % 8] = (temp < dists[(1 + offset) % 8]) ? temp : dists[(1 + offset) % 8];
            //end of calculation
            
            temp = (partPos.x == pos.x && partPos.y > pos.y) ? partPos.y - pos.x : 2;
            dists[(2 + offset) % 8] = (temp < dists[(2 + offset) % 8]) ? temp : dists[(2 + offset) % 8];
            
            //Calculating diagonal 2
            
            inDiag = partPos.y == partPos.x - pos.x + pos.y;
            
            temp = (inDiag && partPos.x < pos.x) ? PVector.dist(pos, partPos) : 2;
            dists[(3 + offset) % 8] = (temp < dists[(3 + offset) % 8]) ? temp : dists[(3 + offset) % 8];
            //end of calculation
            
            temp = (partPos.y == pos.y && partPos.x < pos.x) ? pos.x - partPos.x : 2;
            dists[(4 + offset) % 8] = (temp < dists[(4 + offset) % 8]) ? temp : dists[(4 + offset) % 8];
            
            //Calculating diagonal 3
            
            inDiag = partPos.y == -partPos.x + pos.x + pos.y;
            
            temp = (inDiag && partPos.x < pos.x) ? PVector.dist(pos, partPos) : 2;
            dists[(5 + offset) % 8] = (temp < dists[(5 + offset) % 8]) ? temp : dists[(5 + offset) % 8];
            //end of calculation
            
            temp = (partPos.x == pos.x && partPos.y < pos.y) ? pos.y - partPos.y : 2;
            dists[(6 + offset) % 8] = (temp < dists[(6 + offset) % 8]) ? temp : dists[(6 + offset) % 8];
            
            //Calculating diagonal 4
            
            inDiag = partPos.y == partPos.x - pos.x + pos.y;
            
            temp = (inDiag && partPos.x > pos.x) ? PVector.dist(pos, partPos) : 2;
            dists[(7 + offset) % 8] = (temp < dists[(7 + offset) % 8]) ? temp : dists[(7 + offset) % 8];
            //end of calculation
        }
        
        for (int i = 0; i < 8; i++) {
            dists[i] *= invSqrt2;
        }
        
        return dists;
    }
    
    private void predict(Food food) {
        double[] prediction = brain.predict(fov(food));
        
        int max = 0;
        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] > prediction[max])
                max = i;
        }
        
        switch (max) {
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
