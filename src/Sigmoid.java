/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Sigmoid implementation of the activation function f(x).
 *              and its derivative.
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
