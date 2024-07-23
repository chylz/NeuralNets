/*
 * Author: Luke Zeng
 * Date of Creation: 5/1/2024
 *
 * Description: LeakyRectified Linear Unit (f(x) = max(x, ax) implementation of the activation function f(x).
 *              and its derivative.
 *
 * Table of Contents:
 *    public double funct(double arg)
 *    public double derivative(double arg)
 *    public String toString()
 */

public class LeakyReLu implements ActivationFunction
{
   public double a = 0.01;
   /*
    * F(x) = max(x, 0) function --> one implementation of activation function.
    */
   public double funct(double arg)
   {
      return Math.max(arg, a*arg);
   }

   /*
    * LeakyReluDerivative funct.
    */
   public double derivative(double arg)
   {
      return (arg >= 0.0) ? 1.0 : a;
   }

   /*
    * returns a string indicating that this object represents a Leaky Relu activation function.
    */
   public String toString()
   {
      return "LeakyReLu";
   }
} //public class Fx implements ActivationFunction

