package lightpos;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * LightPos_API
 * @author Reuben Sonnenberg, Tung Nguyen, Dong Nguyen
 * 
 * Author Name          Email Address
 * Reuben Sonnenberg    rjsonnenberg@alaska.edu
 * Tung Nguyen          ttnguyen4@alaska.edu
 * Done Nguyen          dlnguyen@alaska.edu
 * 
 * Final Programming Assignment
 * Goal: evolve a solution x = {L1, L2, ..., Ln}, where Li represents a "Light"
 * with the following parameters:
 *      pos_x:  x position of light within room (int)
 *      pos_y:  y position of light within room (int)
 *      watts:  wattage/brightness metric (int)
 *      power:  whether the light is on/off (bool)
 * 
 * The solution will maximizes the following function:
 *      f(L1, L2, ..., Ln)
 * where f is the fitness of the solution subject to the following constraints:
 *      pos_x:  within the bounds of the room width
 *      pos_y:  within the bounds of the room height
 *      intensity:  candella values from list (enumerable)
 *      power:  if set to False (off), the light is not considered in result
 * 
 * Control parameters:
 *      μ = 10                      (number of parents)
 *      λ = 100                     (number of offspring)
 *      (μ, λ) selection            (the best μ offspring replace the parents)
 *      σ0 = 1                      (initial value of the mutation step size in 
 *                                  each dimension)
 *      termination count = 100,000 (stop the run after 10,000 fitness 
 *                                  evaluations)
 *      τ’ = 1 / sqrt(2 * n)        (the overall learning rate)
 *      τ = 1 / sqrt(2 * sqrt(n))   (the coordinate-specific learning rate)
 * NOTE: may need to use larger values for μ and λ, or increase (or decrease) 
 * the termination count.
 * 
 * 
 */
public class LightPos_API {
    private final light[][] parents; // 2D array: 1st = number, 2nd = per result
    private final light[][] children;
    private final double mutationInitialStepSize;
    private final double[] mutationStepSize;
    private int terminationCount;
    private final int nNumber; // number of lights
    private final int pNumber = 4; // number of light parameters
    private final double overallLearningRate;
    private final double coordinateLearningRate;
    private final int roomWidth;
    private final int roomLength;

    // Randomization generator
    private final Random generatorRandom = new Random();
    
    /**
     * getBestSolution
     * @return Returns a parameter set that gives the best fitness.
     */
    public light[] getBestSolution() {
        int fitIndex;
        double curFitness;
        double bestPerRunFitness = -Double.MAX_VALUE;
        double bestFitness = -Double.MAX_VALUE;
        light[] bestPerRunSolution = {};
        light[] bestSolution = {};
        int noProgressCount = 0;
        
        init();
        
        // The out loop will keep doing more runs after one run has stagnated
        // until the termination counter has reached zero.
        do
        {
            //init();
            resetMutationStepSize();
            bestPerRunFitness = -Double.MAX_VALUE;
            noProgressCount = 0;
            do {
                noProgressCount++;
                generateOffspring();
                selectParents();
                fitIndex = getFittestIndex(parents);
                curFitness = getFitness(parents[fitIndex]);

                //Check to see if it beats the current best solution
                if (curFitness > bestPerRunFitness)
                {
                    noProgressCount = 0; // reset no progress counter
                    System.out.println("\nCurrent run best fitness: " + curFitness);
                    bestPerRunFitness = curFitness;
                    bestPerRunSolution = parents[fitIndex];
                }

                System.out.print(".");
                terminationCount--;
            } while (terminationCount > 0 && noProgressCount < 150);
            
            if (bestPerRunFitness > bestFitness) {
                bestFitness = bestPerRunFitness;
                bestSolution = bestPerRunSolution;
                System.out.println("\nCurrent best overall fitness: " + bestFitness);
            }
            
            if (terminationCount > 0)
            {
                //System.out.println("\nStarting another run...");
                System.out.println("\nResetting mutation step size...");
            }
        }while(terminationCount > 0);
        
        
        return bestSolution;
    }
    
    /** 
     * init
     * Description: Set parents to randomized values between the given bounds:
     */
    private void init() {
        // Initialize parent values:
        for (light[] parent : parents) {
            for (int i = 0; i < nNumber; i++) {
                parent[i] = new light(
                        generatorRandom.nextInt(roomWidth+1),
                        generatorRandom.nextInt(roomLength+1),
                        generatorRandom.nextInt(5),
                        generatorRandom.nextBoolean());
            }
        }
    }
    
    //Reset the mutation step size
    private void resetMutationStepSize(){
        for (int i = 0; i < pNumber; i++) {
            mutationStepSize[i] = mutationInitialStepSize;
        }
    }
    
    /**
     * generateOffspring
     * Description: Apply global recombination to create all child parameter
     * sets. Mutate each of the new child sets.
     */
    private void generateOffspring()
    {
        // Apply global recobmination to create all child sets
        for (int i = 0; i < children.length; i++) {
            children[i] = recombine(
                    parents[generatorRandom.nextInt(parents.length)], 
                    parents[generatorRandom.nextInt(parents.length)]);
        }
        
        // Use uncorrelated mutations with n step sizes to modify each of the
        // offspring produced via recombination (above).
        for (int i = 0; i < children.length; i++) {
            children[i] = mutate(children[i]);
        }
    }
    
    /**
     * selectParents
     * Description: Gets the fitness of all the children, sorts the resulting
     * fitness value/child index pairs, and sets all the parent parameter sets
     * to the child sets that have the highest fitness.
     */
    private void selectParents()
    {
        // Get the fitness of all the children
        double [][]currentFitness = new double[children.length][2];
        for (int i = 0; i < children.length; i++) {
            currentFitness[i][0] = getFitness(children[i]);
            if (Double.isNaN(currentFitness[i][0]))
            {
                // If, for whatever reason, an invalid value is found
                currentFitness[i][0] = -Double.MAX_VALUE;
            }
            currentFitness[i][1] = i;
        }
        // Sort the children by fitness
        Arrays.sort(currentFitness, (double[] o1, double[] o2) -> 
                Double.compare(o1[0], o2[0]));
        
        // Select the children with the best fitness to succeed the parents
        // We assume that the number of parents is ALWAYS less than the number 
        // of children.
        for (int i = 0; i < parents.length; i++) {
            parents[i] = children[
                    (int)(currentFitness[children.length-i-1][1])];
        }
    }
    
    /**
     * recombine
     * @param x light array representing a solution
     * @param y light array representing a solution
     * @return Return either x or y (discrete recombination)
     */
    private light[] recombine(light[] x, light[] y) {
        if (generatorRandom.nextBoolean())
            return x;
        else
            return y;
    }
    
    /**
     * mutate
     * @param individual light array representing a solution
     * @return Returns the modified solution. Uses method from page 76.
     * Uncorrelated mutation with n step sizes.
     * 
     * Will not return until the hard-coded parameter bound requirements have
     * been met (see code).
     * 
     * NOTE: This is a template from the previous project. It will need to be
     * modified to work with Light objects. Might use a separate class for the
     * Lights.
     * 
     * It should pick the values, then create the light last, assigning the 
     * created values to the light object.
     * 
     * Because this method deals in primarily double values, when it creates the
     * light it converts (rounding) to integer and boolean values. This is an
     * approximation that may need to be adjusted.
     */
    private light[] mutate(light[] solution)
    {
        double ithNormal;
        light[] resultSolution = new light[nNumber];
        light individualLight;
        double[] oldIndividual = new double[4], newIndividual = new double[4];
        double oldStepSize;
        
        // Hard-coded parameter bounds (inclusive)
        // first: x position (inches)
        // second: y position (inches)
        // third: intesity option (there are 5 choices)
        double[] minVal = {0, 0, 0, 0};
        double[] maxVal = {roomWidth, roomLength, 4, 1};
        
        for (int a = 0; a< solution.length; a++) {
            individualLight = solution[a];
            oldIndividual[0] = individualLight.getPos_x();
            oldIndividual[1] = individualLight.getPos_y();
            oldIndividual[2] = individualLight.getIntensityOp();
            oldIndividual[3] = individualLight.isOn() ? 1 : 0 ;

            // mutate position parameters
            for (int i = 0; i < pNumber; i++) {
                // Force it to repeat until the mutation is within bounds
                oldStepSize = mutationStepSize[i];
                do {
                    // Make sure to return to original if it needs to retry the step
                    mutationStepSize[i] = oldStepSize;
                    // Get the ith value from a normal distribution
                    ithNormal = generatorRandom.nextGaussian();

                    // Mutate the step size
                    mutationStepSize[i] = mutationStepSize[i] * (Math.pow( 
                            Math.E, (overallLearningRate * 
                                    generatorRandom.nextGaussian())
                                    + (coordinateLearningRate * ithNormal)));

                    // Mutate the individual using modified step size
                    newIndividual[i] = oldIndividual[i] + 
                            (mutationStepSize[i] * ithNormal);
                } while (!(minVal[i] <= newIndividual[i] 
                        && newIndividual[i] <= maxVal[i]));
            }

            light resultLight = new light(
                    (int) Math.round(newIndividual[0]),
                    (int) Math.round(newIndividual[1]),
                    (int) Math.round(newIndividual[2]),
                    (Math.round(newIndividual[3]) == 1.0));
            
            resultSolution[a] = resultLight;
        }
        
        return resultSolution;
    }
    
    /**
     * getFittestIndex
     * @param individuals Double array representing an array of sets of values
     * @return Returns the index of the fittest solution in the given array.
     */
    private int getFittestIndex(light [][] solutions)
    {
        int fittestIndex = 0;
        double bestFitnes = -Double.MAX_VALUE;
        double currentFitness;
        for (int i = 0; i < solutions.length; i++) {
            currentFitness = getFitness(solutions[i]);
            if ( Double.compare(currentFitness, bestFitnes) > 0)
            {
                fittestIndex = i;
                bestFitnes = currentFitness;
            }
        }
        return fittestIndex;
    }
    
    /**
     * getFitness
     * @param solution Double value that represents a given parameter set
     * @return Returns the fitness of an given parameter set after plugging in 
     * the solution parameters into the hard-coded equation.
     * 
     * The fitness functions (Excel and such) go here.
     * This function will use the overall lighting value (good), the variation 
     * in the lighting (bad), the amount of energy used (bad), and the number of
     * lights (less lights = good).
     * 
     * There are multiple steps to this fitness problem that can be broken down
     * into separate methods:
     * 
     *      1. Use sensor grid to calculate how much light it is receiving from
     *          every light in the room that is on. This returns a grid of 
     *          values that are used in the next step as a 2D int array.
     *      2. Use the previous 2D int array and utilizes the overall lighting
     *          and variation to calculate and return a fitness value integer.
     *          This step is what the Excel file does.
     *      3. Finally, the last module divides the fitness value by the number
     *          of lights times the amount of energy used (watts) and returns
     *          the double as the final fitness value. The equation looks like
     *          the following:
     *              fitness = previousFitness / (numLights * overallWatts)
     *              
     */
    public double getFitness(light[] solution)
    {
        // If no lights are on, it is not a valid solution
        if (getOnLights(solution)==0) {
            return -Double.MAX_VALUE;
        }
        // Only do these calculations if there are lights on
        return (getFitnessHelper(getLightGrid(solution)) - getOnLights(solution) 
                - getSolutionWatts(solution));
    }
    
    private int getOnLights(light[] solution)
    {
        int lightsOnCount = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i].isOn()) {
                lightsOnCount++;
            }
        }
        return lightsOnCount;
    }
    
    // This is the first step, setting up the light grid and getting the light
    // intensity in candellas for each "sensor" point on the grid and returning
    // a 2D array of double values.
    private double[][] getLightGrid(light[] solution)
    {
        // divide the room up into a grid or 1 foot between each grid point
        int gridRows = (int) Math.floor(roomWidth / 12);
        int gridColumns = (int) Math.floor(roomLength / 12);
        // The offset from the origin to center the grid in the room in inches
        int originRowOffset = (roomWidth % 12) / 2; 
        int originColumnOffset = (roomLength % 12) / 2; 
        double[][] lightGrid = new double[gridRows][gridColumns];
        // Logic for getting the light intensity at every point
        // This is based on the following Excel file: 
        //      "LightCollectorAlgorithms.xlsx"
        double dist;
        int x1;
        int y1;
        int x2;
        int y2;
        
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {
                // lightGrid[i][j] = 0.0;
                // For the current cell of the grid
                for (int k = 0; k < solution.length; k++) {
                    // Get the distance from each light to myself
                    x1 = solution[k].getPos_x();
                    y1 = solution[k].getPos_y();
                    x2 = originRowOffset + (i * 12);
                    y2 = originColumnOffset + (j * 12);
                    dist = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
                    // This distance is the max horizontal distance of light
                    // given a 110 degree angle spread of the light from a 
                    // nine-foot ceiling to the floor.
                    if (dist <= 154.24)
                    {
                        if (dist >= 1.0)
                        {
                            //Add the light intensity, if it's close enough
                            lightGrid[i][j] += (1/dist)*solution[k].getIntensity();
                        }
                        else
                        {
                            //If it's too close, just add the intensity
                            lightGrid[i][j] += solution[k].getIntensity();
                        }
                    }
                }
            }
        }
        return lightGrid;
    }
    
    // This is step 2 in getting the fitness. It takes a 2D array of doubles and
    // calculates the different between one sensor and all adjacent sensors, it
    // then gets the total brightness. The difference of these two is the return
    // value:
    //      returnFitness = overall brightness - overall light variation
    private double getFitnessHelper(double[][] lightGrid)
    {
        double overallLightIntensity = 0.0;
        double overallLightVariation = 0.0;
        
        // This is based on the following Excel file: 
        //      "FitnessCalculator.xlsx"
        int rows = lightGrid.length;
        int cols = lightGrid[0].length;
        
        // Get the overall light by adding all the sensor inputs together
        // Inner points, then outer point
        for (int i = 1; i < rows-1; i++) {
            for (int j = 1; j < cols-1; j++) {
                overallLightIntensity += lightGrid[i][j];
                overallLightVariation += 
                        Math.abs(lightGrid[i][j] - lightGrid[i-1][j-1]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i-1][j]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i-1][j+1]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i][j+1]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i+1][j+1]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i+1][j]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i+1][j-1]) + 
                        Math.abs(lightGrid[i][j] - lightGrid[i][j-1]);
            }
        }
        // First column, minus the corners
        for (int i = 1; i < rows-1; i++) {
            overallLightIntensity += lightGrid[i][0];
            overallLightVariation += 
                    Math.abs(lightGrid[i][0] - lightGrid[i-1][0]) + 
                    Math.abs(lightGrid[i][0] - lightGrid[i-1][1]) + 
                    Math.abs(lightGrid[i][0] - lightGrid[i][1]) + 
                    Math.abs(lightGrid[i][0] - lightGrid[i+1][1]) + 
                    Math.abs(lightGrid[i][0] - lightGrid[i+1][0]);
        }
        // Last column, minus the corners
        for (int i = 1; i < rows-1; i++) {
            overallLightIntensity += lightGrid[i][cols-1];
            overallLightVariation += 
                    Math.abs(lightGrid[i][cols-1] - lightGrid[i-1][cols-1]) + 
                    Math.abs(lightGrid[i][cols-1] - lightGrid[i-1][cols-2]) + 
                    Math.abs(lightGrid[i][cols-1] - lightGrid[i][cols-2]) + 
                    Math.abs(lightGrid[i][cols-1] - lightGrid[i+1][cols-2]) + 
                    Math.abs(lightGrid[i][cols-1] - lightGrid[i+1][cols-1]);
        }
        // First row, minus the corners
        for (int j = 1; j < cols-1; j++) {
            overallLightIntensity += lightGrid[0][j];
            overallLightVariation += 
                    Math.abs(lightGrid[0][j] - lightGrid[0][j-1]) + 
                    Math.abs(lightGrid[0][j] - lightGrid[1][j-1]) + 
                    Math.abs(lightGrid[0][j] - lightGrid[1][j]) + 
                    Math.abs(lightGrid[0][j] - lightGrid[1][j+1]) + 
                    Math.abs(lightGrid[0][j] - lightGrid[0][j+1]);
        }
        // Last row, minus the corners
        for (int j = 1; j < cols-1; j++) {
            overallLightIntensity += lightGrid[rows-1][j];
            overallLightVariation += 
                    Math.abs(lightGrid[rows-1][j] - lightGrid[rows-1][j-1]) + 
                    Math.abs(lightGrid[rows-1][j] - lightGrid[rows-2][j-1]) + 
                    Math.abs(lightGrid[rows-1][j] - lightGrid[rows-2][j]) + 
                    Math.abs(lightGrid[rows-1][j] - lightGrid[rows-2][j+1]) + 
                    Math.abs(lightGrid[rows-1][j] - lightGrid[rows-1][j+1]);
        }
        
        //top left corner
        overallLightIntensity += lightGrid[0][0];
        overallLightVariation += 
                    Math.abs(lightGrid[0][0] - lightGrid[0][1]) + 
                    Math.abs(lightGrid[0][0] - lightGrid[1][1]) + 
                    Math.abs(lightGrid[0][0] - lightGrid[1][0]);
        
        //top right corner
        overallLightIntensity += lightGrid[0][cols-1];
        overallLightVariation += 
                    Math.abs(lightGrid[0][cols-1] - lightGrid[0][cols-2]) + 
                    Math.abs(lightGrid[0][cols-1] - lightGrid[1][cols-2]) + 
                    Math.abs(lightGrid[0][cols-1] - lightGrid[1][cols-1]);
        
        //bottom right corner
        overallLightIntensity += lightGrid[rows-1][cols-1];
        overallLightVariation += 
                    Math.abs(lightGrid[rows-1][cols-1] - lightGrid[rows-2][cols-1]) + 
                    Math.abs(lightGrid[rows-1][cols-1] - lightGrid[rows-2][cols-2]) + 
                    Math.abs(lightGrid[rows-1][cols-1] - lightGrid[rows-1][cols-2]);
        
        //bottom lwft corner
        overallLightIntensity += lightGrid[rows-1][0];
        overallLightVariation += 
                    Math.abs(lightGrid[rows-1][0] - lightGrid[rows-2][0]) + 
                    Math.abs(lightGrid[rows-1][0] - lightGrid[rows-2][1]) + 
                    Math.abs(lightGrid[rows-1][0] - lightGrid[rows-1][1]);
        
        return overallLightIntensity - overallLightVariation;
    }
    
    private int getSolutionWatts(light[] solution)
    {
        int totalWatts = 0;
        for (int i = 0; i < nNumber; i++) {
            if (solution[i].isOn()) {
                totalWatts += solution[i].getWatts();
            }
        }
        return totalWatts;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////CONSTRUCTORS/////////////////////////////////
    // Maximizer_API
    public LightPos_API()
    {
        nNumber = 8; //number of lights in each solution
        parents = new light[10][nNumber];
        children = new light[100][nNumber];
        mutationInitialStepSize = 1;
        roomWidth = 240;
        roomLength = 120;
        terminationCount = 10000;
        overallLearningRate= 1.0 / Math.sqrt(2. * nNumber * 4);
        coordinateLearningRate = 1.0 / Math.sqrt(2 * Math.sqrt(nNumber));
        mutationStepSize = new double[pNumber];
        for (int i = 0; i < pNumber; i++) {
            mutationStepSize[i] = mutationInitialStepSize;
        }
    }
    
    // Full Constructor
    public LightPos_API(int numParents, int numChildren, int n, int width,
            int height, double mutationStSz, int terminationNumber)
    {
        nNumber = n; // There are n max lights in this solution
        parents = new light[numParents][nNumber];
        children = new light[numChildren][nNumber];
        mutationInitialStepSize = mutationStSz;
        terminationCount = terminationNumber;
        roomWidth = width;
        roomLength = height;
        overallLearningRate= 1.0 / Math.sqrt(2. * nNumber * 4);
        coordinateLearningRate = 1.0 / Math.sqrt(2 * Math.sqrt(nNumber));
        mutationStepSize = new double[pNumber];
        for (int i = 0; i < pNumber; i++) {
            mutationStepSize[i] = mutationInitialStepSize;
        }
    }
}
