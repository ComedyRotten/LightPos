package lightpos;
import java.util.Scanner;
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
    private final int pNumber = 4; // number of lights
    private final double overallLearningRate;
    private final double coordinateLearningRate;
    private final int roomWidth;
    private final int roomHeight;
      private int row = 7;
    private int col = 7;
    private int [][] matrix;
    
    // Randomization generator
    private final Random generatorRandom = new Random();
    
    /**
     * getBestSolution
     * @return Returns a parameter set that gives the best fitness.
     */
    public light[] getBestSolution() {

        init();
        int fitIndex;
        double curFitness;
        double bestFitness = Double.MAX_VALUE;
        light[] bestSolution = {};
        
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
            currentFitness[i][1] = i;
        }
        // Sort the children by fitness
        Arrays.sort(currentFitness, (double[] o1, double[] o2) -> 
                Double.compare(o1[0], o2[0]));
        
        // Select the children with the best fitness to succeed the parents
        if (parents.length <= children.length)
        {
            for (int i = 0; i < parents.length; i++) {
                parents[i] = children[
                        (int)(currentFitness[children.length-i-1][1])];
            }
        }
        else{
            int index = children.length - 1;
            int parentsLeft = parents.length;
            do
            {
                if (index == 0)
                    index = children.length - 1;
                
                parents[index] = children[
                        (int)(currentFitness[children.length-index][1])];
                parentsLeft--;
            }while (parentsLeft > 0);
        }
    }
    
    /**
     * recombine
     * @param x Double array representing a parameter set
     * @param y Double array representing a parameter set
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
        for (int a = 0; a< solution.length; a++) {
            individualLight = solution[a];
            double[] oldIndividual = { individualLight.getPos_x(), 
                individualLight.getPos_y(), individualLight.getIntensityOp(), 
                individualLight.isOn() ? 1 : 0};
            double[] newIndividual = new double[4];

            // Hard-coded parameter bounds (inclusive)
            // first: x position (inches)
            // second: y position (inches)
            // third: intesity option (there are 5 choices)
            double[] minVal = {0, 0, 0, 0};
            double[] maxVal = {roomWidth, roomHeight, 4, 1};

            // mutate position parameters
            double oldStepSize;
            for (int i = 0; i < pNumber; i++) {
                // Force it to repeat until the mutation is within bounds
                oldStepSize = mutationStepSize[i];
                do {
                    // Make sure to return to original if it needs to retry the step
                    mutationStepSize[i] = oldStepSize;
                    // Get the ith value from a normal distribution
                    ithNormal = generatorRandom.nextGaussian();

                    // Mutate the step size
                    mutationStepSize[i] = (int) Math.round(
                            mutationStepSize[i] * (
                                    Math.pow( Math.E, (overallLearningRate * 
                                                    generatorRandom.nextGaussian())
                                                    + (coordinateLearningRate * 
                                                            ithNormal))));

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
                    ((int) Math.round(newIndividual[0]) == 1));
            
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
        int sum1 =0;
        int sum2 = 0;
        
        
        
        //Create 2D array to input values for Sensor
        int[][] inputSensor = new int[row][col];
        Scanner in = new Scanner(System.in);
        for(int row = 0; row< matrix.length; row++){ 
              for(int col = 0 ;col< matrix[row].length; col++){ 
                   System.out.println("enter the elements for the Input Sensor"); 
                  inputSensor[row][col] = in.nextInt(); 
               } System.out.println(); 
          } 

           for(int row = 0; row< matrix.length; row++){
       for(int col = 0 ;col< matrix[row].length; col++){ 
             System.out.println(inputSensor[row][col]);
       } 
      System.out.println(); 
         }
         
           // Create 2D array to imputvalues for calculated variation
           int[][] calVar = new int[row][col];
     
        for(int row = 0; row< matrix.length; row++){ 
              for(int col = 0 ;col< matrix[row].length; col++){ 
                   System.out.println("enter the elements for the Calculated Variation"); 
                  calVar[row][col] = in.nextInt(); 
               } System.out.println(); 
          } 

           for(int row = 0; row< matrix.length; row++){
       for(int col = 0 ;col< matrix[row].length; col++){ 
             System.out.println(calVar[row][col]);
       } 
      System.out.println(); 
         }
          
           //Sum all the rows and columns in the Sensor table
        int[] colSum1 =new int[inputSensor[0].length];
        for (int i = 0; i < inputSensor.length; i++){   
        for (int j = 0; j < inputSensor[i].length; j++){                
        sum1 += inputSensor[i][j];
        colSum1[j] += inputSensor[i][j];
    }
    System.out.println("The sum of rows in Sensor table =" + sum1);
    }  
    for(int k=0;k<colSum1.length;k++){
    System.out.println("The sum of columns in Sensor table =" + colSum1[k]);
    } 
         
    
            //Sum all the rows and columns in the Caculated Variation table
            int[] colSum2 =new int[calVar[0].length];
        for (int i = 0; i < calVar.length; i++){   
        for (int j = 0; j < calVar[i].length; j++){                
        sum2 += calVar[i][j];
        colSum2[j] += calVar[i][j];
    }
    System.out.println("The sum of rows in Caculated Variation table =" + sum2);
    }  
    for(int k=0;k<colSum2.length;k++){
        
    System.out.println("The sum of columns in Caculated Variation table =" + colSum2[k]);
    } 
        
           
                  
      
        int fittestIndex = 0;
        double bestFitnes = Double.MIN_VALUE;
        double currentFitness= sum1-sum2;               //current fitness is taken from substraction of total value from 
                                                        //table Sensor and Calculated Variation
        
        for (int i = 0; i < solutions.length; i++) {
            currentFitness = getFitness(solutions[i]);
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
    public double getFitness(light[] solution)
    {
        return (getFitnessHelper(getLightGrid(solution)) / 
                nNumber * getSolutionWatts(solution));
    }
    
    // This is the first step, setting up the light grid and getting the light
    // intensity in candellas for each "sensor" point on the grid and returning
    // a 2D array of double values.
    public double[][] getLightGrid(light[] solution)
    {
        int horizontalPoints = (int) Math.floor(roomWidth / 12);
        int verticalPoints = (int) Math.floor(roomHeight / 12);
        double[][] lightGrid = new double[horizontalPoints][verticalPoints];
        // Logic for getting the light intensity at every point
        // This is based on the following Excel file: 
        //      "LightCollectorAlgorithms.xlsx"
        
        return lightGrid;
    }
    
    // This is step 2 in getting the fitness. It takes a 2D array of doubles and
    // calculates the different between one sensor and all adjacent sensors, it
    // then gets the total brightness. The difference of these two is the return
    // value:
    //      returnFitness = overall brightness - overall light variation
    public double getFitnessHelper(double[][] lightGrid)
    {
        double result = 0.0;
        
        // This is based on the following Excel file: 
        //      "FitnessCalculator.xlsx"
        
        return result;
    }
    
    public int getSolutionWatts(light[] solution)
    {
        int totalWatts = 0;
        for (int i = 0; i < nNumber; i++) {
            totalWatts += solution[i].getWatts();
        }
        return totalWatts;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////CONSTRUCTORS/////////////////////////////////
    // Maximizer_API
    public LightPos_API()
    {
        nNumber = 6; //number of lights in each solution
        parents = new light[10][nNumber];
        children = new light[100][nNumber];
        mutationInitialStepSize = 1;
        roomWidth = 240;
        roomHeight = 120;
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
        roomHeight = height;
        overallLearningRate= 1.0 / Math.sqrt(2. * nNumber * 4);
        coordinateLearningRate = 1.0 / Math.sqrt(2 * Math.sqrt(nNumber));
        mutationStepSize = new double[pNumber];
        for (int i = 0; i < pNumber; i++) {
            mutationStepSize[i] = mutationStSz;
        }
    }
}
