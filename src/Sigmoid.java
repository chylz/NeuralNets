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
      return 0.0;//temp value
   }

   /*
    * Returns f'(x)
    */
   public double derivative(double arg)
   {
      return funct(arg) * (1-funct(arg)); // sigmoid derivative.
   }
} //public class Sigmoid implements ActivationFunction
