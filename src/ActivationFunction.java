/*
 * Author: Luke Zeng
 * Date of Creation: 1/30/2024
 *
 * Description: interface that can be passed to NetObject to customize the activation function.
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
}
