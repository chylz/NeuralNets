import java.io.*;
/*
 * Runs and Trains a Generative Adversarial Network with the given network configurations.
 * Author: Luke Zeng
 * Date of Creation: 7/31/2024
 */

public class GANEngine
{
   public Config generator;
   public Config discriminator;
   public Network gnet;
   public Network dnet;
   public double[][] noise;
   public double[][] real;

   public String[] inputs;
   public String file;
   public final String OUTNAME = "out";
   public byte[] buff; // I/O buffer

   public Loss loss1 = new LogLoss();
   public Loss loss2 = new LogLoss2();



/*
 * Training parameters: Temporary, so bear with me.
 */
   public int inputsize;
   public int batchsize;
   public int noisesize;
   public int numInputs;
   public int pauseInt;

/*
 * arguments: <generator config> <discriminator config> <real dataset inputs>
 */
   public static void main(String[] args)
   {
      GANEngine g = new GANEngine(args[0], args[1], args[2]);
   }

   public GANEngine(String gen, String disc, String files)
   {
      try
      {
         generator = new Config(gen);
         discriminator = new Config(disc);
         file = files;
      }
      catch (IOException e)
      {
         System.out.println("CONFIG: invalid files, try again");
      }

      if (discriminator.numNodes[0] != generator.numNodes[generator.numNodes.length])
      {
         System.out.println("WARNING: discriminator and generator outputs don't match in size");
      }


   }

/*
 * trains the network, given certain number of iterations max.
 */
   public void train(int maxIt)
   {
      int it = 0;
      while (it <= maxIt)
      {

         it++;
      }
   }

/*
 * runs a single iteration of stochastic gradient descent on the network.
 */
   public void iterate()
   {
      for (int it = 0; it < batchsize; it++) // calculate stochastic minibatch gradient for ONE iteration.
      {
/*
 * generated cases
 */
         gnet.input(noise[it]);
         gnet.recalculateGen();

         dnet.input(gnet.getOutput());
         dnet.recalculateDisc(0.0, loss1);

         dnet.calcDiscDeltas();
         gnet.calcGenDeltas();

/*
 * real cases
 */
         dnet.input(real[it]);
         dnet.recalculateDisc(1.0, loss2);
         dnet.calcDiscDeltas();


      } // for (int it = 0; it < batchsize; it++)

      dnet.applyDeltas(); // push weight changes.
      gnet.applyDeltas();
   } // public void train()

/*
 * editable class to set configurations in GAN network (to be upgraded in future to a GUI or something IDK)
 */
   public void setconfigs()
   {
      inputsize = discriminator.numNodes[0];
      batchsize = 10;
      noisesize = generator.numNodes[0];
      numInputs = 0;
      pauseInt = 0;
      discriminator.loss = new LogLoss();
      generator.loss = new LogLoss();
   }

/******
 * Direct Network Interaction
 */
/*
 * links networks' backprops together by connecting omegas.
 */
   public void link()
   {
      gnet.Omeg = dnet.Omeg;

   }

/*
 * sets up networks with custom configs for GAN.
 */
   public void setNetworks()
   {
      dnet.passPsi = true;
      dnet.batchSize = batchsize;
      gnet.batchSize = batchsize;
   }

/*
 * allocate all arrays for the GAN structure.
 */
   public void allocate()
   {
      noise = new double[batchsize][noisesize];
      real = new double[batchsize][inputsize];
      inputs = new String[numInputs];
      dnet = new Network(discriminator);
      gnet = new Network(generator);
   }




/*
 * saturate the noise samples (using java's default random engine)
 */
   public void saturateNoise()
   {
      for (int i = 0; i < batchsize; i++)
      {
         for (int j = 0; j < noisesize; j++)
         {
            noise[i][j] = Math.random();
         }
      }
   }


/*
 * parses strings of file paths of the images to convert into a proper format.
 */
   public void parseFiles() throws IOException
   {
      BufferedReader buffer = new BufferedReader(new FileReader(file));

      for (int i = 0; i < numInputs; i++)
      {
         inputs[i] = buffer.readLine();
      }
   }

/*
 * populates the real data in the minibatch with file data. (use a random image out of all)
 */
   public void populateReal() throws IOException
   {
      DataInputStream da;
      int image;
      for (int i = 0; i < batchsize; i++)
      {
         image = (int)(Math.random() * numInputs); // select an image at random.
         da = new DataInputStream(new FileInputStream(inputs[image]));

         for (int pel = 0; pel < inputsize; pel++)
         {
            real[i][pel] = da.readDouble();
         }
         da.close();
      }
   }

/*
 * converts a given double array back to an image. (extremely temporary, will fix)
 */
   public void out(double[] arr) throws IOException
   {
      File f = new File(OUTNAME+".bin");
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      DataOutputStream da = new DataOutputStream(ba);

      for (int i = 0; i < arr.length; i++)
      {
         da.writeDouble(arr[i]);
      }

      buff = ba.toByteArray();
      da = new DataOutputStream(new FileOutputStream(f));
      da.write(buff);

      File pelbin = new File(OUTNAME + "pels.bin");

      String[] params = {"1", String.valueOf(arr.length), OUTNAME + ".bin", OUTNAME + "pels.bin"}; // command line parameters: only 1 file, length of n, input file, output file.
      convertback.main(params);

      String[] params2 = {"gray", "100", "100", OUTNAME + "pels.bin", OUTNAME + ".bmp"};
   }
}
