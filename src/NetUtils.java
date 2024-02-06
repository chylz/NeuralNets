/*
 * Author: Luke Zeng
 * Date of Creation: 2/1/2024
 *
 * Description: A class containing the following utilities for training and running a neural network:
 *                 Calculate Delta Ws
 *                 Run the Training procedure for a given input.
 *                 Return f'(x)
 */

public class NetUtils
{
   public ConfigParam cf;
   public NetObject network;
   public double[][] deltawkj; //delta w for activation layer n = 1;
   public double[] deltawj0; //delta w for activation layer n = 2;
   private double psi0;
   private double T0;
   private double deltaEdeltawkj;
   private double deltaEdeltawj0;
   private double PSIj;
   private double OMEGAj;
   private double omega0;





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
    * Returns true if successful
    * Returns false if fail (error)
    */
   public boolean calcDeltas()
   {
      /*
       * below code calculates the omega0 and psi0 values (which are constant no matter the j) for layer n = 3.
       */
      omega0 = T0-network.F0;
      psi0 = omega0*fprime(network.dotProduct(3, 0)); // calculates f'(theta0) (theta of the single output activation).
      for(int j = 0; j<cf.numHiddenNodes; j++)
      {
         /*
          * this code calculates delta wj0.
          */

         deltaEdeltawj0 = -network.hj[j] * psi0;
         deltawj0[j] = -cf.lambda * deltaEdeltawj0;
      }


      for(int k = 0; k<cf.numInputNodes; k++)
      {
         for(int j = 0; j<cf.numHiddenNodes; j++)
         {
            /*
             * this code calculates delta wkj.
             */
            OMEGAj = psi0 * network.w1kj[k][j];
            PSIj = OMEGAj * fprime(network.dotProduct(2, j));
            deltaEdeltawkj = -network.ak[k] * PSIj;
            deltawkj[k][j] = -cf.lambda*deltaEdeltawkj;
         }
      }

      return true;
   } //public boolean calcDeltas()


   /*
    * calculates and updates delta w for one training cycle based on the given truth table index.
    */
   public void trainprocedure(int truthtableindex)
   {
      network.ak = cf.ttInputs[truthtableindex];
      network.recalculate();
      calcDeltas();
      network.applyDeltas(deltawkj, deltawj0);
   }


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
         for(int j = 0; j<cf.numOutNodes; k++)
         {
            network.w1kj[k][j] = Math.random() * range + offset; //random number within 2 numbers.
         }
      }

      for(int j= 0; j<cf.numInputNodes; j++)
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
    * calculates the error bound of the network through all of the
    */
   public double calcError()
   {

   }


} //public class Training

