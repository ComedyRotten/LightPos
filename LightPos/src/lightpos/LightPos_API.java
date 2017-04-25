package lightpos;

import java.util.Arrays;
import java.util.Random;

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
 *      watts:  nominal wattages/lumins from list (enumerable)
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
    private final light[] parents;
    private final light[] children;
    private final double mutationInitialStepSize;
    private final double[] mutationStepSize;
    private int terminationCount;
    private final int nNumber; // number of lights, each light has 4 parameters
    private final double overallLearningRate;
    private final double coordinateLearningRate;
    
    // Randomization generator
    private final Random generatorRandom = new Random();
    
    /**
     * getBestSolution
     * @return Returns a parameter set that gives the best fitness.
     */
    public double[] getBestSolution() {

        init();
        
        do {
            generateOffspring();
            selectParents();
            fitIndex = getFittestIndex(parents);
            curFitness = getFitness(parents[fitIndex]);
            
            //Check to see if it beats the current best solution
            if (curFitness > bestFitness)
            {
                bestFitness = curFitness;
                bestSolution = parents[fitIndex];
            }
            
            terminationCount--;
        } while (terminationCount > 0);
        
        return bestSolution;
    }
    
    /** 
     * init
     * Description: Set parents to randomized values between the given bounds:
     */
    private void init() {
        // Initialize parent values:
    }
    
    /**
     * generateOffsprint
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

        // Sort the children by fitness

        // Select the children with the best fitness to succeed the parents

    }
    
    /**
     * recombine
     * @param x Double array representing a parameter set
     * @param y Double array representing a parameter set
     * @return Return either x or y (discrete recombination)
     */
    private light recombine(light x, light y) {
        if (generatorRandom.nextBoolean())
            return x;
        else
            return y;
    }
    
    /**
     * mutate
     * @param individual Double array representing a parameter set
     * @return Returns the modified parameter set. Uses method from page 76.
     * Uncorrelated mutation with n step sizes.
     * 
     * Will not return until the hard-coded parameter bound requirements have
     * been met (see code).
     * 
     * NOTE: This is a template from the previous project. It will need to be
     * modified to work with Light objects. Might use a separate class for the
     * Lights.
     */
    private light mutate(light individual)
    {
        double ithNormal;
        light newIndividual = new light();
        
        // Hard-coded parameter bounds
        // double[] minVal = {-3.0, 4.0};
        // double[] maxVal = {12.0, 6.0};
        
        // mutate each parameter
        double oldStepSize;
        for (int i = 0; i < nNumber; i++) {
            // Force it to repeat until the mutation is within bounds
            oldStepSize = mutationStepSize[i];
            do {
                // Make sure to return to original if it needs to retry the step
                mutationStepSize[i] = oldStepSize;
                // Get the ith value from a normal distribution
                ithNormal = generatorRandom.nextGaussian();
                // Mutate the step size
                mutationStepSize[i] =  mutationStepSize[i] * (Math.pow(Math.E, 
                        (overallLearningRate * 
                                generatorRandom.nextGaussian()) 
                        + (coordinateLearningRate * ithNormal)));
                // Mutate the individual using modified step size
                newIndividual[i] = individual[i] + 
                        (mutationStepSize[i] * ithNormal);
            } while (!(minVal[i] <= newIndividual[i] 
                    && newIndividual[i] <= maxVal[i]));
        }
        
        return newIndividual;
    }
    
    /**
     * getFittestIndex
     * @param individuals Double array representing an array of sets of values
     * @return Returns the index of the fittest individual in the given array.
     */
    private int getFittestIndex(light [] individuals)
    {
        int fittestIndex = 0;
        double bestFitnes = Double.MIN_VALUE;
        double currentFitness;
        
        for (int i = 0; i < individuals.length; i++) {
            currentFitness = getFitness(individuals[i]);
            if (currentFitness>bestFitnes)
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
    public double getFitness(double [][] solution)
    {
        return 0.0;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////CONSTRUCTORS/////////////////////////////////
    // Maximizer_API
    public LightPos_API()
    {
        parents = new light[10];
        children = new light[100];
        mutationInitialStepSize = 1;
        terminationCount = 10000;
        nNumber = 6; //initially the number of lights
        overallLearningRate= 1.0 / Math.sqrt(2. * nNumber);
        coordinateLearningRate = 1.0 / Math.sqrt(2 * Math.sqrt(nNumber));
        mutationStepSize = new double[nNumber];
        for (int i = 0; i < nNumber; i++) {
            mutationStepSize[i] = mutationInitialStepSize;
        }
    }
    
    // Full Constructor
    public LightPos_API(int numParents, int numChildren, int n,
            double mutationStSz, int terminationNumber)
    {
        parents = new light[numParents];
        children = new light[numChildren];
        mutationInitialStepSize = mutationStSz;
        terminationCount = terminationNumber;
        nNumber = n; // There are n max lights in this solution
        overallLearningRate = (1.0 / Math.sqrt(2. * nNumber));
        coordinateLearningRate = (1.0 / Math.sqrt(2 * Math.sqrt(nNumber)));
        mutationStepSize = new double[nNumber];
        for (int i = 0; i < nNumber; i++) {
            mutationStepSize[i] = mutationStSz;
        }
    }
}
