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
   public int populationMethod;
   public boolean train;
   public double minRand;
   public double maxRand;
   public double avgErrCut;
   public int maxCycles;


   /*
    * initializes an empty configuration parameters object with some default values
    * (default values are lambda = 0.3 and minRand, maxRand = -1.5, 1.5)
    */
   public ConfigParam()
   {
      lambda = 0.3;
      minRand = 1.5;
      maxRand = -1.5;
   }

}
