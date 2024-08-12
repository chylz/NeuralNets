/*
 * logrithmic loss function for the generated data cases.
 */
public class LogLoss implements Loss
{
   public double deriv (double act, double expected)
   {
      return -1/(act*Math.log(10.0));
   }

   public double loss (double act, double expected)
   {
      return Math.log(1-act)/Math.log(10);
   }
}
