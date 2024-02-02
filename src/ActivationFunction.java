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
   public double funct(double arg);
}
