import java.io.*;

/*
 * Author: Luke Zeng
 * Date of Creation: 2/23/2024
 *
 * Description: Handles file IO of weights and truth table for the neural network (input and output of weights).
 */

public class FileHandler
{
   public String network;        // network weights file name
   public DataInputStream ninp;  // file reader
   public DataOutputStream nout; // file writer (output)


/*
 * Creates a new file handler object with the given weight file
 */
   public FileHandler(String weights)
   {
      network = weights;
   }

/*
 * changes the weights file to the new one.
 */
   public void changeFile(String weights)
   {
      network = weights;
   }

/*
 * Writes the network weights to the file.
 * Requires the input of the network configuration (A-B-C)
 */
   public void writeWeights(double[][] w1kj, double[][] w2ji, int A, int B, int C)
   {
      try
      {
         nout = new DataOutputStream(new FileOutputStream(network)); // helps with writing to file.
         for (int k = 0; k < A; k++) // write w1kj weights to file.
         {
            for (int j = 0; j < B; j++)
            {
               nout.writeDouble(w1kj[k][j]);
            }
         }

         for (int j = 0; j < B; j++) // write w2ji weights to file.
         {
            for (int i = 0; i < C; i++)
            {
               nout.writeDouble(w2ji[j][i]);
            }
         }
         nout.close();
      }
      catch (IOException e)
      {
         System.out.println("Error in saving weights. Please check file permissions and presence. Program will continue to run until aborted by user");
      }


   } // public void writeWeights(double[][] w1kj, double[][] w2ji, int A, int B, int C) throws IOException

/*
 * Loads the weights into the given weight arrays with given network specifications (A-B-C).
 * returns an array of the network config of the weights.
 * if the end of the reader is reached and there are still values (IE, weights file is invalid)
 * return false.
 * Will throw an error if there are not enough weights, handled in the main network file.
 */
   public boolean loadWeights(double[][] w1kj, double[][] w2ji, int A, int B, int C) throws IOException
   {
      ninp = new DataInputStream((new FileInputStream(network))); // helps with inputting from file.
      boolean tooshort = false; // flag to indicate the file is too short.
      boolean toolong = true;  // flag to see if the file is too long.
      byte test;
      try
      {
         for (int k = 0; k < A; k++)
         {
            for (int j = 0; j < B; j++)
            {
               w1kj[k][j] = ninp.readDouble();
            }
         }

         for (int j = 0; j < B; j++)
         {
            for (int i = 0; i < C; i++)
            {
               w2ji[j][i] = ninp.readDouble();
            }
         }
      }
      catch (EOFException e)
      {
         tooshort = true;
      }

      if(!tooshort) // if return is still true, see if there are more bytes to read.
      {
         try
         {
            test = ninp.readByte(); // test to see if there is anything else left in the file
         }
         catch (EOFException e) // if nothing left in file, the file is just right.
         {
            toolong = false;
         }
      }

      ninp.close();

      return !(tooshort || toolong); // only return true if the number of weights is just right.

   } // public void loadWeights(double[][] w1kj, double[][] w2ji, int A, int B, int C) throws IOException

/*
 * encodes the given truth table into the given binary file, specifying number of inputs, outputs, and number of cases.
 */
   public static void encodeTruths(double[][] inputs, double[][] outputs, int numInputs,
                                   int numOutputs, int numCases, String filename) throws IOException
   {
      DataOutputStream out = new DataOutputStream(new FileOutputStream(filename)); // file writer.

      for (int cases = 0; cases < numCases; cases++)
      {
         for (int input = 0; input < numInputs; input++)
         {
            out.writeDouble(inputs[cases][input]);
         }
      }

      for (int cases = 0; cases < numCases; cases++)
      {
         for (int output = 0; output < numOutputs; output++)
         {
            out.writeDouble(outputs[cases][output]);
         }
      }

      out.close();

   } // public static void encodeTruths(double[][] inputs, double[][] outputs ...

/*
 * takes in the file for a truth table and puts the values in the given truth table array.
 * returns the number of inputs, outputs, and cases in the truth table.
 * Can read inputs only if specified (for run-exclusive to save memory).
 */
   public static void decodeTruths(double[][] inputs, double[][] outputs, String filename,
                                   int numInputs, int numOutputs, int numCases, boolean inonly) throws IOException
   {
      DataInputStream in = new DataInputStream(new FileInputStream(filename)); // file reader.

      for (int cases = 0; cases < numCases; cases++)
      {
         for (int input = 0; input < numInputs; input++)
         {
            inputs[cases][input] = in.readDouble();
         }
      }
      if(inonly)
      {
         for (int cases = 0; cases < numCases; cases++)
         {
            for (int output = 0; output < numOutputs; output++)
            {
               outputs[cases][output] = in.readDouble();
            }
         }
      }


      in.close();
   } // public static void decodeTruths(double[][] inputs, double[][] outputs ...


} // public class FileHandler
