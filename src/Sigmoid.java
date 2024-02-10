/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Sigmoid activation function which can be passed to a neural net.
 */

public class Sigmoid implements ActivationFunction
{
/*
 * Sigmoid function --> one implementation of activation function (represents f(x)).
 */
   public double funct(double arg)
   {
      return 1.0/(1.0 + Math.exp(-arg));//sigmoid function
   }

/*
 * Returns f'(x), the derivative of f(x)
 */
   public double derivative(double arg)
   {
      return this.funct(arg) * (1.0-this.funct(arg)); // sigmoid derivative.
   }

/*
 * returns a string indicating that this object represents a sigmoid activation function.
 */
   public String toString()
   {
      return "Sigmoid";
   }
} //public class Sigmoid implements ActivationFunction
