/*
 * Author: Luke Zeng
 * Date of Creation: 2/1/2024
 *
 * Description: Stores all the configuration parameter values for a neural network.
 */
public class ConfigParam
{
   public double lambda;
   public int numInputNodes;
   public int numHiddenNodes;
   public int numOutNodes;
   public int populationMethod;//Population method --> 0: randomize weights | 1: manually set weights
   public boolean train;
   public double minRand;
   public double maxRand;
   public double avgErrCut; // average error cutoff.
   public int maxCycles;

   //Truth Table arrays
   public double[][] ttInputs;
   public double[] ttExpectedOutputs;
   public int ttNumCases;





   /*
    * initializes an empty configuration parameters object.
    */
   public ConfigParam()
   {
      //empty for now.
   }


   /*
    * returns a pretty-printing formatted version of the configuration parameters.
    */
   public String toString()
   {
      return "lambda:" + lambda + " | numInputNodes:" + numInputNodes + " | numOutputNodes:" + numOutNodes +
             " | numHiddenNodes:" + numHiddenNodes + " | populationMethod:" + populationMethod + " | minRand:" + minRand +
             " | maxRand:" + maxRand + " | avgErrCut:" + avgErrCut + " | maxCycles:" + maxCycles + " |";
   }

} //public class ConfigParam
