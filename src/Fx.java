/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Linear activation function which can be passed to a neural net.
 */

public class Fx implements ActivationFunction
{
/*
 * F(x) = x function --> one implementation of activation function.
 */
   public double funct(double arg)
   {
      return arg;
   }

/*
 * dummy derivative function (won't actually train).
 */
   public double derivative(double arg)
   {
      return 1;
   }

/*
 * returns a string indicating that this object represents a linear activation function.
 */
   public String toString()
   {
      return "Linear";
   }
} //public class Fx implements ActivationFunction

