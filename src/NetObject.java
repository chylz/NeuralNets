/*
 * Author: Luke Zeng
 * Date of Creation: 1/24/2024
 *
 * Description: Object stores an A-B-1 Neural Network.
 */
//CHANGE MAGIC NUMBER FOR 2D OUTPUT WEIGHTS ARRAY

public class NetObject
{
   public final int inputNodes; // number of input nodes
   public final int hiddenLayerNodes; //number of hidden layer nodes.
   public final int outputNodes; //number of output Nodes.
   public double[][] w1kj; // edge weights between layers a and h.
   public double[] w2j0; // edge weights between layers h and F0
   public double F0; //stores the single output activation.
   public double[] ak; //stores the input activations
   public double[] hj; //stores the single hidden activation node values.
   public ActivationFunction f; // activation function f(x)


   /*
    * Initializes an empty neural network object with no values for any fields (complete manipulation done by user)
    */
   public NetObject()
   {
      inputNodes = 0;
      hiddenLayerNodes = 0;
      outputNodes = 1;
   }


   /*
    * Initializes an neural network object with edge weights 0 and given dimension
    * specification, activation function, and truth table for the specific network for training purposes.
    *
    * Parameters A and B describe the number of input and hidden activation layers respectively.
    * Parameter f1 is the activation function f(x).
    * Parameter In is the inputs of the given truth table.
    * Parameter expOut is the expected outputs of the truth table.
    * Parameter numcases is the number of cases in the Truth Table.
    */
   public NetObject(int A, int B, ActivationFunction f1, double[][] In, double[] expOut, int numcases)
   {
      inputNodes = A;
      hiddenLayerNodes = B;
      outputNodes = 1;
      f = f1;
      w1kj = new double[inputNodes][hiddenLayerNodes];
      w2j0 = new double[hiddenLayerNodes];
      ak = new double[inputNodes];
      hj = new double[inputNodes];
   } //public NetObject(int A, int B, ActivationFunction f1, double[][] In, double[] expOut, int numcases)



   /*
    * Initializes a neural network object with given edge weights, dimensions, and activation function.
    *
    * Parameters A and B describe the number of input and hidden activation layers respectively.
    * Parameter n1 represents the weights for activation layer n=1.--> format: n1[k][j]
    * Parameter n2 represents the weights for activation layer n=2.--> format: n2[j]
    * Parameter f1 is the activation function f(x).
    */
   public NetObject(int A, int B, double[][] n1, double[] n2, ActivationFunction f1)
   {
      inputNodes = A;
      hiddenLayerNodes = B;
      outputNodes = 1;
      f = f1;
      w1kj = new double[inputNodes][hiddenLayerNodes];
      w2j0 = new double[hiddenLayerNodes];
      ak = new double[inputNodes];
      hj = new double[inputNodes];

      setNetwork(n1, n2);
   } //public NetObject(int A, int B, double[][] n1, double[] n2)






   /*
    * Activation function of the network --> f(val)
    */
   public double act(double val)
   {
      return f.funct(val);
   }


   /*
    * recalculates the entire network
    */
   public void recalculate()
   {
      for(int j = 0; j<hiddenLayerNodes; j++)//activation layer 1
      {
         hj[j] = act(dotProduct(2, j));
      }

      F0 = act(dotProduct(3, 0));
   }//public void recalculate()



   /*
    * calculates the dot product in layer n for node a
    * n=2 refers to hidden layer.
    * n=3 refers to output activation layer.
    */
   public double dotProduct(int n, int a)
   {
      double sum = 0;
      if(n == 2)
      {
         for(int k = 0; k<inputNodes; k++)
         {
            sum = sum + ak[k]*w1kj[k][a];
         }
      }
      if(n == 3)
      {
         for (int j = 0; j < hiddenLayerNodes; j++)
         {
            sum = sum + hj[j] * w1kj[j][a];
         }
      }
      return sum;
   } //public double dotProduct(int n, int a)



   /*
    * takes in  an input activation layer and sets the current input to the given input.
    */
   public void input(double[] a)
   {
      for(int k = 0; k<inputNodes; k++)
      {
         ak[k] = a[k];
      }
   }



   /*
    * returns all the edge weights as a string.
    */
   public String dumpWeights()
   {
      String ret = "";

      for(int k = 0; k<inputNodes; k++)
      {
         for(int j = 0; j<hiddenLayerNodes; j++)
         {
            ret+=w1kj[k][j] + " ";
         }
         ret+="\n";
      }

      ret+="\n";

      for(int j = 0; j<hiddenLayerNodes; j++)
      {
         ret+=w2j0[j];
      }

      return ret;
   } //public String dumpWeights()



   /*
    * applies the given delta w values to the network.
    */
   public void applyDeltas(double[][] deltaw1kj, double[] deltaw2j0)
   {
      for(int k = 0; k<inputNodes; k++)
      {
         for(int j = 0; j<hiddenLayerNodes; j++)
         {
            w1kj[k][j] += deltaw1kj[k][j];
         }
      }


      for(int j = 0; j<hiddenLayerNodes; j++)
      {
         w2j0[j] = deltaw2j0[j];
      }
   } //public boolean applyDeltas(int[][] deltaw1kj, int[][] deltaw2j0)




   /*
    * set the weight of an edge on activation layer n going from node a to b to w.
    */
   public void setWeight(int n, int a, int b, double w)
   {
      if (n == 1) {
         w1kj[a][b] = w;
      }
      if (n == 2) {
         w2j0[a] = w;
      }
   } //public void setWeight(int n, int a, int b, double w)


   /*
    * sets all the weights in the network to the given input arrays
    */
   public void setNetwork(double[][] n1, double[] n2)
   {
      for (int k = 0; k < inputNodes; k++) //populate first weights
      {
         for (int j = 0; j < hiddenLayerNodes; j++) {
            w1kj[k][j] = n1[k][j];
         }
      }

      for (int j = 0; j < hiddenLayerNodes; j++)
      {
         w2j0[j] = n2[j];
      }
   } //public void setNetwork(double[][] n1, double[] n2)

} //public class NetObject
