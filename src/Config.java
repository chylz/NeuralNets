import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Author: Luke Zeng
 * Date of Creation: 3/8/2024
 *
 * Handles all the File IO for the network (configuration, weights, truth table)
 * Implements an interpreter for LOML (Luke's Obvious Minimal Language) bytecode.
 */


public class Config
{

/*
 * file IO vars.
 */
   public String config;        // configuration file name.
   public DataInputStream inp;  // file reader
   public boolean loaded;
   public boolean validfile;

/*
 * file parameters vars.
 */
   public double lambda;         // step size multiplier for gradient descent.
   public int numInputNodes;
   public int numHiddenNodes;
   public int numOutNodes;       // number of output nodes.
   public int populationMethod;  // population method --> 0: randomize weights | 1: manually set weights | 2: File IO
   public boolean train;         // if true, train. if false, run.
   public double minRand;        // minimum random threshold
   public double maxRand;        // maximum random threshold.
   public double avgErrCut;      // average error cutoff (error threshold).
   public int maxIterations;     // maximum iterations before cutoff.
   public boolean printWeight;   // flag to indicate whether to print weights before running.
   public boolean printTruths;   // flag to indicate whether to print the truth table.
   public boolean runAfterTrain; // indicates whether the network should run after training.
   public boolean saveWeights;   // indicates to save weights to the file named FILENAME.
   public int numCases;          // number of cases in the truth table.
   public String weightsFile;    // File for the weights of the network.
   public String truthtable;     // File for the truth table of the network.
   public String actfunct;       // activation function name (lowercase)
   public InputValidation valid;


/*
 * Constructs a configuration handler with the given config file to read from and loads the configuration.
 */
   public Config(String Filename) throws IOException
   {
      config = Filename;
      inp = new DataInputStream(new FileInputStream(config));
      valid = new InputValidation();
      validfile = true;
      loadConfig();
      if(!validfile)
      {
         System.out.println("booboofile"); // REPLACE THIS WITH AN ACTUAL ERROR REPORTING.
      }


   }

/*
 * reads the given file in and stores the configurations for that file (after this, cannot load any more
 * configurations into this instance of the object to prevent concurrent modification).
 */
   public boolean loadConfig()
   {
      boolean ret = true;
      boolean done = true;
      if(loaded)
      {
         ret = false;
      }
      int curCommand = 0;
      while(!done && !loaded && validfile) // small interpreter to interpret the input file into the config params.
      {
         try
         {
            int stringLength; // temporary variable to read string length from commands. .

            switch (inp.readInt()) // for each case the int represents a compiled tag for each parameter in config file.
            {
               case 0:
                  done = true;
               case 1: // indicate that current section is 1 (config)
                  curCommand = 1;
               case 2: // indicate number of input nodes
                  numInputNodes = inp.readInt();
                  valid.numInputDefined = true;
               case 3: // command to set number of output nodes
                  numOutNodes = inp.readInt();
                  valid.numOutDefined = true;
               case 4: // command to set population method.
                  populationMethod = inp.readInt();
                  valid.popMethodDefined = true;
               case 5: // command to set train method.
                  train = inp.readInt() == 1; // Syntax for compiled .bin in configuration file is 1 for train is true.
                  valid.trainDefined = true;
               case 6: // command to set minimum random value.
                  minRand = inp.readDouble();
                  valid.minRandDefined = true;
               case 7: // command to set minimum random value. 
                  maxRand = inp.readDouble();
                  valid.maxRandDefined = true;
               case 8: // command to set average error cutoff. 
                  avgErrCut = inp.readDouble();
                  valid.avgErrCutDefined = true;
               case 9: // command to set maximum iterations. 
                  maxIterations = inp.readInt();
                  valid.maxIterationsDefined = true;
               case 10: // command to set value of printweight. 
                  printWeight = inp.readInt() == 1; // Syntax for compiled .bin in configuration file is 1 for when param is true.
                  valid.printWeightDefined = true;
               case 11: // command to set printTruths
                  printTruths = inp.readInt() == 1; // Syntax for compiled .bin in configuration file is 1 for when param is true.
                  valid.printTruthsDefined = true;
               case 12: // command to set runAfterTrain
                  runAfterTrain = inp.readInt() == 1; // Syntax for compiled .bin in configuration file is 1 for when param is true.
                  valid.runAfterDefined = true;
               case 13: // command to save Weights
                  saveWeights = inp.readInt() == 1; // Syntax for compiled .bin in configuration file is 1 for when param is true.
                  valid.saveWeightDefined = true;
               case 14: // command to set activation function
                  actfunct = ""; // length of the weights file name.
                  stringLength = inp.readInt(); // length of the activation function string name.
                  for (int index = 0; index < stringLength; index++)
                  {
                     actfunct+=inp.readChar();
                  }
                  valid.actfunctDefined = true;
               case 15: // command to set numcases.
                  numCases = inp.readInt();
                  valid.numCasesDefined = true;
               case 16: // command to read lambda.
                  lambda = inp.readDouble();
                  valid.lambdaDefined = true;
               case 17:
                  numHiddenNodes = inp.readInt();
                  valid.numHiddenDefined = true;
               case 18:
                  curCommand = 18;
               case 19:
                  stringLength = inp.readInt(); // length of the weights file name.

                  weightsFile = "";
                  for (int index = 0; index < stringLength; index++)
                  {
                     weightsFile+=inp.readChar();
                  }
                  valid.weightsFileDefined = true;
               case 20:
                  curCommand = 20;
               case 21:
                  stringLength = inp.readInt(); // length of the weights file name.

                  truthtable = "";
                  for (int index = 0; index < stringLength; index++)
                  {
                     truthtable+=inp.readChar();
                  }
                  valid.truthtableDefined = true;
            } //switch (inp.readInt())
         } // try
         catch (IOException e)
         {
            validfile = false;
         }
      } // while(!done && !loaded && validfile)


      loaded = true;
      return ret;
   } // public boolean loadConfig()

} // public class ConfigHandler
