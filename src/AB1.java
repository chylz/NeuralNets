/*
 * Author: Luke Zeng
 * Date of Creation: 2/7/2024
 *
 * Description: Feed-Forward A-B-1 Neural Network with Gradient Descent learning.
 *              Merged previous network files into one file representing the AB1 Network.
 */


public class AB1
{

/*
 * base network storage and activation functions.
 */
   public static double[][] w1kj; // edge weights between layers a and h.
   public static double[] w2j0; // edge weights between layers h and F0
   public static double F0; //stores the single output activation.
   public static double[] ak; //stores the input activations
   public static double[] hj; //stores the single hidden activation node values.
   public static ActivationFunction f; // activation function f(x)

/*
 * Configuration Parameters
 */
   public static double lambda;
   public static int numInputNodes;
   public static int numHiddenNodes;
   public static int numOutNodes;
   public static int populationMethod; //Population method --> 0: randomize weights | 1: manually set weights
   public static boolean train;
   public static double minRand;
   public static double maxRand;
   public static double avgErrCut; // average error cutoff.
   public static int maxCycles;
   public static boolean hitThreshold; //Flag for program termination reason.

/******
 * Truth Table Arrays
 */
   public static double[][] givenInputs;
   public static double[] expectedOutputs;
   public static int numCases;

/*
 * Training Memory
 */
   public static double[][] deltawkj; //delta w for  activation layer n = 1;
   public static double[] deltawj0; //delta w for activation layer n = 2;
   public static double psi0;
   public static double T0;
   public static double partialwkj;
   public static double partialwj0;
   public static double[] PSI;
   public static double[] OMEGA;
   public static double omega0;



/*
 * Main function: runs/train the network based on configuration parameters.
 */
   public static void main(String[] args)
   {
      setConfig();
      allocate();
      populate();
      System.out.println(dumpNetConfig());
      if (train)
      {
         System.out.println(dumpTrainingConfigs());
         reportTraining(train());
         reportRun();
      }
      else
      {
         System.out.println("Weights: ");
         System.out.println(dumpWeights());
         reportRun();
      }


   }//public static void main(String[] args)



/******
 * Driver methods
 */

/*
 * train the network with gradient descent learning, then print out the weights obtained by training.
 * Returns the number of iterations the training took.
 */
   public static int train()
   {
      boolean done = false;
      int cases;
      int curiteration = 0;
      double curerror;
      while(!done)
      {
         for (cases = 0; cases < numCases; cases++)
         {
            trainProcedure(cases); //sets inputs, runs network, calculates delta W, and applies delta W for a given case of the truth table
         }
         curerror = calcError();

         //System.out.println("DEBUG" + curerror);
         if (curerror <= avgErrCut)
         {
            hitThreshold = true;
            done = true;
         }

         if (++curiteration >= maxCycles)
         {
            hitThreshold = false;
            done = true;
         }
      } //while(!done)
      return curiteration;
   } //public static int train()



/*
 * allocates the arrays and activation function for the network.
 */
   public static void allocate()
   {
      f = new Sigmoid(); //defines the activation function object as a sigmoid.

      givenInputs = new double[numCases][numInputNodes];
      expectedOutputs = new double[numCases];


      if (train)
      {
         PSI = new double[numHiddenNodes];
         OMEGA = new double[numHiddenNodes];

         deltawkj = new double[numInputNodes][numHiddenNodes];
         deltawj0 = new double[numHiddenNodes];
      }


      w1kj = new double[numInputNodes][numHiddenNodes];
      w2j0 = new double[numHiddenNodes];

      ak = new double[numInputNodes];
      hj = new double[numHiddenNodes];
   } //public static void allocate()



/*
 * populates the arrays of the network based on the configuration parameters.
 */
   public static void populate()
   {
      switch(populationMethod)
      {
         case 0:
            populateRand();
            break;
         case 1:
            double[][] tempset1 = {{2.0, 1.0}, {2.0, 1.0}}; //temporary matrix used to set the n=1 weights manually
            double[] tempset2 = {2.0, 1.0};                 //temporary array used to set n=2 weights manually.
            populateSet(tempset1, tempset2);
            break;
      } //switch(populationMethod)


      double[][] ttsetin = {{0.0, 0.0},
                            {0.0, 1.0},
                            {1.0, 0.0},
                            {1.0, 1.0}}; //temporary array to set truth table inputs.

      double[] ttsetout = {0.0,
                           1.0,
                           1.0,
                           0.0}; //temporary array to set truth table outputs.


      for (int cases = 0; cases < numCases; cases++)
      {
         for (int k = 0; k < numInputNodes; k++)
         {
            givenInputs[cases][k] = ttsetin[cases][k];
         }
         expectedOutputs[cases] = ttsetout[cases];
      }
   } //public static void populate()



/*
 * sets the configuration parameters of the network.
 */
   public static void setConfig()
   {
      maxCycles = 100000;
      avgErrCut = 0.0002;
      minRand = -1.5;
      maxRand = 1.5;
      lambda = 0.3;
      populationMethod = 0; //0 is random, 1 is set population.
      train = true;
      numInputNodes = 2;
      numHiddenNodes = 20;
      numOutNodes = 1;
      numCases = 4; //reflects the number of cases put into the input and output of truth table
   } //public static void setConfig()


/*
 * prints all the configuration parameters to the terminal.
 */
   public static void echoConfig()
   {
      System.out.println(dumpConfig());
   }



/******
 * Result reporting methods
 */

/*
 * reports the end results for training.
 */
   public static void reportTraining(int iterations)
   {
      System.out.println();
      System.out.println("Training Terminated");
      System.out.print("Reason for termination: ");
      if (hitThreshold)
      {
         System.out.print("Error Threshold Reached\n");
      }
      else
      {
         System.out.print("Max Iterations Reached\n");
      }

      System.out.println("Iterations Reached: " + iterations);

      System.out.println("Error Reached: " + calcError());

      System.out.println();
      System.out.println("Final Weights: ");
      System.out.println(dumpWeights());

      System.out.println();
      System.out.println("Truth Table format: | In In | Out |");
      System.out.println(dumpTruthTable());
   } //public static void reportTraining(int iterations)



/*
 * run the network and output the result.
 */
   public static void reportRun()
   {
      System.out.println("Truth Table format: | In In | Out |");
      System.out.println(dumpTruthTable());
      System.out.println();
      System.out.println("Results from run");
      for (int cases = 0; cases < numCases; cases++)
      {
         System.out.println("Case " + (cases+1) + ": " + getOutput(givenInputs[cases]));
         //System.out.println("DEBUG" + dumpVals());
      }
   } //public static void reportRun()


/******
 * cleaner sigmoid function wrappers (for convenience).
 */

/*
 * Activation function of the network --> f(val)
 */
   public static double act(double val)
   {
      return f.funct(val);
   }



/*
 * Calculates f'(x) for the given value.
 */
   public static double fprime(double x)
   {
      return f.derivative(x); //(f(x)*(1-f(x)) --> f prime with a sigmoid function.
   }



/******
 * network helper functions.
 */

/*
 * recalculates the entire network
 */
   public static void recalculate()
   {
      for (int j = 0; j < numHiddenNodes; j++)//activation layer 2
      {
         hj[j] = act(THETA(2, j)); //parameter of 2 refers to the hidden layer
      }

      F0 = act(THETA(3, 0)); //parameter of 3 refers to the output activation layer and parameter 0 refers to node F0
   } //public static void recalculate()



/*
 * calculates the dot product in layer n for node a
 * n=2 refers to hidden layer.
 * n=3 refers to output activation layer.
 */
   public static double THETA(int n, int a)
   {
      double sum = 0.0;
      if (n == 2)
      {
         for (int k = 0; k < numInputNodes; k++)
         {
            sum += ak[k]*w1kj[k][a];
         }
      }
      if (n == 3)
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            sum += hj[j] * w2j0[j];
         }
      }
      return sum;
   } //public static double THETA(int n, int a)



/*
 * takes in  an input activation layer and sets the current input to the given input.
 */
   public static void input(double[] a)
   {
      for (int k = 0; k < numInputNodes; k++)
      {
         ak[k] = a[k];
      }
   } //public static void input(double[] a)



/*
 * applies the given delta w values to the network.
 */
   public static void applyDeltas(double[][] deltaw1kj, double[] deltaw2j0)
   {
      for (int k = 0; k < numInputNodes; k++)
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] += deltaw1kj[k][j];
         }
      }

      for (int j = 0; j < numHiddenNodes; j++)
      {
         w2j0[j] += deltaw2j0[j];
      }
   } //public static void applyDeltas(double[][] deltaw1kj, double[] deltaw2j0)


/*
 * updates the input and calculates the output to a given input and returns the output. 
 */
   public static double getOutput(double[] inp)
   {
      input(inp);
      recalculate();
      return F0;
   }



/*
 * sets all the weights in the network to the given input arrays
 */
   public static void setNetwork(double[][] n1, double[] n2)
   {
      for  (int k = 0; k < numInputNodes; k++) //populate first weights
      {
         for  (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] = n1[k][j];
         }
      }

      for  (int j = 0; j < numHiddenNodes; j++)
      {
         w2j0[j] = n2[j];
      }
   } //public static void setNetwork(double[][] n1, double[] n2)


/******
 * Training Helper functions
 */

/*
 * Calculates the delta w values for the network and stores them in arrays.
 */
   public static void calcDeltas()
   {
      omega0 = T0-F0;
      psi0 = omega0 * fprime(THETA(3, 0)); // fprime calculates f'(theta0) (theta of the single output activation).


      for (int j = 0; j < numHiddenNodes; j++)
      {
         partialwj0 = -hj[j] * psi0;
         deltawj0[j] = -lambda * partialwj0;
      }

      for (int j = 0; j < numHiddenNodes; j++)
      {
         OMEGA[j] = psi0 * w2j0[j];
         //System.out.println("DEBUG" + OMEGA[j]);
         PSI[j] = OMEGA[j] * fprime(THETA(2, j));

      }

      for (int j = 0; j < numHiddenNodes; j++)
      {
         for (int k = 0; k < numInputNodes; k++)
         {
            partialwkj = -ak[k] * PSI[j];
            deltawkj[k][j] = -lambda * partialwkj;
         }
      }

   } //public static void calcDeltas()


/*
 * calculates and updates delta w for one training cycle based on the given truth table index.
 */
   public static void trainProcedure(int truthtableindex)
   {
      input(givenInputs[truthtableindex]);
      T0 = expectedOutputs[truthtableindex];
      recalculate();

      calcDeltas();

      applyDeltas(deltawkj, deltawj0);
      recalculate();
   } //public static void trainProcedure(int truthtableindex)



/*
 * calculates the error bound of the network through all the test cases.
 */
   public static double calcError()
   {
      double ret = 0.0;
      for (int cases = 0; cases < numCases; cases++)
      {
         input(givenInputs[cases]);
         recalculate();
         omega0 = expectedOutputs[cases]-F0;
         ret += 0.5 * omega0*omega0;
      }
      ret /= (double)numCases;

      return ret;
   } //public static double calcError()



/******
 * network population helper functions
 */

/*
 * populates the weights of the network with random values within the range of the configuration parameters.
 */
   public static void populateRand()
   {
      double range = maxRand-minRand;
      double offset = minRand;
      for (int k = 0; k < numInputNodes; k++)
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] = Math.random() * range + offset; //random number within 2 numbers.
         }
      }

      for (int j= 0; j < numHiddenNodes; j++)
      {
         w2j0[j] = Math.random() * range + offset;
      }
   } //public static void populateRand()



/*
 * populates the weights of the network with given values of w.
 */
   public static void populateSet(double[][] neww1kj, double[] neww2j0)
   {
      setNetwork(neww1kj, neww2j0);
   }

/******
 * Pretty printing functions
 */

/*
 * returns a pretty-printing for matted version of the configuration parameters.
 */
   public static String dumpConfig()
   {
      return "lambda:" + lambda + " | numInputNodes:" + numInputNodes + " | numOutNodes:" + numOutNodes +
             " | numHiddenNodes:" + numHiddenNodes + " | populationMethod:" + populationMethod + " | minRand:" + minRand +
             " | maxRand:" + maxRand + " | avgErrCut:" + avgErrCut + " | maxIterations:" + maxCycles + " |";
   }



/*
 * returns a pretty-printing string of the network configuration.
 */
   public static String dumpNetConfig()
   {
      return "Network Configuration: " + numInputNodes + "-" + numHiddenNodes + "-" + numOutNodes;
   }



/*
 * returns a pretty-printing string of the random number range, max iterations, error threshold, and Lambda.
 */
   public static String dumpTrainingConfigs()
   {
      return "Training Parameters: \n| lambda:" + lambda + " | max iterations:" + maxCycles + " | error threshold:" + avgErrCut + " | random number range: [" + minRand + " , " + maxRand + "] |";
   }



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
            ret += givenInputs[cases][input] + " ";
         }
         ret += "| " + expectedOutputs[cases] + " | --> Case " + (cases+1) + "\n";
      }
      
      return ret;
   } //public static String dumpTruthTable()



/*
 * returns all the edge weights as a pretty printing string.
 */
   public static String dumpWeights()
   {
      String ret = "";
      ret += "----------------------------------------------------------------------";
      ret += "\n";
      for (int k = 0; k < numInputNodes; k++)
      {
         ret += "| ";
         for (int j = 0; j < numHiddenNodes; j++)
         {
            ret += "w1" + k + j + ": " +  w1kj[k][j] + " | ";
         }
         ret += "\n";
      }

      ret += "----------------------------------------------------------------------";
      ret += "\n";

      for (int j = 0; j < numHiddenNodes; j++)
      {
         ret += "w2" + j + "" + 0 + ": " + w2j0[j] + " | ";
      }

      ret += "\n";
      ret += "----------------------------------------------------------------------";

      return ret;
   } //public static String dumpWeights()



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
   } //public static String dumpVals()
   


/******
 * Deprecated
 */

/*
 * set the weight of an edge on activation layer n going from node a to b to w.
 */
   public static void setWeight(int n, int a, int b, double w)
   {
      if (n == 1)
      {
         w1kj[a][b] = w;
      }
      if (n == 2)
      {
         w2j0[a] = w;
      }
   } //public void setWeight(int n, int a, int b, double w)



/*
 * Initializes a AB1 Network with edge weights 0 and given dimension
 * specification, activation function, and truth table for the specific network for training purposes.
 *
 * Parameters A and B describe the number of input and hidden activation layers respectively.
 * Parameter f1 is the activation function f(x).
 */
   public AB1(int A, int B, ActivationFunction f1)
   {
      numInputNodes = A;
      numHiddenNodes = B;
      numOutNodes = 1;
      f = f1;
      w1kj = new double[numInputNodes][numHiddenNodes];
      w2j0 = new double[numHiddenNodes];
      ak = new double[numInputNodes];
      hj = new double[numHiddenNodes];
   } //public AB1(int A, int B, ActivationFunction f1)
} //public class AB1