/*
 * logrithmic loss function for the real case data in GAN
 */
public class LogLoss2 implements Loss
{
   public double deriv (double act, double expected)
   {
      return 1/(act*Math.log(10.0));
   }

   public double loss (double act, double expected)
   {
      return Math.log(act)/Math.log(10);
   }
}
