/*
 * Author: Luke Zeng
 * Date of Creation: 2/7/2024
 *
 * Description: Fully connected Feed-Forward A-B-1 Neural Network with Gradient Descent learning.
 *              The way data is handled in this neural network is modeled after the way data is handled in
 *              a spreadsheet (this project's network is based on the spreadsheet 2-2-1 network).
 */


public class AB1
{

/******
 * Base network storage and activation function.
 */
   public static double[][] w1kj;      // edge weights between layers a and h.
   public static double[] w2j0;        // edge weights between layers h and F0 (1d array because there is only one output node)
   public static double F0;            // stores the single output activation.
   public static double[] ak;          // stores the input activations.
   public static double[] hj;          // stores the single hidden activation node values.
   public static ActivationFunction f; // activation function f(x).
   public static double[] outputs;     // stores the outputs from the sequential run function.

/******
 * Configuration Parameters
 */
   public static double lambda;         // step size multiplier for gradient descent.
   public static int numInputNodes;
   public static int numHiddenNodes;
   public static int numOutNodes;       // number of output nodes.
   public static int populationMethod;  // population method --> 0: randomize weights | 1: manually set weights.
   public static boolean train;         // if true, train. if false, run.
   public static double minRand;        // minimum random threshold
   public static double maxRand;        // maximum random threshold.
   public static double avgErrCut;      // average error cutoff (error threshold).
   public static int maxIterations;     // maximum iterations before cutoff.
   public static boolean hitThreshold;  // flag for program termination due to hitting error threshold.
   public static boolean printWeight;   // flag to indicate whether to print weights before running.
   public static boolean printTruths;   // flag to indicate whether to print the truth table.
   public static boolean runAfterTrain; // indicates whether the network should run after training.


/******
 * Truth Table Arrays
 */
   public static double[][] givenInputs;   // truth table inputs.
   public static double[] expectedOutputs; // truth table outputs.
   public static int numCases;             // number of rows (cases) in the truth table.

/******
 * Training Arrays/Variables
 * Lowercase greek letter spellings (like psi, omega) represent the lowercase greek letters.
 * Uppercase greek letter spellings (like PSI, OMEGA) represent the uppercase greek letters.
 */
   public static double[][] deltawkj;  // delta w for activation layer n = 1.
   public static double[] deltawj0;    // delta w for activation layer n = 2. (1d array because output layer only has 1 node)
   public static double psi0;
   public static double omega0;
   public static double T0;            // expected output, only really there to make the delta calculation look more like the notes
   public static double partialwkj;
   public static double partialwj0;
   public static double[] PSI;          // capital psi, referring to PSI sub j.
   public static double[] OMEGA;        // capital omega, referring to OMEGA sub j.
   public static int iterationsReached; // the amount of iterations reached during training.


/*
 * main function: initializes and runs/train the network based on configuration parameters.
 */
   public static void main(String[] args)
   {
      setConfig();
      echoConfig();
      allocate();
      populate();

      if (train)          // procedure if training
      {
         printStatus();   // prints a small status message letting user know that the program is training.
         train();         // trains the network.
         if(runAfterTrain)
         {
            run();        // runs the network after training to get the F0 values for each test case to compare with truth table.
         }
      }
      else                // procedure if running
      {
         if (printWeight) // if option is selected, print weights.
         {
            printWeights();
         }
         printStatus();   // prints a short status message which indicates that the program is running.
         run();           // runs the network for all cases and stores the outputs.
      }
      reportResults();    // print the result of the train/run.
   } // public static void main(String[] args)



/******
 * Driver methods
 */

/*
 * sets the configuration parameters of the network. (User can and should change this for their needs)
 */
   public static void setConfig()
   {
      maxIterations = 100000; // Maximum amount of iterations when training.
      avgErrCut = 0.0002;     // Error to stop at when training.
      minRand = -1.5;         // Minimum value for random population of weights.
      maxRand = 1.5;          // Maximum value for random population of weights.
      lambda = 0.3;           // Lambada factor --> multiplier used to adjust step size in training
      populationMethod = 0;   // 0 is random, 1 is populate from a set parameter.
      train = true;           // Training flag --> if set to true the network will train, if false the network will run.
      numInputNodes = 2;      // For this network will most likely be 2 but can functionally be any integer.
      numHiddenNodes = 2;     // For this network number of hidden nodes can be any value.
      numOutNodes = 1;        // For this network this value will always be 1.
      numCases = 4;           // Reflects the number of cases put into the input and output of truth table.
      printWeight = false;    // Flag that determines whether to print weights at the beginning of running.
      printTruths = true;     // Flag that determines whether to print truth table at the end of running/training.
      runAfterTrain = true;   // Should the network run and print run results after training (default is yes).
      f = new Sigmoid();      // Defines the activation function as a sigmoid
   } // public static void setConfig()



/*
 * populates the arrays of the network based on the configuration parameters.
 * the user should change arrays declarations for "ttsetin" & "ttsetout"; and "tempset1" & "tempset2" to change truth tables and
 * manually set weights respectively. This method makes it simpler and more intuitive for the user to set truth table inputs and
 * manually set activation weights. Make sure to change the population method to 1 (manually set weights) to set weights manually
 */
   public static void populate()
   {
      double[][] ttsetin = {{0.0, 0.0},
                            {0.0, 1.0},
                            {1.0, 0.0},
                            {1.0, 1.0}}; // temporary array to set truth table inputs.

      double[] ttsetout  =  {0.0,
                             0.0,
                             0.0,
                             1.0};       // temporary array to set truth table outputs. (indented to match truth table input)

      switch(populationMethod)                  // populates the weights of the network.
      {
         case 0:                                // random population
            populateRand();
            break;
         case 1:                                // manual population
            double[][] tempset1 = {{2.0, 1.0},
                                   {2.0, 1.0}}; // temporary matrix used to set the layer n = 1 weights manually
            double[] tempset2   =  {2.0, 1.0};  // temporary array used to set the layer n=2 weights manually.
            populateSet(tempset1, tempset2);
            break;
      } // switch(populationMethod)


      for (int cases = 0; cases < numCases; cases++)   //sets truth table to the values described in the above array declarations
      {
         for (int k = 0; k < numInputNodes; k++)
         {
            givenInputs[cases][k] = ttsetin[cases][k]; // set inputs in Truth Table.
         }
         expectedOutputs[cases] = ttsetout[cases];     // set the single value output in output Truth Table.
      }
   } // public static void populate()



/*
 * trains the network with gradient descent learning, then print out the weights obtained by training.
 * stores the amount of iterations the network took to train in a static field.
 * Will terminate if iterations exceeds maximum iterations defined in configuration parameters or if
 * an error threshold defined in configuration parameters is reached.
 *
 * an iteration consists of numCases cycles of delta w calculations and applications
 * (essentially, calculating and updating delta w for each training case is one iteration)
 */
   public static void train()
   {
      boolean done = false; // flag to indicate that training has finished.
      int cases;            // loop iterator for each case in training.
      int curiteration = 0; // current iteration of the training.
      double curerror;      // current error of the network.
      while(!done)
      {
         for (cases = 0; cases < numCases; cases++)
         {
            trainProcedure(cases); // sets inputs, runs network, calculates delta W, and applies delta W for a given case.
         }
         curerror = calcError();   // calculate the error to determine whether or not to end training early.

         // System.out.println("DEBUG" + curerror);
         if (curerror <= avgErrCut)
         {
            hitThreshold = true; // mark that training terminated for hitting error threshold.
            done = true;         // flag to indicate that training has finished.
         }

         if (++curiteration >= maxIterations)
         {
            hitThreshold = false; // mark that training terminated not from hitting the error threshold
            done = true;          // flag to indicate that training has finished.
         }
      } // while(!done)
      iterationsReached = curiteration; // stores the iterations reached for result reporting.
   } // public static void train()



/*
 * allocates the major network arrays for the network.
 */
   public static void allocate()
   {
      givenInputs = new double[numCases][numInputNodes];
      expectedOutputs = new double[numCases];

      if (train) // only allocate training-exclusive arrays if training.
      {
         PSI = new double[numHiddenNodes];
         OMEGA = new double[numHiddenNodes];

         deltawkj = new double[numInputNodes][numHiddenNodes];
         deltawj0 = new double[numHiddenNodes];
      }
      if ((train && runAfterTrain) || !train) // only allocate run-exclusive arrays if running or running after training.
      {
         outputs = new double[numCases];      // stores the outputs after running.
      }

      w1kj = new double[numInputNodes][numHiddenNodes]; // connections between activation layers n=1 and n=2.
      w2j0 = new double[numHiddenNodes];                // connections between activation layers n=2 and n=3.

      ak = new double[numInputNodes];  // stores input activation layer values
      hj = new double[numHiddenNodes]; // stores hidden activation layer values
   } // public static void allocate()

/*
 * runs the network and output the result.
 */
   public static void run()
   {
      for (int cases = 0; cases < numCases; cases++) // runs all the test cases from the truth table.
      {
         outputs[cases] = getOutput(givenInputs[cases]); // gets and stores the output of the truth table inputs.
         // System.out.println("DEBUG" + dumpVals());
      }
   } // public static void run()


/*
 * prints all the configuration parameters to the terminal.
 */
   public static void echoConfig()
   {
      printNetworkConfig(); // prints network configuration (A-B-1).
      System.out.println(); // newline for clarity.

      System.out.println("Configuration Parameters");
      System.out.println("number of input nodes: " + numInputNodes);
      System.out.println("number of hidden nodes: " + numHiddenNodes);
      System.out.println("number of output nodes: " + numOutNodes);
      System.out.println("population method: " + populationMethod);
      System.out.println("activation function: " + f.toString()); // prints the activation function's type.

      if (train) // training-exclusive parameters.
      {
         System.out.println();
         System.out.println("Training Exclusive Parameters: ");
         System.out.println("minRand: " + minRand);
         System.out.println("maxRand: " + maxRand);
         System.out.println("average error cutoff: " + avgErrCut);
         System.out.println("maximum iterations: " + maxIterations);
         System.out.println("lambda: " + lambda);
      } // if (train)

   } // public static void echoConfig()



/******
 * Methods for printing information to the console. 
 */

/*
 * If training, reports the results of training, including reason for termination, iterations reached, error reached,
 * final weights, and the results of the subsequent run after train.
 * If running, reports the results of the run.
 * If option is selected, print truth table.
 */
   public static void reportResults()
   {
      if (train)
      {
         System.out.println("Training Terminated");
         System.out.print("Reason for termination: ");
         if (hitThreshold) // if the training termination happened because the error went below the error threshold, report it.
         {
            System.out.print("Error Threshold Reached\n");
         }
         else // otherwise, report that the maximum amount of iterations has been reached.
         {
            System.out.print("Max Iterations Reached\n");
         }

         System.out.println("Iterations Reached: " + iterationsReached); // output the number of iterations training took.

         System.out.println("Error Reached: " + calcError());            // output the error reached at the end of training.

         System.out.println();                                           // newline for formatting purposes

         System.out.println("Final Weights: ");
         System.out.println(dumpWeights());                              // print out weights in a nice format.
      } // if (train)

      if (printTruths) // print out truth table if option is turned on.
      {
         System.out.println();
         System.out.println("Truth Table (format: | In ... In | Out |)");
         System.out.println(dumpTruthTable());
      }

      if(runAfterTrain || (!train)) // only report run results if program is running after training or running.
      {
         System.out.println("Results from Running:");
         for (int cases = 0; cases < numCases; cases++)
         {
            System.out.println("Case " + (cases + 1) + ": " + outputs[cases]);
         }
      }

   } // public static void reportResults()

/*
 * prints the configuration (A-B-1) of the network in a pretty-printing format.
 */
   public static void printNetworkConfig()
   {
      System.out.println("Network Configuration: " + numInputNodes + "-" + numHiddenNodes + "-" + numOutNodes);
   }

/*
 * prints the weights of the network to the console. 
 */
   public static void printWeights()
   {
      System.out.println("Weights: ");
      System.out.println(dumpWeights()); // prints a pretty-printing representation of the weights.
   }
   
/*
 * prints a small status message that indicates to the user that the program is running or training
 * (as opposed to the program freezing)
 */
   public static void printStatus()
   {
      System.out.println(); // whitespace for clarity
      if (train) // if training print a training status message.
      {
         System.out.println("Training ...");
      }
      else // if running print a running status message.
      {
         System.out.println("Running ...");
      }
      System.out.println(); // whitespace for clarity
   } // public static void printStatus()


/******
 * Sigmoid function wrappers (I am doing this for convenience and clarity).
 */

/*
 * wrapper for activation function --> f(x)
 */
   public static double act(double x)
   {
      return f.funct(x);
   }

/*
 * wrapper for derivative of activation function --> f'(x)
 */
   public static double fprime(double x)
   {
      return f.derivative(x); // (f(x)*(1-f(x)) --> f prime with a sigmoid function.
   }



/******
 * network helper functions.
 */

/*
 * runs the network and stores the resulting hidden activations and output based on the stored input.
 */
   public static void recalculate()
   {
      for (int j = 0; j < numHiddenNodes; j++) // updates each node in the hidden layer.
      {
         hj[j] = act(THETA(j));
      }

      F0 = act(THETA0());
   } // public static void recalculate()



/*
 * calculates the dot product (THETA) for node j of the hidden activation layer.
 */
   public static double THETA(int j)
   {
      double sum = 0.0;
      for (int k = 0; k < numInputNodes; k++) // calculates the dot product of the input activations and the edges leading to node j.
      {
         sum += ak[k] * w1kj[k][j];
      }
      return sum;
   } // public static double THETA(int j)

/*
 * calculates the dot product (THETA) for node 0 of the output activation layer.
 */
   public static double THETA0()
   {
      double sum = 0.0;
      for (int j = 0; j < numHiddenNodes; j++) // calculates dot product of the hidden activations and the edges leading to node 0.
      {
         sum += hj[j] * w2j0[j];
      }
      return sum;
   } //public static double THETA0()


/*
 * takes in a set of inputs and sets the current stored input to the given input.
 */
   public static void input(double[] a)
   {
      for (int k = 0; k < numInputNodes; k++) // set input activation layer to the given inputs.
      {
         ak[k] = a[k];
      }
   } // public static void input(double[] a)



/*
 * applies the stored delta w values to the network.
 */
   public static void applyDeltas()
   {
      for (int k = 0; k < numInputNodes; k++) // apply deltas to the w1kj weights
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] += deltawkj[k][j];
         }
      }

      for (int j = 0; j < numHiddenNodes; j++) // apply delta to the w2j0 weights
      {
         w2j0[j] += deltawj0[j];
      }
   } // public static void applyDeltas()



/*
 * runs the network on a given set of input activations and returns the output.
 */
   public static double getOutput(double[] inp)
   {
      input(inp);    // inputs the given set of values.
      recalculate(); // runs the network.
      return F0;     // returns the output resulting from the run.
   }




/******
 * Training Helper functions
 */

/*
 * calculates the delta w values for the network and stores them in arrays.
 */
   public static void calcDeltas()
   {
      omega0 = T0 - F0;                        // calculates omega0 and psi0 to avoid redundant calculations.
      psi0 = omega0 * fprime(THETA0());        // THETA0() refers to the dot product for the output activation.

      for (int j = 0; j < numHiddenNodes; j++) // calculates the partial derivatives and deltas for the output layer.
      {
         partialwj0 = -hj[j] * psi0;
         deltawj0[j] = -lambda * partialwj0;
      }

      for (int j = 0; j < numHiddenNodes; j++) // calculates OMEGA(j) and PSI(j) to avoid redundant calculations.
      {
         OMEGA[j] = psi0 * w2j0[j];
         // System.out.println("DEBUG" + OMEGA[j]);

         PSI[j] = OMEGA[j] * fprime(THETA(j));
      } // for (int j = 0; j < numHiddenNodes; j++)

      for (int j = 0; j < numHiddenNodes; j++) // calculates the partial derivatives and deltas for the output layer.
      {
         for (int k = 0; k < numInputNodes; k++)
         {
            partialwkj = -ak[k] * PSI[j];
            deltawkj[k][j] = -lambda * partialwkj;
         }
      }

   } // public static void calcDeltas()



/*
 * Calculates and updates delta w for one training cycle based on the given truth table index.
 * List of operations in one training cycle, sequentially:
 *    Input truth table values
 *    set T0 (expected output in calcDeltas method) to the truth table output.
 *    run the network to
 *    calculate the delta ws.
 *    apply the delta ws.
 *    rerun the network to keep it accurate.
 */
   public static void trainProcedure(int truthtableindex)
   {
      input(givenInputs[truthtableindex]);
      T0 = expectedOutputs[truthtableindex];
      recalculate(); // runs the network.

      calcDeltas();

      applyDeltas();
      recalculate();
   } // public static void trainProcedure(int truthtableindex)



/*
 * calculates the average error bound of the network through all the test cases.
 */
   public static double calcError()
   {
      double ret = 0.0;
      for (int cases = 0; cases < numCases; cases++) // run network for all the cases and calculate the error for each one
      {
         input(givenInputs[cases]);            // input the truth table inputs for the current index.
         recalculate();                        // run the network
         omega0 = expectedOutputs[cases] - F0; // calculate difference between expected output for this case and actual output.
         ret += 0.5 * omega0 * omega0;         // add error for this case to total error.
      }
      ret /= (double) numCases; // Divide by numCases to get average error. (note: cast to double b/c numCases is usually an int)

      return ret;
   } // public static double calcError()



/******
 * network population helper functions
 */

/*
 * populates the weights of the network with random values within the range of the configuration parameters.
 */
   public static void populateRand()
   {
      double range = maxRand-minRand;         // range size of the random number to help calculate range.
      double offset = minRand;                // offset of the random number to help calculate range.
      for (int k = 0; k < numInputNodes; k++) // populate w1kj weights.
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] = Math.random() * range + offset; // random number between minRand and maxRand.
         }
      }

      for (int j= 0; j < numHiddenNodes; j++) // populate w2j0 weights.
      {
         w2j0[j] = Math.random() * range + offset; // random number between minRand and maxRand.
      }
   } // public static void populateRand()



/*
 * populates the weights of the network with the given weights.
 */
   public static void populateSet(double[][] neww1kj, double[] neww2j0)
   {
      for (int k = 0; k < numInputNodes; k++) // populate w1kj weights.
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] = neww1kj[k][j];
         }
      }

      for (int j = 0; j < numHiddenNodes; j++) // populate w2j0 weights.
      {
         w2j0[j] = neww2j0[j];
      }
   } // public static void populateSet(double[][] neww1kj, double[] neww2j0)

/******
 * Pretty printing string functions
 */

/*
 * returns a pretty-printing string of the truth table. 
 */
   public static String dumpTruthTable()
   {
      String ret = "";

      for (int cases = 0; cases < numCases; cases++)
      {
         ret += "| ";
         for (int input = 0; input < numInputNodes; input++)
         {
            ret += givenInputs[cases][input] + " ";                                // adds input to return string.
         }
         ret += "| " + expectedOutputs[cases] + " | --> Case " + (cases+1) + "\n"; // adds output and case number label.
      }
      
      return ret;
   } // public static String dumpTruthTable()



/*
 * returns all the edge weights as a pretty printing string.
 */
   public static String dumpWeights()
   {
      String ret = "";
      ret += "----------------------------------------------------------------------"; // divider characters for aesthetic
      ret += "\n";

      for (int k = 0; k < numInputNodes; k++) // formats w1kj weights in an easily digestible format.
      {
         ret += "| ";
         for (int j = 0; j < numHiddenNodes; j++)
         {
            ret += "w1" + k + j + ": " +  w1kj[k][j] + " | ";
         }
         ret += "\n";
      }

      ret += "----------------------------------------------------------------------"; // divider characters for aesthetic
      ret += "\n";

      for (int j = 0; j < numHiddenNodes; j++) // formats w2j0 weights into an easily digestible format.
      {
         ret += "w2" + j + "" + 0 + ": " + w2j0[j] + " | ";
      }

      ret += "\n";
      ret += "----------------------------------------------------------------------"; // divider characters for aesthetic

      return ret;
   } // public static String dumpWeights()



/*
 * returns all the activation values as a string. Used for DEBUG.
 */
   public static String dumpVals()
   {
      String ret = "";
      for (int k = 0; k < numInputNodes; k++)
      {
         ret += ak[k] + " ";
      }
      ret += "\n";

      for (int j = 0; j < numHiddenNodes; j++)
      {
         ret += hj[j] + " ";
      }
      ret += "\n";

      ret += F0 + " ";

      return ret;
   } // public static String dumpVals()

} // public class AB1