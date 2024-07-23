/*
 * Author: Luke Zeng
 * Date of Creation: 5/1/2024
 *
 * Description: inverse hyperbolic sine implementation of the activation function f(x).
 *              and its derivative.
 *
 * Table of Contents:
 *    public double funct(double arg)
 *    public double derivative(double arg)
 *    public String toString()
 */

public class Sinhinv implements ActivationFunction
{
   public double a = 0.2;
   /*
    * F(x) = sinh^-1(x) function --> one implementation of activation function.
    */
   public double funct(double arg)
   {
      return Math.log(arg+Math.sqrt(arg*arg + 1))/5;
   }

   /*
    * sinh^-1(x) funct.
    */
   public double derivative(double arg)
   {
      return 1/(5*Math.sqrt(arg*arg+1));
   }

   /*
    * returns a string indicating that this object represents a Leaky Relu activation function.
    */
   public String toString()
   {
      return "Sinh-1";
   }
} //public class Fx implements ActivationFunction

