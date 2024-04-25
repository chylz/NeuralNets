import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/*
 * Author: Luke Zeng
 * Date of Creation: 2/7/2024
 *
 * Description: Fully connected Feed-Forward A-B-C Neural Network with Gradient Descent learning and Backpropagation Optimization.
 *              The way data is handled in this neural network is modeled after the way data is handled in
 *              a spreadsheet.
 */


public class ABCBP
{
/******
 * Default config parameters.
 */

   public static final double DEFAULT_LAMBDA = 0.3;        // default value for lambda.
   public static final int DEFAULT_POPULATION = 0;         // default value for population method.
   public static final double DEFAULT_MAXRAND = 1.5;       // default value for maxRand
   public static final double DEFAULT_MINRAND = -1.5;      // default value for minRand.
   public static final double DEFAULT_ERR = 2E-4;          // default value for avg error cutoff.
   public static final int DEFAULT_MAXIT = 100000;         //Default value for max iterations.
   public static final String DEFAULT_CONFIG = "test.bin"; // default config file.

/******
 * Configuration/validation
 */
   public static Config cf;
   public static InputValidation valid; // input validation.
/******
 * Base network storage and activation function.
 */
   public static double[][] w1kj;      // edge weights between layers a and h.
   public static double[][] w2ji;      // edge weights between layers h and F
   public static double[] F;           // stores the single output activation.
   public static double[] ak;          // stores the input activations.
   public static double[] hj;          // stores the single hidden activation node values.
   public static ActivationFunction f; // activation function f(x).
   public static double[][] outputs;   // stores the outputs from the sequential run function.

/******
 * Configuration Parameters
 */
   public static double lambda;         // step size multiplier for gradient descent.
   public static int numInputNodes;
   public static int numHiddenNodes;
   public static int numOutNodes;       // number of output nodes.
   public static int populationMethod;  // population method --> 0: randomize weights | 1: manually set weights | 2: File IO
   public static boolean train;         // if true, train. if false, run.
   public static double minRand;        // minimum random threshold
   public static double maxRand;        // maximum random threshold.
   public static double avgErrCut;      // average error cutoff (error threshold).
   public static int maxIterations;     // maximum iterations before cutoff.
   public static boolean printWeight;   // flag to indicate whether to print weights before running.
   public static boolean printTruths;   // flag to indicate whether to print the truth table.
   public static boolean runAfterTrain; // indicates whether the network should run after training.
   public static boolean saveWeights;   // indicates to save weights to the file named FILENAME.
   public static String inFile;         // input file filename.
   public static String outFile;        // output file filename.
   public static FileHandler fh;        // handles the files for the network.

   public static boolean hitThreshold = false;  // flag for program termination due to hitting error threshold. (default false)
   public static boolean outofOps = false;      // flag for program termination due to running out of operations. (default false)



/******
 * Truth Table Arrays
 */
   public static double[][] givenInputs;     // truth table inputs.
   public static double[][] expectedOutputs; // truth table outputs.
   public static int numCases;               // number of rows (cases) in the truth table.

/******
 * Training Arrays/Variables
 * Lowercase greek letter spellings (like psi) represent the lowercase greek letters.
 * Uppercase greek letter spellings (like Theta) represent the uppercase greek letters.
 */
   public static double[] psi;          // lowercase psi, referring to small psi sub j.
   public static double[] Thetaj;       // thetas for the hidden activation layer.
   public static int iterationsReached; // the amount of iterations reached during training.

/******
 * network frontend flags/miscellaneous
 */
   public static int exit = 0;   // default exit value is 0 (flag to determine if initialization is valid).
   public static long starttime; // start time of train
   public static long endtime;   // end time of train
   public static Date date;

/*
 * main function: initializes and runs/train the network based on passed control file to command line
 * (will use default if no configuration file is provided).
 */
   public static void main(String[] args)
   {
      boolean canstart;
      if (args.length > 0) // set configuration file from command line arguments.
      {
         try
         {
            cf = new Config(args[0]);
         }
         catch (IOException e)
         {
            System.out.println("Cannot find configuration file, or configuration file is of a corrupted format. Will use default (test.bin)");
            try
            {
               cf = new Config(DEFAULT_CONFIG);
            }
            catch (IOException ie)
            {
               System.out.println("Add the default config file into your directory, dummy");
            }

         }

      } // if (args.length > 0)
      else
      {
         System.out.println("No configuration file provided. Will use default (test.bin)");
         try
         {
            cf = new Config(DEFAULT_CONFIG);
         }
         catch (IOException ie)
         {
            System.out.println("Add the default config file into your directory, dummy");
         }

      }

      canstart = initialize();
      if (canstart)
      {
         date = new Date(); // record start time.
         starttime = date.getTime();
         if (train)          // procedure if training
         {
            printStatus();   // prints a small status message letting user know that the program is training.
            train();         // trains the network.
            if (runAfterTrain)
            {
               run();        // runs the network after training to get the F values for each test case to compare with truth table.
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

         date = new Date(); // record end time.
         endtime = date.getTime();

         reportResults();    // print the result of the train/run.

         if (saveWeights)
         {
            fh.writeWeights(w1kj, w2ji, numInputNodes, numHiddenNodes, numOutNodes); // saves the weights to the output file.
         }
      } // if (canstart)
   } // public static void main(String[] args)



/******
 * Driver methods
 */

/*
 * sets the configuration parameters of the network.
 * If no values are provided for any configuration
 */
   public static void setConfig() throws IOException
   {
      String actfunct = cf.actfunct; // temporary string to store the activation function configuration.


      maxIterations = cf.maxIterations;       // Maximum amount of iterations when training.
      avgErrCut = cf.avgErrCut;               // Error to stop at when training.
      minRand = cf.minRand;                   // Minimum value for random population of weights.
      maxRand = cf.maxRand;                   // Maximum value for random population of weights.
      lambda = cf.lambda;                     // Lambada factor --> multiplier used to adjust step size in training
      populationMethod = cf.populationMethod; // 0 is random, 1 is manual populate (DEPRECATED), 2 is populate from file.
      train = cf.train;                       // Training flag --> if set to true the network will train, if false the network will run.
      numInputNodes = cf.numInputNodes;       // For this network will most likely be 2 but can functionally be any integer.
      numHiddenNodes = cf.numHiddenNodes;     // For this network number of hidden nodes can be any value.
      numOutNodes = cf.numOutNodes;           // For this network this value will always be 1.
      numCases = cf.numCases;                 // Reflects the number of cases put into the input and output of truth table.
      printWeight = cf.printWeight;           // Flag that determines whether to print weights at the beginning of running.
      printTruths = cf.printTruths;           // Flag that determines whether to print truth table at the end of running/training.
      runAfterTrain = cf.runAfterTrain;       // Should the network run and print run results after training (default is yes).
      saveWeights = cf.saveWeights;           // set to true to save weights to file (only salient if train).
      inFile = cf.inputs;                     // set file name of the inputs.
      outFile = cf.outputs;                   // file name of the truth table.
      actfunct = cf.actfunct;                 // set activation function.

      valid = cf.valid; // sets validation parameters of config to current validation parameters.

      if ((saveWeights && train) || populationMethod == 2)
      {
         fh = new FileHandler(cf.weightsFile); // handles the files for the project.
      }

      if (actfunct.equals("sigmoid"))
      {
         f = new Sigmoid();
      }
      else if (actfunct.equals("linear"))
      {
         f = new Fx();
      }
   } // public static void setConfig() throws IOException

/*
 * runs allocate, populate, setconfig, and echoconfig, not necessarily in that order (makes main method less cluttered).
 */
   public static boolean initialize()
   {
      try
      {
         setConfig();
         if (!validate()) // validates the function.
         {
            exit = 1; // set exit code to 1 (basically, the code can't run).
         }
         else
         {
            echoConfig();
            allocate();
            populate();
         }
      } // try
      catch (FileNotFoundException fe)
      {
         System.out.println("Given config file is not found. Please give a valid configuration file");
         exit = 1; // exit code 1 means initialize doesn't complete.
      }
      catch (IOException ie)
      {
         System.out.println("Something is wrong with the configuration file or the weights files or the truth table. Please fix");
         exit = 1; // exit code 1 means intialize doesn't complete
      }
      return exit == 0; // only validate if exit code is 0 (any exit code other than zero is false).

   } // public boolean initialize()

/*
 * populates the arrays of the network based on the configuration parameters.
 */
   public static void populate() throws IOException
   {
      switch(populationMethod)                  // populates the weights of the network.
      {
         case 0:                                // random population
            populateRand();
            break;
         case 1:                                // manual population (DEPRECATED, no longer supported (will replace with something else soon))
            double[][] TEMPSET1 = {{2.0, 1.0},
                                   {2.0, 1.0}}; // temporary matrix used to set the layer n=1 weights manually
            double[][] TEMPSET2 = {{2.0, 1.0},
                                   {2.0, 1.0}}; // temporary array used to set the layer n=2 weights manually.
            populateSet(TEMPSET1, TEMPSET2);
            break;
         case 2:                                // Populate from file.
            if (!fh.loadWeights(w1kj, w2ji, numInputNodes, numHiddenNodes, numOutNodes)) // loads weights from file, return true if valid weights, false if invalid.
            {
               System.out.println("CONFIG: weights file does not match with given config parameters. Populating random");
               populateRand();
            }
            break;

      } // switch(populationMethod)

/*
 * Load the inputs/outputs, checking if dimensions are valid.
 */
      if (!FileHandler.decodeIns(givenInputs, inFile, numInputNodes, numCases))
      {
         System.out.println("input dimensions don't match up with given parameters. Aborting");
         exit = 1; // exit code 1 is a flag that indicates that initialize didn't complete
      }

      boolean outsPres = FileHandler.decodeOuts(expectedOutputs, outFile, numOutNodes, numCases); // indicate outputs as present.

      if (train && !outsPres)
      {
         System.out.println("expected output dimensions don't match up with given parameters and you are training. Aborting");
         exit = 1; // exit code 1 is a flag that indicates that initialize didn't complete
      }

      if (!train && printTruths && !outsPres) // only if running and printing truths.
      {
         System.out.println("expected output dimensions don't match up with given parameters. Will not print truth table this run. ");
         printTruths = false;
      }

   } // public static void populate() throws IOException



/*
 * trains the network with gradient descent learning, then print out the weights obtained by training.
 * stores the amount of iterations the network took to train in a static field.
 * Will terminate if iterations exceeds maximum iterations defined in configuration parameters or if
 * an error threshold defined in configuration parameters is reached.
 *
 * an iteration consists of numCases cycles of delta w calculations and applications
 * (essentially, calculating and updating delta w for each training case is one iteration)
 *
 * Will save weights to the output file if option is selected.
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
         curerror = calcError();   // calculates the error to determine whether to end training early.

         // System.out.println("DEBUG" + curerror);
         if (curerror <= avgErrCut)
         {
            hitThreshold = true; // mark that training terminated for hitting error threshold.
            done = true;         // flag to indicate that training has finished.
         }

         if (++curiteration >= maxIterations)
         {
            outofOps = true; // mark that training terminated not from hitting the error threshold
            done = true;          // flag to indicate that training has finished.
         }
      } // while(!done)

      iterationsReached = curiteration; // stores the iterations reached for result reporting.
   } // public static void train() throws IOException



/*
 * allocates the major network arrays for the network.
 */
   public static void allocate()
   {
      givenInputs = new double[numCases][numInputNodes];


      if (train || printTruths) // only allocate training-exclusive arrays if training.
      {
         psi = new double[numOutNodes];
         Thetaj = new double[numHiddenNodes];
         expectedOutputs = new double[numCases][numOutNodes];
      } // if (train)

      if ((train && runAfterTrain) || !train)         // only allocate run-exclusive arrays if running or running after training.
      {
         outputs = new double[numCases][numOutNodes]; // stores the outputs after running.
      }

      w1kj = new double[numInputNodes][numHiddenNodes]; // connections between activation layers n=1 and n=2.
      w2ji = new double[numHiddenNodes][numOutNodes];   // connections between activation layers n=2 and n=3.

      ak = new double[numInputNodes];  // stores input activation layer values
      hj = new double[numHiddenNodes]; // stores hidden activation layer values
      F = new double[numOutNodes];     // stores output activation layer values
   } // public static void allocate()

/*
 * runs the network and output the result.
 */
   public static void run()
   {
      for (int cases = 0; cases < numCases; cases++) // runs all the test cases from the truth table.
      {
         getOutput(givenInputs[cases]);        // runs the network with the inputs from the given truth table case.
         for (int i = 0; i < numOutNodes; i++) // stores the outputs of the run for this truth table case.
         {
            outputs[cases][i] = F[i];
         }
         // System.out.println("DEBUG" + dumpVals());
      }
   } // public static void run()

/*
 * validates that the configuration parameters are all defined and
 * reports if not all params are present (sets default vals if not present).
 */
   public static boolean validate()
   {
      boolean ret = true;
      if (!valid.lambdaDefined)
      {
         System.out.println("Config: lambda not defined, setting lambda to 0.3");
         lambda = DEFAULT_LAMBDA; // default lambda value.
      }

      if (!valid.numInputDefined)
      {
         System.out.println("Config: numInputs not defined, aborting");
         ret = false;
      }

      if (!valid.numHiddenDefined)
      {
         System.out.println("Config: num Hidden nodes not defined in config, aborting");
         ret = false;
      }

      if (!valid.numOutDefined)
      {
         System.out.println("Config: number of outputs not defined, aborting");
         ret = false;
      }
/*
 * set population method to default if undefined or the value is invalid (>=3 or <0 or 1 (manual population DEPRECATED)).
 */
      if (!valid.popMethodDefined || populationMethod>=3 || populationMethod<0 || populationMethod == 1)
      {
         System.out.println("Config: The population method you specified is missing or isn't valid. Will use random values.");
         populationMethod = DEFAULT_POPULATION; // default population method.
      }

      if (!valid.trainDefined)
      {
         System.out.println("Config: have not specified whether to train or run, will train");
         train = true;
      }

      if (!valid.minRandDefined)
      {
         System.out.println("Config: minRand not defined, will set to -1.5");
         minRand = DEFAULT_MINRAND; // default minRand.
      }

      if (!valid.maxRandDefined)
      {
         System.out.println("Config: maxRand not defined, will set to 1.5");
         minRand = DEFAULT_MAXRAND; // default maxRand
      }

      if (minRand>maxRand)
      {
         System.out.println("Config: minRand is larger than maxRand, swapping min and max random vals");
         double temp = minRand;
         minRand = maxRand;
         maxRand = temp;
      }

      if (!valid.avgErrCutDefined || avgErrCut == 0.0)
      {
         System.out.println("Config: average error cutoff not defined or invalid, setting to 2E-4");
         avgErrCut = DEFAULT_ERR; // default average error cutoff.
      }

      if (!valid.maxIterationsDefined)
      {
         System.out.println("Config: Max iterations not defined, setting to 100000");
         maxIterations = DEFAULT_MAXIT; // default maxIterations value.
      }

      if (!valid.printWeightDefined)
      {
         System.out.println("Config: Did not specify whether to print weights or not, will not print");
         printWeight = false; // don't print weights.
      }

      if (!valid.printTruthsDefined)
      {
         System.out.println("Config: Did not specify whether to print truth table, will print");
         printTruths = true;
      }

      if (!valid.runAfterDefined && train)
      {
         System.out.println("Config: Did not specify whether to run after train, will run after train");
         runAfterTrain = true;
      }

      if (!valid.saveWeightDefined)
      {
         System.out.println("Config: Did not specify whether to save weights, not saving them.");
         saveWeights = false;
      }

      if (!valid.numCasesDefined)
      {
         System.out.println("Config: numCases not defined, aborting");
         ret = false;
      }

      if (!valid.actfunctDefined)
      {
         System.out.println("Config: activation function not defined; you get a sigmoid.");
         f = new Sigmoid();
      }

      if (!valid.inputsDefined)
      {
         System.out.println("Config: input file not defined, aborting.");
         ret = false;

      }

      if (!valid.outputsDefined && (train || printTruths))
      {
         System.out.println("Config: truth table not defined for training or print truth table wasn't selected and truth table not defined. aborting.");
         ret = false;
      }

      if (!valid.weightsFileDefined && populationMethod == 2) // if reading weights from file and weights aren't present, random
      {
         System.out.println("Config: weights file not present with population set to file. Randomly populating.");
         populationMethod = DEFAULT_POPULATION;
      }

      return ret;
   } //public static boolean validate()

/*
 * prints all the configuration parameters to the terminal.
 */
   public static void echoConfig()
   {
      printNetworkConfig(); // prints network configuration (A-B-C).

      System.out.println(); // newline for clarity.

      System.out.println("Configuration Parameters");
      System.out.println("number of input nodes: " + numInputNodes);
      System.out.println("number of hidden nodes: " + numHiddenNodes);
      System.out.println("number of output nodes: " + numOutNodes);
      System.out.println("population method: " + populationMethod);
      if (populationMethod == 2) // population method = 2 means load from file
      {
         System.out.println("loading weights from " + cf.weightsFile);
      }
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
         System.out.print("Reason(s) for termination: ");
         if (hitThreshold) // if the training termination happened because the error went below the error threshold, report it.
         {
            System.out.print("Error Threshold Reached");
         }
         if (outofOps) // otherwise, report that the maximum amount of iterations has been reached.
         {
            if (hitThreshold) // add a comma if both reasons for termination were satisfied.
            {
               System.out.println(", ");
            }
            System.out.print("Max Iterations Reached");
         }
         System.out.println();

         System.out.println("Iterations Reached: " + iterationsReached); // output the number of iterations training took.

         System.out.println("Error Reached: " + calcError());            // output the error reached at the end of training.

         System.out.println();                                           // newline for formatting purposes


         if (printWeight)
         {
            System.out.println("Final Weights: ");
            System.out.println(dumpWeights());                           // print out weights in a nice format, if specified.
         }
      } // if (train)
      //System.out.println("DEBUG " + starttime + " " + endtime);
      System.out.println("Time Elapsed: " + (endtime-starttime) + " ms"); // print time elapsed.

      if (saveWeights && train) // indicate weights have been saved to given file.
      {
         System.out.println(); // newline for fomratting.
         System.out.println("Weights saved to file " + cf.weightsFile);
      }

      if (printTruths) // print out truth table if option is turned on.
      {
         System.out.println();
         System.out.println("Truth Table (format: | In ... In | Out ... Out |)");
         System.out.println(dumpTruthTable());
      }

      if (runAfterTrain || (!train)) // only report run results if program is running after training or running.
      {
         System.out.println("Results from Running:");
         for (int cases = 0; cases < numCases; cases++)
         {
            System.out.print("Case " + (cases + 1) + ": ");
            for (int i = 0; i < numOutNodes; i++)
            {
               System.out.print(outputs[cases][i] + " ");
            }
            System.out.println();
         }
      } // if (runAfterTrain || (!train))

   } // public static void reportResults()

/*
 * prints the configuration (A-B-C) of the network in a pretty-printing format.
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
 * Stores the theta sub j values for the hidden activation layer and calculates psi sub j values and stores them. (used for train).
 * takes the current test case (to reference truth table to calculate psis).
 */
   public static void recalculate(int curCase)
   {
      double thetasubi;
      double omegai;
      double err = 0.0;

      for (int j = 0; j < numHiddenNodes; j++) // updates each node in the hidden layer.
      {
         Thetaj[j] = theta(j);

         hj[j] = act(Thetaj[j]);
      }

      for (int i = 0; i < numOutNodes; i++)
      {
         thetasubi = thetai(i);

         F[i] = act(thetasubi);
         omegai = (expectedOutputs[curCase][i] - F[i]);
         psi[i] = omegai * fprime(thetasubi);
      }

   } // public static void recalculate(int curCase)

/*
 * runs the network and stores the resulting hidden activations and output based on the stored input.
 */
   public static void recalculateForRun()
   {
      for (int j = 0; j < numHiddenNodes; j++) // updates each node in the hidden layer.
      {
         hj[j] = act(theta(j));
      }

      for (int i = 0; i < numOutNodes; i++)
      {
         F[i] = act(thetai(i));
      }
   } // public static void recalculateForRun()



/*
 * calculates the dot product (theta) for node j of the hidden activation layer.
 */
   public static double theta(int j)
   {
      double sum = 0.0;
      for (int k = 0; k < numInputNodes; k++) // calculates the dot product of the input activations and the edges leading to node j.
      {
         sum += ak[k] * w1kj[k][j];
      }
      return sum;
   } // public static double theta(int j)

/*
 * calculates the dot product (theta) for node i of the output activation layer.
 */
   public static double thetai(int i)
   {
      double sum = 0.0;
      for (int j = 0; j < numHiddenNodes; j++) // calculates dot product of the hidden activations and the edges leading to node 0.
      {
         sum += hj[j] * w2ji[j][i];
      }
      return sum;
   } // public static double thetai(int i)


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
 * runs the network on a given set of input activations.
 */
   public static void getOutput(double[] inp)
   {
      input(inp);    // inputs the given set of values.
      recalculateForRun(); // runs the network.
   }




/******
 * Training Helper functions
 */

/*
 * calculates the delta w values for the network and stores them in arrays.
 */
   public static void calcDeltas()
   {
      double Omegaj;
      double Psij;
      for (int j = 0; j < numHiddenNodes; j++)
      {
         Omegaj = 0.0;
         for (int i = 0; i < numOutNodes; i++)
         {
            Omegaj += psi[i] * w2ji[j][i];

            w2ji[j][i] += lambda * hj[j] * psi[i];
         }

         Psij = Omegaj * fprime(Thetaj[j]);

         for (int k = 0; k < numInputNodes; k++)
         {
            w1kj[k][j] += lambda * ak[k] * Psij;
         }
      } // for (int j = 0; j < numHiddenNodes; j++)

   } // public static void calcDeltas()



/*
 * Calculates and updates delta w for one training cycle based on the given truth table index.
 * List of operations in one training cycle, sequentially:
 *    Input truth table values
 *    set T (expected output in calcDeltas method) to the truth table output.
 *    run the network
 *    calculate the delta ws.
 *    apply the delta ws.
 *    rerun the network to keep it accurate.
 */
   public static void trainProcedure(int truthtableindex)
   {
      input(givenInputs[truthtableindex]);
      recalculate(truthtableindex); // runs the network.

      calcDeltas();
   } // public static void trainProcedure(int truthtableindex)



/*
 * calculates the average error bound of the network through all the test cases.
 */
   public static double calcError()
   {
      double omegai;
      double ret = 0.0;
      for (int cases = 0; cases < numCases; cases++) // run network for all the cases and calculate the error for each one
      {
         input(givenInputs[cases]);                  // input the truth table inputs for the current index.
         recalculateForRun();                        // run the network
         for (int i = 0; i < numOutNodes; i++)       // loop over outputs and calculate error.
         {
            omegai = expectedOutputs[cases][i] - F[i]; // calculate difference between expected output for this case and actual output.
            ret += 0.5 * omegai * omegai;            // add error for this output to total error.
         }
      }
      ret /= (double) numCases; // Divide by numCases to get average error. (note: cast to double b/c numCases is an int)

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

      for (int j= 0; j < numHiddenNodes; j++) // populate w2ji weights.
      {
         for (int i = 0; i < numOutNodes; i++)
         {
            w2ji[j][i] = Math.random() * range + offset; // random number between minRand and maxRand.
         }
      }
   } // public static void populateRand()



/*
 * populates the weights of the network with the given weights.
 */
   public static void populateSet(double[][] neww1kj, double[][] neww2ji)
   {
      for (int k = 0; k < numInputNodes; k++) // populate w1kj weights.
      {
         for (int j = 0; j < numHiddenNodes; j++)
         {
            w1kj[k][j] = neww1kj[k][j];
         }
      }

      for (int j = 0; j < numHiddenNodes; j++) // populate w2ji weights.
      {
         for (int i = 0; i < numOutNodes; i++)
         {
            w2ji[j][i] = neww2ji[j][i];
         }
      }
   } // public static void populateSet(double[][] neww1kj, double[][] neww2ji)

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
            ret += givenInputs[cases][input] + " "; // adds input to return string.
         }

         ret += "| ";
         for (int output = 0; output < numOutNodes ; output++)
         {
            ret += expectedOutputs[cases][output] + " ";
         }
         ret += "| --> Case " + (cases+1) + "\n"; // adds output and case number label.
      } // for (int cases = 0; cases < numCases; cases++)
      
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

      for (int j = 0; j < numHiddenNodes; j++) // formats w2ji weights into an easily digestible format.
      {
         ret+="| ";
         for (int i = 0; i < numOutNodes; i++)
         {
            ret += "w2" + j + "" + i + ": " + w2ji[j][i] + " | ";
         }
         ret += "\n";
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

      for (int i = 0; i < numOutNodes; i++)
      {
         ret += F[i] + " ";
      }

      return ret;
   } // public static String dumpVals()

} // public class ABC