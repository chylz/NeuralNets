/*
 * Author: Luke Zeng
 * Date of Creation: 2/1/2024
 *
 * Description: Main class --> Driver class behind the neural network.
 */


public class Main
{
   public static ConfigParam cf;
   public static NetUtils nu;
   public static NetObject network;



   public static void main(String[] args)
   {


   }


   /*
    * train the network with gradient descent learning, then print out the weights obtained by training.
    */
   public void train()
   {
      boolean done = true;
      int curiter = 0;
      double curerror;
      while(!done)
      {
         for(int cases = 0; cases<cf.ttNumCases; cases++)
         {
            nu.trainprocedure(cases);

         }
      }
   }

   /*
    * run the network and ouput the result.
    */
   public void run()
   {

   }

   public void populate()
   {
      switch(cf.populationMethod)
      {
         case 0:
            nu.populateRand();
            break;
         case 1:
            double[][] setw1kj = {{}};
            double[] setw2j0 = {};
            nu.populateSet(setw1kj, setw2j0);
            break;
      }
   } //public void populate()

   /*
    * allocates the memory for the network.
    */
   public static void allocate()
   {
      network = new NetObject(cf.numHiddenNodes, cf.numHiddenNodes, new Sigmoid(), cf.ttInputs, cf.ttExpectedOutputs, cf.ttNumCases);
      nu = new NetUtils(cf, network);
   }



   /*
    * sets the configuration parameters of the network.
    */
   public static void setConfig()
   {
      cf = new ConfigParam();
      cf.maxCycles = 50000;
      cf.avgErrCut = 0.7;
      cf.minRand = -1.5;
      cf.maxRand = 1.5;
      cf.lambda = 0.3;
      cf.populationMethod = 1;
      cf.train = true;
      cf.numInputNodes = 2;
      cf.numHiddenNodes = 2;
      cf.numOutNodes = 1;

      double[][] truthtablesetInputs = {{0.0 , 0.0} , {0.0 , 1.0} , {1.0 , 0.0} , {1.0 , 1.0}};
      double[] truthtablesetOutputs = {0 , 0 , 0 , 1};
      //above 2 arrays must have same length in 1st dimension.

      cf.ttNumCases = 4; //reflects the number of cases put into the input and output of truth table
      cf.ttInputs = truthtablesetInputs;
      cf.ttExpectedOutputs = truthtablesetOutputs;

   } //public static void setConfig()



   /*
    * prints the configuration parameters to the terminal.
    */
   public static void echoConfig()
   {
      System.out.println(cf);
   }
}