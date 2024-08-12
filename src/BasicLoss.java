/*
 * represents the basic 1/2 omega squared loss function.
 *
 * Author: Luke Zeng
 * Date of Creation: 8/6/2024
 */
public class BasicLoss implements Loss
{
   public double deriv(double act, double expected)
   {
      return expected-act;
   }

   public double loss(double act, double expected)
   {
      return 0.5 * (expected-act) * (expected-act);
   }
}
