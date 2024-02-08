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
   public static double[][] setw1kj;
   public static double[] setw2j0;



   public static void main(String[] args)
   {
      setConfig();
      allocate();
      populate();
      echoConfig();
      if(cf.train)
         train();
      else
         run();
      run();


   }//public static void main(String[] args)


   /*
    * train the network with gradient descent learning, then print out the weights obtained by training.
    */
   public static void train()
   {
      boolean done = false;
      int cases;
      int curiteration = 0;
      double curerror;
      System.out.println(network.dumpWeights());
      while(!done)
      {
         for(cases = 0; cases<cf.numCases; cases++)
         {
            nu.trainprocedure(cases); //sets inputs, runs network, calculates delta W, and applies delta W for a given case of the truth table
         }
         curerror = nu.calcError();

         //System.out.println(curerror);
         if(curerror<=cf.avgErrCut)
         {
            System.out.println("Ended training for: met error threshold.");
            done = true;
         }
         if(++curiteration >= cf.maxCycles)
         {
            System.out.println("Ended training for: maximum iterations exceeded.");
            done = true;
         }
      }
      System.out.println("Iterations: " + curiteration);
      System.out.println("Final Weights: \n" + network.dumpWeights());
   } //public void train()



   /*
    * run the network and ouput the result.
    */
   public static void run()
   {
      for(int i = 0; i<4; i++)
      {

         System.out.println(network.getOutput(cf.givenInputs[i]));
         //System.out.println(network.dumpVals());
      }
   }



   /*
    * allocates the arrays for the network.
    */
   public static void allocate()
   {
      network = new NetObject(cf.numInputNodes, cf.numHiddenNodes, new Sigmoid());
      nu = new NetUtils(cf, network);


      cf.givenInputs = new double[cf.numCases][cf.numInputNodes];
      cf.ExpectedOutputs = new double[cf.numCases];


      setw1kj = new double[cf.numInputNodes][cf.numHiddenNodes];
      setw2j0 = new double[cf.numHiddenNodes];

      nu.PSI = new double[cf.numHiddenNodes];
      nu.OMEGA = new double[cf.numHiddenNodes];
   }



   /*
    * populates the arrays of the network based on the configuration parameters.
    */
   public static void populate()
   {
      if(cf.populationMethod == 1)
      {
         double[][] tempset1 = {{2.0, 1.0}, {2.0, 1.0}};//temporary matrix used to set setw1kj to desired constants.
         double[] tempset2 = {2.0, 1.0}; // temporary array used to set setw2j0 to desired constants.

         for(int k = 0; k<cf.numInputNodes; k++)
         {
            for(int j = 0; j<cf.numHiddenNodes; j++)
            {
               setw1kj[k][j] = tempset1[k][j];
            }
         }
         for(int j = 0; j<cf.numHiddenNodes; j++)
         {
            setw2j0[j] = tempset2[j];
         }
      } //if(cf.populationMethod == 1)


      switch(cf.populationMethod)
      {
         case 0:
            nu.populateRand();
            break;
         case 1:

            nu.populateSet(setw1kj, setw2j0);
            break;
      } //switch(cf.populationMethod)

      double[][] ttsetin = {{0.0, 0.0}, {0.0, 1.0}, {1.0, 0.0}, {1.0, 1.0}};//temporary array to set truth table inputs.
      double[] ttsetout = {0.0, 1.0, 1.0, 0.0}; //temporary array to set truth table outputs.


      for(int cases = 0; cases<cf.numCases; cases++)
      {
         for(int k = 0; k<cf.numInputNodes; k++)
         {
            cf.givenInputs[cases][k] = ttsetin[cases][k];
         }
         cf.ExpectedOutputs[cases] = ttsetout[cases];
      }
   } //public void populate()



   /*
    * sets the configuration parameters of the network.
    */
   public static void setConfig()
   {
      cf = new ConfigParam();
      cf.maxCycles = 100000;
      cf.avgErrCut = 0.0002;
      cf.minRand = -1.5;
      cf.maxRand = 1.5;
      cf.lambda = 0.3;
      cf.populationMethod = 0;//0 is random, 1 is set population.
      cf.train = true;
      cf.numInputNodes = 2;
      cf.numHiddenNodes = 2;
      cf.numOutNodes = 1;
      cf.numCases = 4; //reflects the number of cases put into the input and output of truth table



   } //public static void setConfig()


   /*
    * prints the configuration parameters to the terminal.
    */
   public static void echoConfig()
   {
      System.out.println(cf);
   }
}