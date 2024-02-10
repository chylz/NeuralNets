/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: Interface that can be passed to a neural network to customize the activation function.
 */

public interface ActivationFunction
{
/*
 * activation function, to be implemented by implementing classes.
 */
   double funct(double arg);


/*
 * derivative of the activation function, to be implemented by implementing classes.
 */
   double derivative(double arg);


/*
 * returns a string indicating the type of activation function implementing classes are.
 */
   String toString();
}
