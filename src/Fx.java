/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Sigmoid activation function which can be passed to a neural net.
 */

public class Fx implements ActivationFunction
{
   /*
    * F(x) = x function --> one implementation of activation function.
    */
   public double funct(double arg)
   {
      return arg;//temp value
   }

   /*
    * dummy derivative function (won't actually train).
    */
   public double derivative(double arg)
   {
      return 1;
   }
} //public class Fx implements ActivationFunction

