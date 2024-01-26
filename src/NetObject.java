/*
 * Author: Luke Zeng
 * Date of Creation: 1/24/2024
 *
 * Description: Object stores an A-B-1 Neural Network.
 */

public class NetObject {
   private int inputNodes; // number of input nodes
   private int hiddenLayerNodes; //number of hidden layer nodes.
   private int[] a; // Input activations
   private int[] h; // Hidden activations
   private int F0; // output activation

   /*
    * Initializes an empty neural network object with edge weights 0 and given dimension specification
    *
    * Parameters A and B describe the number of input and hidden activation layers respectively.
    */
   public NetObject(int A, int B)
   {
      inputNodes = A;
      hiddenLayerNodes = B;
   }

   /*
    * Initializes a neural network object with given edge weights
    */
   public NetObject(int[] dimensions, int[][] edgeweights)
   {

   }
}
