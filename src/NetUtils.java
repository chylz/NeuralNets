/*
 * Author: Luke Zeng
 * Date of Creation: 2/1/2024
 *
 * Description: A class containing the following utilities for training and populating a neural network:
 *                 Calculate Delta Ws
 *                 Run the Training procedure for a given truth table index.
 *                 Calculate f'(x)
 *                 Populate network with random values.
 *                 Populate network with given values
 *                 Calculate average error of the network.
 */
public class NetUtils
{
   public ConfigParam cf;
   public NetObject network;
   public double[][] deltawkj; //delta w for activation layer n = 1;
   public double[] deltawj0; //delta w for activation layer n = 2;
   public double psi0;
   public double T0;
   public double partialwkj;
   public double partialwj0;
   public double[] PSI;
   public double[] OMEGA;
   public double omega0;





   /*
    * Constructs a new Training object with given configuration parameters and network container.
    */
   public NetUtils(ConfigParam c1, NetObject net)
   {
      cf = c1;
      network = net;
      deltawkj = new double[cf.numInputNodes][cf.numHiddenNodes];
      deltawj0 = new double[cf.numHiddenNodes];
   }



   /*
    * Calculates the delta w values for the network and stores them in arrays.
    */
   public void calcDeltas()
   {
      /*
       * below code calculates the omega0 and psi0 values (which are constant no matter the j) for layer n = 3.
       */
      omega0 = T0-network.F0;
      psi0 = omega0 * fprime(network.THETA(3, 0)); // fprime calculates f'(theta0) (theta of the single output activation).


      for(int j = 0; j<cf.numHiddenNodes; j++)
      {
         partialwj0 = -network.hj[j] * psi0;
         deltawj0[j] = -cf.lambda * partialwj0;
      }

      //calculate omega and psi.
      for(int j = 0; j<cf.numHiddenNodes; j++)
      {
         OMEGA[j] = psi0 * network.w2j0[j];
         //System.out.println(OMEGA[j]);
         PSI[j] = OMEGA[j] * fprime(network.THETA(2, j));

      }

      for(int j = 0; j<cf.numHiddenNodes; j++)
      {
         for(int k = 0; k<cf.numInputNodes; k++)
         {
            partialwkj = -network.ak[k] * PSI[j];
            deltawkj[k][j] = -cf.lambda*partialwkj;
         }
      } //for(int k = 0; k<cf.numInputNodes; k++)

   } //public boolean calcDeltas()


   /*
    * calculates and updates delta w for one training cycle based on the given truth table index.
    */
   public void trainprocedure(int truthtableindex)
   {
      network.input(cf.givenInputs[truthtableindex]);
      T0 = cf.ExpectedOutputs[truthtableindex];
      network.recalculate();

      calcDeltas();


      network.applyDeltas(deltawkj, deltawj0);
      network.recalculate();
   } //public void trainprocedure(int truthtableindex)



   /*
    * Calculates f'(x) for the given value.
    */
   public double fprime(double x)
   {
      return network.f.derivative(x); // (f(x)*(1-f(x)) --> f prime with a sigmoid function.
   }


   /*
    * populates the weights of the network with random values within the range of the configuration parameters. 
    */
   public void populateRand()
   {
      double range = cf.maxRand-cf.minRand;
      double offset = cf.minRand;
      for(int k = 0; k<cf.numInputNodes; k++)
      {
         for(int j = 0; j<cf.numHiddenNodes; j++)
         {
            network.w1kj[k][j] = Math.random() * range + offset; //random number within 2 numbers.
         }
      }

      for(int j= 0; j<cf.numHiddenNodes; j++)
      {
         network.w2j0[j] = Math.random() * range + offset;
      }
   } //public void populateRand()



   /*
    * populates the weights of the network with given values of w.
    */
   public void populateSet(double[][] neww1kj, double[] neww2j0)
   {
      network.setNetwork(neww1kj, neww2j0);
   }

   /*
    * calculates the error bound of the network through all the test cases.
    */
   public double calcError()
   {
      double ret = 0.0;
      for(int cases = 0; cases<cf.numCases; cases++)
      {
         network.input(cf.givenInputs[cases]);
         network.recalculate();
         omega0 = cf.ExpectedOutputs[cases]-network.F0;
         //System.out.println(network.F0);
         ret+=0.5 * omega0*omega0;
      }
      ret/=(double)cf.numCases;

      return ret;
   } //public double calcError()


} //public class Training

