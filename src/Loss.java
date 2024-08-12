/*
 * used to make loss function calculation in the Neural Network more customizable
 */
public interface Loss
{
   public double deriv(double act, double expected);
   public double loss(double act, double expected);
}
