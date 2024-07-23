/*
 * Author: Luke Zeng
 * Date of Creation: 5/1/2024
 *
 * Description: Rectified Linear Unit (f(x) = max(x, 0) implementation of the activation function f(x).
 *              and its derivative.
 *
 * Table of Contents:
 *    public double funct(double arg)
 *    public double derivative(double arg)
 *    public String toString()
 */

public class ReLu implements ActivationFunction
{
   /*
    * F(x) = max(x, 0) function --> one implementation of activation function.
    */
   public double funct(double arg)
   {
      return Math.max(arg, 0.0);
   }

   /*
    * dummy derivative function (won't actually train).
    */
   public double derivative(double arg)
   {
      return (arg >= 0.0) ? 1.0 : 0.0;
   }

   /*
    * returns a string indicating that this object represents a linear activation function.
    */
   public String toString()
   {
      return "ReLu";
   }
} //public class Fx implements ActivationFunction

