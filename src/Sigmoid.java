/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Sigmoid activation function which can be passed to a neural net.
 */

public class Sigmoid implements ActivationFunction
{
   /*
    * Sigmoid function --> one implementation of activation function.
    */
   public double funct(double arg)
   {
      return 1.0/(1.0 + Math.exp(-arg));//sigmoid function
   }

   /*
    * Returns f'(x)
    */
   public double derivative(double arg)
   {
      return this.funct(arg) * (1.0-this.funct(arg)); // sigmoid derivative.
   }
} //public class Sigmoid implements ActivationFunction
